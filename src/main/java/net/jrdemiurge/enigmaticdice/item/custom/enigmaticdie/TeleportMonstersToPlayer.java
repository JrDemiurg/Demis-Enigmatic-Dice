package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.jrdemiurge.enigmaticdice.scheduler.Scheduler;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

import java.util.*;


public class TeleportMonstersToPlayer extends RandomEvent {

    public TeleportMonstersToPlayer(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!rollChance(pLevel, pPlayer, rarity)) return false;
        }

        if (!(pLevel instanceof ServerLevel serverLevel)) {
            return false;
        }

        AABB searchArea = pPlayer.getBoundingBox().inflate(100);
        List<Monster> monsters  = new ArrayList<>(serverLevel.getEntitiesOfClass(Monster.class, searchArea));

        if (monsters.isEmpty()) return false;

        Scheduler.schedule(() -> {
            if (!monsters.isEmpty()) {
                Monster mob = monsters.remove(0);
                if (mob.isAlive()) {
                    double angle = serverLevel.random.nextDouble() * Math.PI * 2.0;
                    double radius = 5.0;

                    double targetX = pPlayer.getX() + Math.cos(angle) * radius;
                    double targetZ = pPlayer.getZ() + Math.sin(angle) * radius;

                    int surfaceY = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                            (int)Math.floor(targetX), (int)Math.floor(targetZ));
                    double targetY = surfaceY + 0.1;

                    mob.teleportTo(targetX, targetY, targetZ);
                }
            }
        }, 0, 10, monsters.size());

        MutableComponent message = Component.translatable("enigmaticdice.event.teleport_monsters_to_player");
        pPlayer.displayClientMessage(message, false);
        return true;
    }
}
