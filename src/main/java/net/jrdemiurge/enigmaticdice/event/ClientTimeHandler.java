package net.jrdemiurge.enigmaticdice.event;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnigmaticDice.MOD_ID, value = Dist.CLIENT)
public class ClientTimeHandler {

    private static boolean active = false;
    private static int multiplier = 1;
    private static int ticksLeft = 0;

    public static void start(int mul, int duration) {
        active = true;
        multiplier = mul - 1;
        ticksLeft = duration;
    }
    public static void stop() { active = false; }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (event.phase != TickEvent.Phase.END || !active || mc.level == null) return;
        if (mc.isPaused()) return;

        long t = mc.level.getDayTime();
        mc.level.setDayTime(t + multiplier);

        if (--ticksLeft <= 0) stop();
    }
}
