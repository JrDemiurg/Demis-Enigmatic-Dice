package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface RandomEvent {
    int[] RARITY_CHANCES = {2, 5, 20, 100, 1000}; // 50%, 20%, 5%, 1%, 0.1%
    // 100% обычная - белый
    // 20% редкий - синий
    // 4% эпический - фиолетовый
    // 1% легендарный - оранжевый
    // 0,1% мифический - скорее всего берюзовый или голубой
    // у баффов розовый
    // у отрицательных исходов красный

    boolean execute(Level pLevel, Player pPlayer, boolean guaranteed);

    // Перегруженный метод по умолчанию вызывается с guaranteed = false
    default boolean execute(Level pLevel, Player pPlayer) {
        return execute(pLevel, pPlayer, false);
    }

    boolean simulationExecute(Level pLevel, Player pPlayer);

    static boolean rollChance(Level pLevel, Player pPlayer, int rarity) {
        int attempts = 1 + Math.max(0, (int) pPlayer.getLuck());
        for (int i = 0; i < attempts; i++) {
            if (pLevel.getRandom().nextInt(RARITY_CHANCES[rarity]) == 0) {
                return true;
            }
        }
        return false;
    }
}
