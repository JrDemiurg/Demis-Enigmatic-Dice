package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class AncientDebrisCageEvent implements RandomEvent {
    private final int rarity;

    public AncientDebrisCageEvent(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean  execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!RandomEvent.rollChance(pLevel, pPlayer, rarity)) return false;
        }

        BlockPos playerPos = pPlayer.blockPosition();
        BlockState debris = Blocks.ANCIENT_DEBRIS.defaultBlockState();

        // Координаты блока вокруг игрока
        int[][] offsets = {
                {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}, // Боковые блоки
                {1, 1, 0}, {-1, 1, 0}, {0, 1, 1}, {0, 1, -1},
                {0, 2, 0}
        };

        for (int[] offset : offsets) {
            BlockPos pos = playerPos.offset(offset[0], offset[1], offset[2]);
            pLevel.setBlock(pos, debris, 3);
        }

        MutableComponent message = Component.literal("Now you are safe.")
                .withStyle(getColorForRarity(rarity));
        pPlayer.displayClientMessage(message, false);
        return true;
    }

    private ChatFormatting getColorForRarity(int rarity) {
        return switch (rarity) {
            case 1 -> ChatFormatting.BLUE;      // Синий
            case 2 -> ChatFormatting.DARK_PURPLE; // Фиолетовый
            case 3 -> ChatFormatting.GOLD;      // Оранжевый
            case 4 -> ChatFormatting.DARK_RED; // Бирюзовый
            default -> ChatFormatting.WHITE;    // Белый
        };
    }

    @Override
    public boolean simulationExecute(Level pLevel, Player pPlayer) {
        return RandomEvent.rollChance(pLevel, pPlayer, rarity);
    }
}
