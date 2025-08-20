package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public abstract class RandomEvent {
    private boolean isNegativeEvent = false;
    private boolean isNeutralEvent = false;
    protected int rarity = 2;

    public abstract boolean execute(Level pLevel, Player pPlayer, boolean guaranteed);

    public boolean execute(Level pLevel, Player pPlayer) {
        return execute(pLevel, pPlayer, false);
    }

    public boolean simulationExecute(Level pLevel, Player pPlayer) {
        return rollChance(pLevel, pPlayer, rarity);
    }

    public boolean rollChance(Level pLevel, Player pPlayer, int rarity) {
        float luck = pPlayer.getLuck();
        RandomSource random = pLevel.getRandom();

        if (isNeutralEvent) {
            return random.nextInt(rarity) == 0;
        }

        int attempts = 1; // базовая попытка

        if (!isNegativeEvent && luck > 0) {
            for (int i = 0; i < (int) luck; i++) {
                if (random.nextFloat() < 0.1f) { // 10% шанс за каждое очко
                    attempts++;
                }
            }
        } else if (isNegativeEvent && luck < 0) {
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


    public boolean isNegativeEvent() {
        return isNegativeEvent;
    }

    public void setNegativeEvent(boolean negativeEvent) {
        isNegativeEvent = negativeEvent;
    }

    public boolean isNeutralEvent() {
        return isNeutralEvent;
    }

    public void setNeutralEvent(boolean neutralEvent) {
        isNeutralEvent = neutralEvent;
    }
}
