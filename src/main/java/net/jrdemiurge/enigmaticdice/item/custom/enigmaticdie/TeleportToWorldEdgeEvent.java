package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;

public class TeleportToWorldEdgeEvent implements RandomEvent {
    private final int rarity;

    public TeleportToWorldEdgeEvent(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!RandomEvent.rollChance(pLevel, pPlayer, rarity, false)) return false;
        }

        if (!(pLevel instanceof ServerLevel serverLevel)) {
            return false;
        }

        try {
            boolean xEdge = pLevel.random.nextBoolean();
            boolean positive = pLevel.random.nextBoolean();

            double borderHalfSize = serverLevel.getWorldBorder().getSize() / 2.0 - 10.0; // -10 блоков от края
            double borderCenterX = serverLevel.getWorldBorder().getCenterX();
            double borderCenterZ = serverLevel.getWorldBorder().getCenterZ();

            final int MAX_WORLD_COORD = 29_999_974;

            int rawX = xEdge
                    ? (int) (borderCenterX + (positive ? borderHalfSize : -borderHalfSize))
                    : pPlayer.blockPosition().getX();

            int rawZ = xEdge
                    ? pPlayer.blockPosition().getZ()
                    : (int) (borderCenterZ + (positive ? borderHalfSize : -borderHalfSize));

            int targetX = Math.max(-MAX_WORLD_COORD, Math.min(MAX_WORLD_COORD, rawX));
            int targetZ = Math.max(-MAX_WORLD_COORD, Math.min(MAX_WORLD_COORD, rawZ));

            serverLevel.getChunkSource().getChunk(targetX >> 4, targetZ >> 4, ChunkStatus.FULL, true);
            int surfaceY = serverLevel.getHeight(Heightmap.Types.WORLD_SURFACE, targetX, targetZ);

            pPlayer.teleportTo(targetX + 0.5, surfaceY + 1, targetZ + 0.5);

            MutableComponent message = Component.translatable("enigmaticdice.event.teleport_to_world_edge");
            pPlayer.displayClientMessage(message, false);
            return true;

        } catch (Exception e) {
            EnigmaticDice.LOGGER.error("Error teleporting to world edge: ", e);
            return false;
        }
    }

    @Override
    public boolean simulationExecute(Level pLevel, Player pPlayer) {
        return RandomEvent.rollChance(pLevel, pPlayer, rarity, false);
    }
}
