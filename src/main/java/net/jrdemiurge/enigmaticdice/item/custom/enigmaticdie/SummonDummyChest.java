package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.jrdemiurge.enigmaticdice.attribute.ModAttributes;
import net.jrdemiurge.enigmaticdice.scheduler.Scheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;


public class SummonDummyChest extends RandomEvent {

    public SummonDummyChest(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!rollChance(pLevel, pPlayer, rarity)) return false;
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
        entity.setCustomName(Component.literal("Dummy Chest"));
        pLevel.addFreshEntity(entity);

        if (entity instanceof Mob mob) {
            mob.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(0);
            mob.getAttribute(Attributes.ARMOR).setBaseValue(30);
            mob.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(20);
            mob.getAttribute(Attributes.MAX_HEALTH).setBaseValue(1024);
            mob.getAttribute(ModAttributes.SIZE_SCALE.get()).setBaseValue(2);
        }

        Scheduler.schedule(() -> {
            if (entity instanceof Mob mob) {
                mob.setNoAi(true);
                CompoundTag entityData = mob.saveWithoutId(new CompoundTag());
                entityData.putString("DeathLootTable", "enigmaticdice:entities/dummy_chest_loot_table");
                mob.load(entityData);
            }
        }, 20);

        MutableComponent message = Component.translatable("enigmaticdice.event.dummy_chest");
        pPlayer.displayClientMessage(message, false);
        return true;
    }
}
