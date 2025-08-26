package net.jrdemiurge.enigmaticdice.item.custom.crucibleofrile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Objects;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public final class ClientLookController {
    private static UUID currentTargetUuid = null;
    private static int ticksLeft = 0;
    private static boolean active = false;

    // настройки плавности
    private static final float MAX_YAW_SPEED_DEG_PER_SEC = 720f;
    private static final float MAX_PITCH_SPEED_DEG_PER_SEC = 720f;

    public static void startOrExtend(UUID target, int durationTicks) {
        // Если уже смотрим на этого же — просто продлеваем
        if (active && Objects.equals(currentTargetUuid, target)) {
            ticksLeft = Math.max(ticksLeft, durationTicks);
        } else {
            currentTargetUuid = target;
            ticksLeft = Math.max(1, durationTicks);
            active = true;
        }
    }

    public static void stop() {
        active = false;
        currentTargetUuid = null;
        ticksLeft = 0;
    }

    /** вызывать КАЖДЫЙ КАДР (например в ViewportEvent.ComputeCameraAngles) */
    public static void onRenderFrame(float partialTick) {
        if (!active) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) return;

        // цель — игрок (по UUID)
        Player target = mc.level.getPlayerByUUID(currentTargetUuid);
        if (target == null || !target.isAlive()) return;

        // вычисляем желаемые углы к глазам цели (с интерполяцией partialTick)
        Vec3 eye = player.getEyePosition(partialTick);
        Vec3 tEye = target.getEyePosition(partialTick);
        Vec3 dir = tEye.subtract(eye);

        double dx = dir.x;
        double dy = dir.y;
        double dz = dir.z;
        double horiz = Math.sqrt(dx * dx + dz * dz);

        float desiredYaw = (float)(Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;
        float desiredPitch = (float)(-(Mth.atan2(dy, horiz) * (180.0 / Math.PI)));
        desiredYaw = Mth.wrapDegrees(desiredYaw);
        desiredPitch = Mth.clamp(desiredPitch, -90.0F, 90.0F);

        float dt = mc.getDeltaFrameTime(); // секунды на кадр
        float maxYawStep = MAX_YAW_SPEED_DEG_PER_SEC * dt;
        float maxPitchStep = MAX_PITCH_SPEED_DEG_PER_SEC * dt;

        float newYaw = Mth.approachDegrees(player.getYRot(), desiredYaw, maxYawStep);
        float newPitch = Mth.approachDegrees(player.getXRot(), desiredPitch, maxPitchStep);

        player.setYRot(newYaw);
        player.setXRot(newPitch);
        player.setYHeadRot(newYaw);
        player.yBodyRot = newYaw;
    }

    /** вызывать раз в клиентский тик (END) */
    public static void onClientTickEnd() {
        if (!active) return;

        if (--ticksLeft <= 0) {
            stop();
        }
    }

    public static boolean isActive() { return active; }
}
