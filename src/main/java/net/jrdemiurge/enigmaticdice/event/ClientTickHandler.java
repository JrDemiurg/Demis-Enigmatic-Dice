package net.jrdemiurge.enigmaticdice.event;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.ModItems;
import net.jrdemiurge.enigmaticdice.network.DoubleJumpPacket;
import net.jrdemiurge.enigmaticdice.network.NetworkHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = EnigmaticDice.MOD_ID, value = Dist.CLIENT)
public class ClientTickHandler {

    private static final int DOUBLE_JUMP_THRESHOLD_TICKS = 6;

    private static final WeakHashMap<Player, Long> lastJumpPressTick = new WeakHashMap<>();
    private static final WeakHashMap<Player, Boolean> wasJumping = new WeakHashMap<>();
    private static final WeakHashMap<Player, Boolean> lastTickIsJumping = new WeakHashMap<>();

    @SubscribeEvent
    public static void onClientTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof LocalPlayer player)) return;

        if (!isWearingGravityCore(player)) return;

        if (player.onGround()) {
            wasJumping.put(player, false);
        }

        if (!player.input.jumping) {
            lastTickIsJumping.put(player, false);
            return;
        }

        if (player.input.jumping
                && !lastTickIsJumping.getOrDefault(player, false)
                && !wasJumping.getOrDefault(player, false)) {
            long lastTick = lastJumpPressTick.getOrDefault(player, -100L);
            long currentTick = player.level().getGameTime();
            lastJumpPressTick.put(player, currentTick);
            lastTickIsJumping.put(player, true);
            if (currentTick - lastTick < DOUBLE_JUMP_THRESHOLD_TICKS) {
                NetworkHandler.INSTANCE.sendToServer(new DoubleJumpPacket());
                wasJumping.put(player, true);
                lastJumpPressTick.put(player, -100L);
            }
        }
    }

    private static boolean isWearingGravityCore(Player player) {
        return CuriosApi.getCuriosInventory(player)
                .map(handler -> !handler.findCurios(ModItems.GRAVITY_CORE.get()).isEmpty())
                .orElse(false);
    }
}
