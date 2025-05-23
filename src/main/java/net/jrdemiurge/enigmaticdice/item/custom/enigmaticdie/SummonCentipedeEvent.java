package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Method;

public class SummonCentipedeEvent implements RandomEvent {
    private final int rarity;

    public SummonCentipedeEvent(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean  execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!RandomEvent.rollChance(pLevel, pPlayer, rarity, false)) return false;
        }

        Vec3 lookVec = pPlayer.getLookAngle();
        lookVec = new Vec3(lookVec.x, 0, lookVec.z).normalize();

        Vec3 spawnPos = pPlayer.position().add(lookVec.scale(6)).add(0, 1, 0);

        if (!pLevel.getBlockState(BlockPos.containing(spawnPos)).getCollisionShape(pLevel, BlockPos.containing(spawnPos)).isEmpty()) {
            return false;
        }

        EntityType<?> entityType = EntityType.byString("alexsmobs:centipede_head").orElse(null);
        if (entityType == null) return false;

        Entity entity = entityType.create(pLevel);
        if (entity == null) return false;

        entity.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, 0, 0);
        pLevel.addFreshEntity(entity);

        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100);
        }

        try {
            Class<?> centipedeClass = Class.forName("com.github.alexthe666.alexsmobs.entity.EntityCentipedeHead");
            if (centipedeClass.isInstance(entity)) {
                Method setCommandMethod = centipedeClass.getMethod("setSegmentCount", int.class);
                setCommandMethod.invoke(entity, 20);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        MutableComponent message = Component.translatable("enigmaticdice.event.centipede." + pLevel.random.nextInt(9));
        pPlayer.displayClientMessage(message, false);
        return true;
    }

    @Override
    public boolean simulationExecute(Level pLevel, Player pPlayer) {
        return RandomEvent.rollChance(pLevel, pPlayer, rarity, false);
    }
}
