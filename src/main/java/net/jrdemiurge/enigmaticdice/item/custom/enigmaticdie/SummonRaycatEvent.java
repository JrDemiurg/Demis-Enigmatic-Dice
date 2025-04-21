package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.jrdemiurge.enigmaticdice.scheduler.Scheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Method;
import java.util.List;

public class SummonRaycatEvent implements RandomEvent {
    private final int rarity;

    public SummonRaycatEvent(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!RandomEvent.rollChance(pLevel, pPlayer, rarity, true)) return false;
        }

        Vec3 lookVec = pPlayer.getLookAngle();
        lookVec = new Vec3(lookVec.x, 0, lookVec.z).normalize();

        Vec3 spawnPos = pPlayer.position().add(lookVec.scale(3)).add(0, 1, 0);

        if (!pLevel.getBlockState(BlockPos.containing(spawnPos)).getCollisionShape(pLevel, BlockPos.containing(spawnPos)).isEmpty()) {
            return false;
        }

        EntityType<?> entityType = EntityType.byString("alexscaves:raycat").orElse(null);
        if (entityType == null) return false;

        Entity entity = entityType.create(pLevel);
        if (entity == null) return false;

        entity.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, 0, 0);
        pLevel.addFreshEntity(entity);

        if (entity instanceof TamableAnimal tamable) {
            tamable.tame(pPlayer);
            tamable.setOwnerUUID(pPlayer.getUUID());
            tamable.addEffect(new MobEffectInstance(MobEffects.REGENERATION, MobEffectInstance.INFINITE_DURATION, 0, false, false));
            tamable.getAttribute(Attributes.MAX_HEALTH).setBaseValue(999.0D);
        }

        try {
            Class<?> raycatClass = Class.forName("com.github.alexmodguy.alexscaves.server.entity.living.RaycatEntity");
            if (raycatClass.isInstance(entity)) {
                Method setCommandMethod = raycatClass.getMethod("setCommand", int.class);
                setCommandMethod.invoke(entity, 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        MutableComponent message = Component.translatable("enigmaticdice.event.raycat");
        pPlayer.displayClientMessage(message, false);
        return true;
    }

    @Override
    public boolean simulationExecute(Level pLevel, Player pPlayer) {
        return RandomEvent.rollChance(pLevel, pPlayer, rarity, true);
    }
}
