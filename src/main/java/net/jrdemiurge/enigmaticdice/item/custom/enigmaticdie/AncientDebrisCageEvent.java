package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

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

        MutableComponent message = Component.translatable("enigmaticdice.event.safe_message");
        pPlayer.displayClientMessage(message, false);
        return true;
    }

    @Override
    public boolean simulationExecute(Level pLevel, Player pPlayer) {
        return RandomEvent.rollChance(pLevel, pPlayer, rarity);
    }
}
