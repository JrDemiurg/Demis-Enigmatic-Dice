package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.jrdemiurge.enigmaticdice.scheduler.Scheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;


public class SummonMimic implements RandomEvent {
    private final int rarity;

    public SummonMimic(int rarity) {
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

        EntityType<?> entityType = EntityType.byString("artifacts:mimic").orElse(null);
        if (entityType == null) return false;

        Entity entity = entityType.create(pLevel);
        if (entity == null) return false;

        entity.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, 0, 0);
        entity.setCustomName(Component.literal("Minic"));
        pLevel.addFreshEntity(entity);

        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(0);
        }

        MutableComponent message = Component.translatable("enigmaticdice.event.mimic");
        pPlayer.displayClientMessage(message, false);
        return true;
    }

    @Override
    public boolean simulationExecute(Level pLevel, Player pPlayer) {
        return RandomEvent.rollChance(pLevel, pPlayer, rarity, true);
    }
}
