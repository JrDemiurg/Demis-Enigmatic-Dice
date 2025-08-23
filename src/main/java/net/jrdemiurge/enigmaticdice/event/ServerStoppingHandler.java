package net.jrdemiurge.enigmaticdice.event;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie.AccelerateDayCycleEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnigmaticDice.MOD_ID)
public class ServerStoppingHandler {

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        AccelerateDayCycleEvent.active = false;
        EnigmaticDice.eventManager = null;
    }
}
