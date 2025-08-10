package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public abstract class RandomEvent {
    private boolean isPositiveEvent = true;

    public abstract boolean execute(Level pLevel, Player pPlayer, boolean guaranteed);

    public boolean execute(Level pLevel, Player pPlayer) {
        return execute(pLevel, pPlayer, false);
    }

    public abstract boolean simulationExecute(Level pLevel, Player pPlayer);

    public static boolean rollChance(Level pLevel, Player pPlayer, int rarity, boolean isPositiveEvent) {
        float luck = pPlayer.getLuck();
        RandomSource random = pLevel.getRandom();

        int attempts = 1; // базовая попытка

        if (isPositiveEvent && luck > 0) {
            for (int i = 0; i < (int) luck; i++) {
                if (random.nextFloat() < 0.1f) { // 10% шанс за каждое очко
                    attempts++;
                }
            }
        } else if (!isPositiveEvent && luck < 0) {
            for (int i = 0; i < (int) -luck; i++) {
                if (random.nextFloat() < 0.1f) { // 10% шанс за каждое очко
                    attempts++;
                }
            }
        }

        for (int i = 0; i < attempts; i++) {
            if (random.nextInt(rarity) == 0) {
                return true;
            }
        }

        return false;
    }


    public boolean isPositiveEvent() {
        return isPositiveEvent;
    }

    public void setPositiveEvent(boolean positiveEvent) {
        isPositiveEvent = positiveEvent;
    }
}
