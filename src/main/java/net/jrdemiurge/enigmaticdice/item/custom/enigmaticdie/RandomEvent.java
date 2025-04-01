package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface RandomEvent {

    boolean execute(Level pLevel, Player pPlayer, boolean guaranteed);

    default boolean execute(Level pLevel, Player pPlayer) {
        return execute(pLevel, pPlayer, false);
    }

    boolean simulationExecute(Level pLevel, Player pPlayer);

    static boolean rollChance(Level pLevel, Player pPlayer, int rarity) {
        int attempts = 1 + Math.max(0, (int) pPlayer.getLuck());
        for (int i = 0; i < attempts; i++) {
            if (pLevel.getRandom().nextInt(rarity) == 0) {
                return true;
            }
        }
        return false;
    }
}
