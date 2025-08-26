package net.jrdemiurge.enigmaticdice.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.jrdemiurge.enigmaticdice.Config;
import net.jrdemiurge.enigmaticdice.effect.ModEffects;
import net.jrdemiurge.enigmaticdice.item.ModItems;
import net.jrdemiurge.enigmaticdice.scheduler.Scheduler;
import net.jrdemiurge.enigmaticdice.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.Team;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Permafrost extends SwordItem {
    private Multimap<Attribute, AttributeModifier> configModifiers;
    public static final String PERSISTENT_DATA_PERMAFROST_STACKS_FLY_SPEED_REDUCTION = "PermafrostStacksFlySpeedReduction";
    public static final String PERSISTENT_DATA_PERMAFROST_AURA_FLY_SPEED_REDUCTION = "PermafrostAuraFlySpeedReduction";
    private static final UUID STACKED_SPEED_DEBUFF_UUID = UUID.fromString("52276cff-92bc-4a7b-aaf3-56edcdd49521");
    private static final UUID STACKED_FLYING_SPEED_DEBUFF_UUID = UUID.fromString("3feb3b9b-a99c-42b3-9d2a-e53ca1ca5440");
    private static final UUID AURA_SPEED_DEBUFF_UUID = UUID.fromString("a2f507ba-3631-4a91-ace0-41711c981a47");
    private static final UUID AURA_FLYING_SPEED_DEBUFF_UUID = UUID.fromString("60c8804b-cb86-4efc-8a9a-63f7801731ac");
    public static final Map<UUID, Integer> stackMap = new HashMap<>();
    private static final Map<UUID, Long> lastHitTimeMap = new HashMap<>();
    public static final Set<UUID> auraApplied = new HashSet<>();
    private static final Map<UUID, Long> lastAuraTimeMap = new HashMap<>();

    public Permafrost(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot != EquipmentSlot.MAINHAND) return super.getDefaultAttributeModifiers(slot);
        if (configModifiers == null) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();

            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID,
                    "Weapon modifier", Config.PermafrostAttackDamage - 1, AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID,
                    "Weapon modifier", Config.PermafrostAttackSpeed - 4, AttributeModifier.Operation.ADDITION));

            this.configModifiers = builder.build();
        }
        return this.configModifiers;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide && attacker instanceof Player player) {
            if (player.getAttackStrengthScale(0.5F) > 0.9F) {

                int debuffDuration = Config.PermafrostAttackDebuffDuration;
                int maxStacks = Config.PermafrostAttackMaxStacks;
                double reductionFactorPerStack = Config.PermafrostAttackReductionPerStack;

                UUID targetId = target.getUUID();
                int stacksOld = stackMap.getOrDefault(targetId, 0);

                int stacks = Math.min(maxStacks, stacksOld + 1);
                stackMap.put(targetId, stacks);

                long gameTime = target.level().getGameTime();
                lastHitTimeMap.put(targetId, gameTime);

                if (stacks == maxStacks) {
                    player.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                            ModSounds.PERMAFROST_FROZEN.get(), SoundSource.PLAYERS, 1F, 1.0F);
                }

                if (stacks == maxStacks && target instanceof Player targetPlayer){
                    targetPlayer.stopFallFlying();
                }

                double reductionFactor = -reductionFactorPerStack * stacks;

                AttributeInstance movementSpeedAttribute = target.getAttribute(Attributes.MOVEMENT_SPEED);
                if (movementSpeedAttribute != null) {
                    movementSpeedAttribute.removeModifier(STACKED_SPEED_DEBUFF_UUID);

                    AttributeModifier slowModifier = new AttributeModifier(
                            STACKED_SPEED_DEBUFF_UUID,
                            "PermafrostStackedSpeedDebuff",
                            reductionFactor,
                            AttributeModifier.Operation.MULTIPLY_TOTAL
                    );

                    movementSpeedAttribute.addTransientModifier(slowModifier);
                }

                AttributeInstance flyingSpeedAttribute = target.getAttribute(Attributes.FLYING_SPEED);
                if (flyingSpeedAttribute != null) {
                    flyingSpeedAttribute.removeModifier(STACKED_FLYING_SPEED_DEBUFF_UUID);

                    AttributeModifier slowModifier = new AttributeModifier(
                            STACKED_FLYING_SPEED_DEBUFF_UUID,
                            "PermafrostStackedFlyingSpeedDebuff",
                            reductionFactor,
                            AttributeModifier.Operation.MULTIPLY_TOTAL
                    );

                    flyingSpeedAttribute.addTransientModifier(slowModifier);
                }

                target.addEffect(new MobEffectInstance(ModEffects.FROST_HIT.get(), debuffDuration, stacks - 1));

                float flyingSpeedReduction = 0.05F * (float)reductionFactorPerStack;

                if (target instanceof Player targetPlayer && stacks > stacksOld) {
                    float currentFlySpeed = targetPlayer.getAbilities().getFlyingSpeed();
                    targetPlayer.getAbilities().setFlyingSpeed(currentFlySpeed - flyingSpeedReduction);
                    targetPlayer.onUpdateAbilities();
                    targetPlayer.getPersistentData().putFloat(PERSISTENT_DATA_PERMAFROST_STACKS_FLY_SPEED_REDUCTION, flyingSpeedReduction * stacks);
                }

                Scheduler.schedule(() -> {
                    if (gameTime == lastHitTimeMap.getOrDefault(targetId, -1L)) {
                        if (target instanceof Player targetPlayer) {
                            float restoredFlySpeed = targetPlayer.getAbilities().getFlyingSpeed() + (flyingSpeedReduction * stackMap.getOrDefault(targetId, 0));
                            targetPlayer.getAbilities().setFlyingSpeed(restoredFlySpeed);
                            targetPlayer.onUpdateAbilities();
                            targetPlayer.getPersistentData().remove(PERSISTENT_DATA_PERMAFROST_STACKS_FLY_SPEED_REDUCTION);
                        }
                        if (movementSpeedAttribute != null && target.isAlive()) {
                            movementSpeedAttribute.removeModifier(STACKED_SPEED_DEBUFF_UUID);
                        }
                        if (flyingSpeedAttribute != null && target.isAlive()) {
                            flyingSpeedAttribute.removeModifier(STACKED_FLYING_SPEED_DEBUFF_UUID);
                        }
                        if (target.isAlive()) {
                            player.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                                    ModSounds.PERMAFROST_UNFROZEN.get(), SoundSource.PLAYERS, 1F, 1.0F);
                        }
                        stackMap.remove(targetId);
                        lastHitTimeMap.remove(targetId);
                    }
                }, debuffDuration);
            }
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (!pLevel.isClientSide && pEntity instanceof Player player) {
            if (pLevel.getGameTime() % 5L != 0L)  return;

            boolean isInMainHand = player.getMainHandItem().is(ModItems.PERMAFROST.get());

            if (isInMainHand) {
                int auraRadius = Config.PermafrostAuraRadius;
                int debuffDuration = Config.PermafrostAuraDebuffDuration;
                double reductionFactor = Config.PermafrostAuraReductionFactor;

                AABB area = pEntity.getBoundingBox().inflate(auraRadius);
                List<LivingEntity> victims = pLevel.getEntitiesOfClass(LivingEntity.class, area,
                        e -> e != player && e.isAlive() && e.isPickable());


                for (LivingEntity victim : victims) {
                    if (checkFriendlyFire(victim, player)) {
                        UUID targetId = victim.getUUID();
                        long gameTime = victim.level().getGameTime();
                        lastAuraTimeMap.put(targetId, gameTime);

                        AttributeInstance movementSpeedAttribute = victim.getAttribute(Attributes.MOVEMENT_SPEED);
                        if (movementSpeedAttribute != null) {
                            movementSpeedAttribute.removeModifier(AURA_SPEED_DEBUFF_UUID);

                            AttributeModifier slowModifier = new AttributeModifier(
                                    AURA_SPEED_DEBUFF_UUID,
                                    "PermafrostAuraSpeedDebuff",
                                    -reductionFactor,
                                    AttributeModifier.Operation.MULTIPLY_TOTAL
                            );

                            movementSpeedAttribute.addTransientModifier(slowModifier);
                        }

                        AttributeInstance flyingSpeedAttribute = victim.getAttribute(Attributes.FLYING_SPEED);
                        if (flyingSpeedAttribute != null) {
                            flyingSpeedAttribute.removeModifier(AURA_FLYING_SPEED_DEBUFF_UUID);

                            AttributeModifier slowModifier = new AttributeModifier(
                                    AURA_FLYING_SPEED_DEBUFF_UUID,
                                    "PermafrostAuraFlyingSpeedDebuff",
                                    -reductionFactor,
                                    AttributeModifier.Operation.MULTIPLY_TOTAL
                            );

                            flyingSpeedAttribute.addTransientModifier(slowModifier);
                        }

                        victim.addEffect(new MobEffectInstance(ModEffects.FROST_AURA.get(), debuffDuration, 0));

                        float flyingSpeedReduction = 0.05F * (float)reductionFactor;

                        if (victim instanceof Player targetPlayer && !auraApplied.contains(targetId)) {
                            float currentFlySpeed = targetPlayer.getAbilities().getFlyingSpeed();
                            targetPlayer.getAbilities().setFlyingSpeed(currentFlySpeed - flyingSpeedReduction);
                            targetPlayer.onUpdateAbilities();
                            auraApplied.add(targetId);
                            targetPlayer.getPersistentData().putFloat(PERSISTENT_DATA_PERMAFROST_AURA_FLY_SPEED_REDUCTION, flyingSpeedReduction);
                        }

                        Scheduler.schedule(() -> {
                            if (gameTime == lastAuraTimeMap.getOrDefault(targetId, -1L)) {
                                if (movementSpeedAttribute != null && victim.isAlive()) {
                                    movementSpeedAttribute.removeModifier(AURA_SPEED_DEBUFF_UUID);
                                }
                                if (flyingSpeedAttribute != null && victim.isAlive()) {
                                    flyingSpeedAttribute.removeModifier(AURA_FLYING_SPEED_DEBUFF_UUID);
                                }
                                if (victim instanceof Player targetPlayer) {
                                    float restoredFlySpeed = targetPlayer.getAbilities().getFlyingSpeed() + flyingSpeedReduction;
                                    targetPlayer.getAbilities().setFlyingSpeed(restoredFlySpeed);
                                    targetPlayer.onUpdateAbilities();
                                    auraApplied.remove(targetId);
                                    targetPlayer.getPersistentData().remove(PERSISTENT_DATA_PERMAFROST_AURA_FLY_SPEED_REDUCTION);
                                }
                                lastAuraTimeMap.remove(targetId);
                            }
                        }, debuffDuration);
                    }
                }

                BlockPos center = player.blockPosition();

                for (BlockPos pos : BlockPos.betweenClosed(
                        center.offset(-auraRadius, -auraRadius, -auraRadius),
                        center.offset(auraRadius, auraRadius, auraRadius)
                )) {
                    if (pos.closerToCenterThan(center.getCenter(), auraRadius)) {

                        BlockState bs = pLevel.getBlockState(pos);
                        FluidState fs = bs.getFluidState();

                        if (fs.is(Fluids.WATER)) {
                            if (bs.hasProperty(BlockStateProperties.WATERLOGGED) && bs.getValue(BlockStateProperties.WATERLOGGED)) {
                                continue; // пропускаем waterlogged-блоки
                            }
                            pLevel.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
                            continue;
                        }

                        if (fs.is(Fluids.LAVA)) {
                            pLevel.setBlockAndUpdate(pos, Blocks.OBSIDIAN.defaultBlockState()); // возможно стоит в базальт
                            continue;
                        }

                        if (fs.is(Fluids.FLOWING_LAVA)) {
                            pLevel.setBlockAndUpdate(pos, Blocks.COBBLESTONE.defaultBlockState());
                            continue;
                        }
                        // возможно стоит превращать рыхлый снег в снег, но это не логично
                    }
                }
            }
        }
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
    }

    public static boolean checkFriendlyFire(LivingEntity target, LivingEntity attacker) {
        Team attackerTeam = attacker.getTeam();
        Team entityTeam = target.getTeam();
        if (entityTeam != null && attackerTeam == entityTeam && !attackerTeam.isAllowFriendlyFire()) {
            return false;
        } else {
            if (target instanceof OwnableEntity tameable && tameable.getOwner() != null) {
                LivingEntity owner = tameable.getOwner();
                if (owner == attacker) {
                    return false;
                }
                Team ownerTeam = owner.getTeam();
                if (ownerTeam != null && attackerTeam == ownerTeam && !attackerTeam.isAllowFriendlyFire()) {
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (Screen.hasShiftDown()) {
            int stackDebuffDuration = Config.PermafrostAttackDebuffDuration / 20;
            int maxStacks = Config.PermafrostAttackMaxStacks;
            double reductionFactorPerStack = Config.PermafrostAttackReductionPerStack * 100;
            int auraRadius = Config.PermafrostAuraRadius;
            double auraReductionFactor = Config.PermafrostAuraReductionFactor * 100;

            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.permafrost_1"));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.permafrost_2", reductionFactorPerStack, stackDebuffDuration)
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.permafrost_3", maxStacks)
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.permafrost_4"));
            pTooltipComponents.add(Component.literal(" "));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.permafrost_5",auraRadius)
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.permafrost_6",auraReductionFactor)
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.permafrost_7"));
        } else {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.holdShift"));
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}

