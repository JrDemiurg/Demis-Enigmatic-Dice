package net.jrdemiurge.enigmaticdice.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.jrdemiurge.enigmaticdice.Config;
import net.jrdemiurge.enigmaticdice.effect.ModEffects;
import net.jrdemiurge.enigmaticdice.network.LookAtTargetPacket;
import net.jrdemiurge.enigmaticdice.network.NetworkHandler;
import net.jrdemiurge.enigmaticdice.scheduler.Scheduler;
import net.jrdemiurge.enigmaticdice.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.Team;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

// TODO Бафф за казнь, броня и скорость
public class CrucibleOfRile extends SwordItem {
    public static final String PDATA_RILE_HITS = "enigmaticdice:crucible_of_rile_hits";
    private Multimap<Attribute, AttributeModifier> configModifiers;

    public CrucibleOfRile(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot != EquipmentSlot.MAINHAND) return super.getDefaultAttributeModifiers(slot);
        if (configModifiers == null) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();

            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID,
                    "Weapon modifier", Config.CrucibleOfRileAttackDamage - 1, AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID,
                    "Weapon modifier", Config.CrucibleOfRileAttackSpeed - 4, AttributeModifier.Operation.ADDITION));

            this.configModifiers = builder.build();
        }
        return this.configModifiers;
    }

    // TODO добавить звук казни
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide && attacker instanceof Player player) {
            player.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                    ModSounds.CRUCIBLE_OF_RILE_ATTACK.get(), SoundSource.PLAYERS, 0.5F, 1.0F);

            if (target.getHealth() <= Config.CrucibleOfRileExecuteThreshold) {
                target.setHealth(0);
                if (target.getHealth() <= 0) {
                    target.getPersistentData().putBoolean("enigmaticdice_should_die", true);
                    target.die(attacker.damageSources().playerAttack(player));
                }
                /*if (target.getHealth() > 0) {
                    ((LivingEntityAccessor) target).enigmaticdice$invokeDropAllDeathLoot(attacker.damageSources().playerAttack(player));
                    target.level().broadcastEntityEvent(target, (byte)60);
                    target.remove(Entity.RemovalReason.KILLED);
                }*/
            }
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack item = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide) return InteractionResultHolder.pass(item);

        aggroMobsAndLockPlayers((ServerLevel) pLevel, (ServerPlayer) pPlayer);

        giveArmorBoost(pPlayer);

        pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(),
                ModSounds.CRUCIBLE_OF_RILE_USE.get(), SoundSource.PLAYERS, 0.8F, 1.0F);

        pPlayer.getCooldowns().addCooldown(this, Config.CrucibleOfRileCooldown);
        return InteractionResultHolder.success(item);
    }

    public static void aggroMobsAndLockPlayers(ServerLevel level, ServerPlayer caster) {
        double radius = Config.CrucibleOfRileAggroRadius;
        int durationTicks = Config.CrucibleOfRileAggroDuration;

        List<Mob> mobs = level.getEntitiesOfClass(
                Mob.class,
                caster.getBoundingBox().inflate(radius),
                m -> m.isAlive() && checkFriendlyFire(m, caster)
        );

        for (Mob mob : mobs) {
            mob.setTarget(caster);
        }

        if (Config.CrucibleOfRileAggroPlayers) {
            List<ServerPlayer> victims = level.getEntitiesOfClass(
                    ServerPlayer.class,
                    caster.getBoundingBox().inflate(radius),
                    sp -> sp != caster && sp.isAlive() && checkFriendlyFire(sp, caster)
            );

            for (ServerPlayer sp : victims) {
                NetworkHandler.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> sp),
                        new LookAtTargetPacket(caster.getUUID(), durationTicks)
                );
            }
        }
    }

    public static void giveArmorBoost(Player player) {
        AttributeInstance armor = player.getAttribute(Attributes.ARMOR);
        AttributeInstance toughness = player.getAttribute(Attributes.ARMOR_TOUGHNESS);

        float effectValue = (float) Config.CrucibleOfRileArmorBuffValue;
        int buffDuration = Config.CrucibleOfRileArmorBuffDuration;

        if (armor == null || toughness == null) return;

        UUID ARMOR_BOOST_UUID = UUID.fromString("2fe31679-2a55-415d-9d49-9f1cc61e4731");
        UUID TOUGHNESS_BOOST_UUID = UUID.fromString("e6481205-0401-4508-ace8-0ce73d48d2a9");

        AttributeModifier armorBoost = new AttributeModifier(
                ARMOR_BOOST_UUID,
                "crucible_of_rile_use_armor_buff",
                effectValue,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );
        AttributeModifier toughnessBoost = new AttributeModifier(
                TOUGHNESS_BOOST_UUID,
                "crucible_of_rile_use_armor_toughness_buff",
                effectValue,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );

        if (!armor.hasModifier(armorBoost)) armor.addTransientModifier(armorBoost);
        if (!toughness.hasModifier(toughnessBoost)) toughness.addTransientModifier(toughnessBoost);

        Scheduler.schedule(() -> {
            AttributeInstance a = player.getAttribute(Attributes.ARMOR);
            AttributeInstance t = player.getAttribute(Attributes.ARMOR_TOUGHNESS);

            if (a != null) a.removeModifier(ARMOR_BOOST_UUID);
            if (t != null) t.removeModifier(TOUGHNESS_BOOST_UUID);
        }, buffDuration);

        player.addEffect(new MobEffectInstance(ModEffects.CRUCIBLE_OF_RILE_ARMOR_BOOST.get(), buffDuration, 0));
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

    public static boolean isHeldMainHand(LivingEntity livingEntity) {
        return livingEntity.getMainHandItem().getItem() instanceof CrucibleOfRile;
    }

    public static void handleOnOwnerAttacked(LivingEntity livingEntity) {
        CompoundTag data = livingEntity.getPersistentData();
        int hits = data.getInt(PDATA_RILE_HITS) + 1;

        if (hits >= Config.CrucibleOfRileHitsForCounterattack) {
            data.putInt(PDATA_RILE_HITS, 0);
            doAoEDamage(livingEntity);
        } else {
            data.putInt(PDATA_RILE_HITS, hits);
        }
    }

    // TODO в идеале добавить анимацию крутилки из better combat
    private static void doAoEDamage(LivingEntity livingEntity) {
        livingEntity.level().playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
                ModSounds.CRUCIBLE_OF_RILE_HELIX.get(), SoundSource.PLAYERS, 0.8F, 1.0F);


        AABB box = livingEntity.getBoundingBox().inflate(Config.CrucibleOfRileCounterattackRadius);

        List<LivingEntity> targets = livingEntity.level().getEntitiesOfClass(
                LivingEntity.class, box,
                e -> e.isAlive() && e != livingEntity  && checkFriendlyFire(e, livingEntity)
        );

        float dmg = (float) livingEntity.getAttributeValue(Attributes.ATTACK_DAMAGE);

        DamageSource src;
        if (livingEntity instanceof Player player) {
            src = livingEntity.damageSources().playerAttack(player);
        } else {
            src = livingEntity.damageSources().mobAttack(livingEntity);
        }

        for (LivingEntity t : targets) {
            t.hurt(src, dmg);
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
            double crucibleOfRileExecuteThreshold = Config.CrucibleOfRileExecuteThreshold;
            double crucibleOfRileAggroRadius = Config.CrucibleOfRileAggroRadius;
            double crucibleOfRileArmorBuffValue = Config.CrucibleOfRileArmorBuffValue * 100;
            int crucibleOfRileArmorBuffDuration = Config.CrucibleOfRileArmorBuffDuration / 20;
            int crucibleOfRileHitsForCounterattack = Config.CrucibleOfRileHitsForCounterattack;
            double crucibleOfRileCounterattackRadius = Config.CrucibleOfRileCounterattackRadius;
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.on_use"));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.crucible_of_rile_1", crucibleOfRileAggroRadius)
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.crucible_of_rile_2", crucibleOfRileArmorBuffValue, crucibleOfRileArmorBuffDuration)
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.add(Component.literal(" "));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.crucible_of_rile_3", crucibleOfRileHitsForCounterattack)
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.crucible_of_rile_4", crucibleOfRileCounterattackRadius)
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.add(Component.literal(" "));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.on_hit"));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.crucible_of_rile_5", crucibleOfRileExecuteThreshold)
                    .withStyle(ChatFormatting.GOLD));
        } else {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.holdShift"));
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
