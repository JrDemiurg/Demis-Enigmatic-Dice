package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SummonTamedHorse extends RandomEvent {
    private final int rarity;

    public SummonTamedHorse(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed && !RandomEvent.rollChance(pLevel, pPlayer, rarity, isPositiveEvent())) {
            return false;
        }

        Vec3 lookVec = pPlayer.getLookAngle();
        lookVec = new Vec3(lookVec.x, 0, lookVec.z).normalize();

        Vec3 spawnPos = pPlayer.position().add(lookVec.scale(3)).add(0, 1, 0);
        BlockPos blockPos = BlockPos.containing(spawnPos);

        if (!pLevel.getBlockState(blockPos).getCollisionShape(pLevel, blockPos).isEmpty()) {
            return false;
        }

        Horse horse = EntityType.HORSE.create(pLevel);
        if (horse == null) return false;

        horse.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, 0, 0);
        horse.tameWithName(pPlayer);
        horse.equipSaddle(SoundSource.NEUTRAL);

        horse.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.3375D);
        horse.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(1.0D);
        horse.getAttribute(Attributes.MAX_HEALTH).setBaseValue(30.0D);
        horse.setHealth(horse.getMaxHealth());
        horse.addEffect(new MobEffectInstance(MobEffects.REGENERATION, MobEffectInstance.INFINITE_DURATION, 0, false, false));

        pLevel.addFreshEntity(horse);

        MutableComponent message = Component.translatable("enigmaticdice.event.horse." + pLevel.random.nextInt(2));
        pPlayer.displayClientMessage(message, false);

        return true;
    }

    @Override
    public boolean simulationExecute(Level pLevel, Player pPlayer) {
        return RandomEvent.rollChance(pLevel, pPlayer, rarity, isPositiveEvent());
    }
}
