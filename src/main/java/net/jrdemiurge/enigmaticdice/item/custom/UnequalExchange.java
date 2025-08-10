package net.jrdemiurge.enigmaticdice.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.jrdemiurge.enigmaticdice.Config;
import net.jrdemiurge.enigmaticdice.effect.ModEffects;
import net.jrdemiurge.enigmaticdice.item.custom.unequalexchange.UnequalExchangeData;
import net.jrdemiurge.enigmaticdice.item.custom.unequalexchange.UnequalExchangeDataStorage;
import net.jrdemiurge.enigmaticdice.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class UnequalExchange extends SwordItem {
    private Multimap<Attribute, AttributeModifier> configModifiers;

    public UnequalExchange(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot != EquipmentSlot.MAINHAND) return super.getDefaultAttributeModifiers(slot);
        if (configModifiers == null) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();

            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID,
                    "Weapon modifier", Config.UnequalExchangeAttackDamage - 1, AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID,
                    "Weapon modifier", Config.UnequalExchangeAttackSpeed - 4, AttributeModifier.Operation.ADDITION));

            this.configModifiers = builder.build();
        }
        return this.configModifiers;
    }

    private static final UUID HEALTH_DEBUFF_UUID = UUID.fromString("5f1d9b20-dfd6-4284-9d3a-004776a87bfd");
    private static final UUID ATTACK_SPEED_DEBUFF_UUID = UUID.fromString("27a72973-be25-44f0-9613-187812e4627c");
    private static final UUID ARMOR_DEBUFF_UUID = UUID.fromString("44371e08-d7ae-4344-8cfa-95c9fd4825c2");
    private static final UUID ARMOR_TOUGHNESS_DEBUFF_UUID = UUID.fromString("a6fb771d-97de-4883-86a8-1ba2b8db59c2");
    private static final UUID SPEED_DEBUFF_UUID = UUID.fromString("68403af9-c76e-4b08-8196-f264d872876d");

    // добавить чёрный список мобов
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide && attacker instanceof Player player) {
            if (player.getAttackStrengthScale(0.5F) > 0.9F) {
                removeModifierIfExists(player, Attributes.MAX_HEALTH, HEALTH_DEBUFF_UUID);
                float targetMaxHealth = target.getMaxHealth();
                float attackerMaxHealth = attacker.getMaxHealth();

                float targetNewHealth = target.getHealth() - targetMaxHealth * (float) Config.UnequalExchangeTargetHealthReduction;
                target.setHealth(Math.max(targetNewHealth, 0.0F));
                if (target.getHealth() <= 0.0F) {
                    target.die(attacker.damageSources().playerAttack(player));
                }

                float attackerNewHealth = attacker.getHealth() - attackerMaxHealth * (float) Config.UnequalExchangePlayerHealthReduction;
                attacker.setHealth(Math.max(attackerNewHealth, 0.0F));
                if (attacker.getHealth() <= 0.0F) {
                    attacker.die(attacker.damageSources().playerAttack(player));
                }

                UnequalExchangeData data = UnequalExchangeDataStorage.get(player);
                data.onHit();
                updateModifiers(player, data);

                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.UNEQUAL_EXCHANGE_HIT.get(), SoundSource.PLAYERS, 0.3F, 1.0F);

            }
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    public static void updateModifiers(Player player, UnequalExchangeData data) {
        double reductionFactor = - Config.UnequalExchangeStatDebuff * data.getHitCount();
        double healthReduction = Math.max(reductionFactor, -0.8);

        removeModifierIfExists(player, Attributes.MAX_HEALTH, HEALTH_DEBUFF_UUID);
        removeModifierIfExists(player, Attributes.ATTACK_SPEED, ATTACK_SPEED_DEBUFF_UUID);
        removeModifierIfExists(player, Attributes.ARMOR, ARMOR_DEBUFF_UUID);
        removeModifierIfExists(player, Attributes.ARMOR_TOUGHNESS, ARMOR_TOUGHNESS_DEBUFF_UUID);
        removeModifierIfExists(player, Attributes.MOVEMENT_SPEED, SPEED_DEBUFF_UUID);

        player.getAttribute(Attributes.MAX_HEALTH).addTransientModifier(new AttributeModifier(
                HEALTH_DEBUFF_UUID, "UnequalExchangeHealthDebuff", healthReduction, AttributeModifier.Operation.MULTIPLY_TOTAL));

        player.getAttribute(Attributes.ATTACK_SPEED).addTransientModifier(new AttributeModifier(
                ATTACK_SPEED_DEBUFF_UUID, "UnequalExchangeAttackSpeedDebuff", reductionFactor, AttributeModifier.Operation.MULTIPLY_TOTAL));

        player.getAttribute(Attributes.ARMOR).addTransientModifier(new AttributeModifier(
                ARMOR_DEBUFF_UUID, "UnequalExchangeArmorDebuff", reductionFactor, AttributeModifier.Operation.MULTIPLY_TOTAL));

        player.getAttribute(Attributes.ARMOR_TOUGHNESS).addTransientModifier(new AttributeModifier(
                ARMOR_TOUGHNESS_DEBUFF_UUID, "UnequalExchangeArmorToughnessDebuff", reductionFactor, AttributeModifier.Operation.MULTIPLY_TOTAL));

        player.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(new AttributeModifier(
                SPEED_DEBUFF_UUID, "UnequalExchangeSpeedDebuff", reductionFactor, AttributeModifier.Operation.MULTIPLY_TOTAL));

        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }

        player.addEffect(new MobEffectInstance(ModEffects.UNEQUAL_EXCHANGE_DEBUFFS.get(), data.getTimeLeftTicks(), data.getHitCount() - 1));
    }

    private static void removeModifierIfExists(Player player, Attribute attribute, UUID uuid) {
        if (player.getAttribute(attribute) != null && player.getAttribute(attribute).getModifier(uuid) != null) {
            player.getAttribute(attribute).removeModifier(uuid);
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
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.unequal_exchange_1"));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.unequal_exchange_2", String.valueOf(Config.UnequalExchangeTargetHealthReduction   * 100) + "%")
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.unequal_exchange_3", String.valueOf(Config.UnequalExchangePlayerHealthReduction   * 100) + "%")
                    .withStyle(ChatFormatting.GOLD));

            pTooltipComponents.add(Component.literal(" "));

            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.unequal_exchange_4", Config.UnequalExchangeDebuffDuration)
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.unequal_exchange_5", String.valueOf(Config.UnequalExchangeStatDebuff   * 100) + "%")
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.unequal_exchange_6", String.valueOf(Config.UnequalExchangeStatDebuff   * 100) + "%")
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.unequal_exchange_7", String.valueOf(Config.UnequalExchangeStatDebuff   * 100) + "%")
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.unequal_exchange_8", String.valueOf(Config.UnequalExchangeStatDebuff   * 100) + "%")
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.unequal_exchange_9", String.valueOf(Config.UnequalExchangeStatDebuff   * 100) + "%")
                    .withStyle(ChatFormatting.GOLD));
        } else {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.unequal_exchange_0"));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.holdShift"));
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
