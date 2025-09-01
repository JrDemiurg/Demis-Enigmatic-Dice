package net.jrdemiurge.enigmaticdice.item.custom;

import net.jrdemiurge.enigmaticdice.Config;
import net.jrdemiurge.enigmaticdice.item.custom.unequalexchange.UnequalExchangeData;
import net.jrdemiurge.enigmaticdice.item.custom.unequalexchange.UnequalExchangeDataStorage;
import net.jrdemiurge.enigmaticdice.sound.ModSounds;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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

/*public class Kyomu extends SwordItem {
    // на ЖЕРТВЕ: Compound с парами attackerUUID -> double (накопленный урон от этого атакующего)
    public static final String VICTIM_ACCUM_MAP = "kyomu_accum_damage";
    public static final String VICTIM_HITCOUNT_MAP = "kyomu_hit_counts";
    // на ИГРОКЕ: список UUID целей, у которых есть накопленный урон от него
    public static final String ATTACKER_TARGETS = "kyomu_targets";
    public static final String KYOMU_RELEASING = "kyomu_releasing";

    public Kyomu(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide && attacker instanceof Player player) {
            if (player.getAttackStrengthScale(0.5F) > 0.9F) {
                CompoundTag vtag = target.getPersistentData();
                CompoundTag hits = vtag.getCompound(Kyomu.VICTIM_HITCOUNT_MAP);
                String key = player.getUUID().toString();
                int prev = hits.contains(key, Tag.TAG_INT) ? hits.getInt(key) : 0;
                hits.putInt(key, prev + 1);
                vtag.put(Kyomu.VICTIM_HITCOUNT_MAP, hits);
            }
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack item = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide) return InteractionResultHolder.pass(item);

        ServerLevel srv = (ServerLevel) pLevel;
        applyStoredDamage(srv, pPlayer);

        // добавить кд 5 сек, чтобы у игроков и мысли не было юзать меч против обычных мобов
        // добавить звук активации от каждого моба и от игрока
        return InteractionResultHolder.success(item);
    }

    private void applyStoredDamage(ServerLevel level, Player attacker) {
        CompoundTag ptag = attacker.getPersistentData();

        ptag.putBoolean(KYOMU_RELEASING, true);

        ListTag list = ptag.getList(ATTACKER_TARGETS, Tag.TAG_STRING);
        if (!list.isEmpty()) {
            for (Tag t : list) {
                UUID id = UUID.fromString(t.getAsString());
                Entity e = level.getEntity(id);
                if (!(e instanceof LivingEntity victim) || !victim.isAlive()) {
                    continue;
                }

                CompoundTag vtag = victim.getPersistentData();
                CompoundTag map = vtag.getCompound(VICTIM_ACCUM_MAP);
                String key = attacker.getUUID().toString();
                if (!map.contains(key, Tag.TAG_DOUBLE)) continue;

                double dmg = map.getDouble(key);
                if (dmg <= 0.0) {
                    map.remove(key);
                    vtag.put(VICTIM_ACCUM_MAP, map);
                    continue;
                }

                CompoundTag hitMap = vtag.getCompound(Kyomu.VICTIM_HITCOUNT_MAP);
                int hits = hitMap.contains(key, Tag.TAG_INT) ? hitMap.getInt(key) : 0;
                double multiplier = 1.0 + 0.05 * Math.max(0, hits);

                float finalDmg = (float) (dmg * multiplier);

                DamageSource src = attacker.damageSources().playerAttack(attacker);
                ((LivingEntityAccessor) victim).invokeActuallyHurt(src, finalDmg);
                // victim.hurt(attacker.damageSources().playerAttack(attacker), (float) dmg);

                if (victim.isDeadOrDying()) {
                    boolean savedByTotem = ((LivingEntityAccessor) victim).invokeCheckTotemDeathProtection(src);
                    if (!savedByTotem) {
                        victim.die(src);
                    }
                }

                map.remove(key);
                vtag.put(VICTIM_ACCUM_MAP, map);

                if (hitMap.contains(key, Tag.TAG_INT)) {
                    hitMap.remove(key);
                    vtag.put(Kyomu.VICTIM_HITCOUNT_MAP, hitMap);
                }
            }

            ptag.remove(ATTACKER_TARGETS);
        }

        ptag.remove(KYOMU_RELEASING);
    }

    public static boolean isHeldMainHand(LivingEntity livingEntity) {
        return livingEntity.getMainHandItem().getItem() instanceof Kyomu;
    }

    public static void addTargetForAttacker(LivingEntity player, UUID targetId) {
        CompoundTag ptag = player.getPersistentData();
        ListTag list = ptag.getList(ATTACKER_TARGETS, Tag.TAG_STRING);
        String s = targetId.toString();
        for (Tag t : list) if (s.equals(t.getAsString())) return;
        list.add(StringTag.valueOf(s));
        ptag.put(ATTACKER_TARGETS, list);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (Screen.hasShiftDown()) {

        } else {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.holdShift"));
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }
}*/
