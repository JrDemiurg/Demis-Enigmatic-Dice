package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.jrdemiurge.enigmaticdice.scheduler.Scheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

public class LightningStrikeEvent extends RandomEvent {
    private static final int NUM_LIGHTNING = 15;  // Количество молний
    private static final int DELAY_TICKS = 15;
    private static final int RADIUS = 8;
    private final int rarity;

    public LightningStrikeEvent(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!RandomEvent.rollChance(pLevel, pPlayer, rarity, isPositiveEvent())) return false;
        }

        BlockPos pos = pPlayer.blockPosition();

        BlockPos strikePos = pLevel.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, pos);

        LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, pLevel);
        lightningBolt.moveTo(strikePos.getX(), strikePos.getY(), strikePos.getZ());
        pLevel.addFreshEntity(lightningBolt);

        int sum = 0;

        for (int i = 0; i < NUM_LIGHTNING; i++) {
            int delay = 15 + i * DELAY_TICKS - sum;
            sum += i;

            Scheduler.schedule(() -> {

                int offsetX = pLevel.random.nextInt(2*RADIUS +1) - RADIUS; // -1, 0 или 1
                int offsetZ = pLevel.random.nextInt(2*RADIUS +1) - RADIUS; // -1, 0 или 1
                BlockPos strikePos_2 = pLevel.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, pos.offset(offsetX, 0, offsetZ));

                LightningBolt lightningBolt_2 = new LightningBolt(EntityType.LIGHTNING_BOLT, pLevel);
                lightningBolt_2.moveTo(strikePos_2.getX(), strikePos_2.getY(), strikePos_2.getZ());
                pLevel.addFreshEntity(lightningBolt_2);

                offsetX = pLevel.random.nextInt(2*RADIUS +1) - RADIUS; // -1, 0 или 1
                offsetZ = pLevel.random.nextInt(2*RADIUS +1) - RADIUS; // -1, 0 или 1
                strikePos_2 = pLevel.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, pos.offset(offsetX, 0, offsetZ));

                LightningBolt lightningBolt_3 = new LightningBolt(EntityType.LIGHTNING_BOLT, pLevel);
                lightningBolt_3.moveTo(strikePos_2.getX(), strikePos_2.getY(), strikePos_2.getZ());
                pLevel.addFreshEntity(lightningBolt_3);
            }, delay, 0);
        }

        pPlayer.displayClientMessage(Component.translatable("enigmaticdice.event.lightning_wrath"), false);
        return true;
    }

    @Override
    public boolean simulationExecute(Level pLevel, Player pPlayer) {
        return RandomEvent.rollChance(pLevel, pPlayer, rarity, isPositiveEvent());
    }
}