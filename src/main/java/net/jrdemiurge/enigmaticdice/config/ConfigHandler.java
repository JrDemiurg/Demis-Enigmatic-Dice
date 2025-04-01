package net.jrdemiurge.enigmaticdice.config;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnigmaticDice.MOD_ID)
public class ConfigHandler {
    @SubscribeEvent
    public static void onServerStarting(ServerAboutToStartEvent event) {
        EnigmaticDiceConfig.loadConfig();
        EnigmaticDice.LOGGER.info("Enigmatic Dice config loading...");
    }
}
