package net.jrdemiurge.enigmaticdice.event;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.custom.Permafrost;
import net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie.DayOfInvisibility;
import net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie.DayOfStrength;
import net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie.DayOfTag;
import net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie.DayOnTheMoon;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = EnigmaticDice.MOD_ID)
public class PlayerLogoutHandler {

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        UUID playerId = player.getUUID();

        Permafrost.stackMap.remove(playerId);
        Permafrost.auraApplied.remove(playerId);

        DayOfInvisibility.activePlayers.remove(playerId);
        DayOfTag.activePlayers.remove(playerId);
        DayOfStrength.activePlayers.remove(playerId);
        DayOnTheMoon.activePlayers.remove(playerId);
    }
}
