package net.jrdemiurge.enigmaticdice.item.custom;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.jrdemiurge.enigmaticdice.Config;
import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.attribute.ModAttributes;
import net.jrdemiurge.enigmaticdice.item.ModItems;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.Team;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.*;

// TODO: Если есть моды позволяющие одеть тринкетки на мобов или манекен, то убрать проверку на игрока на onEquip и onUnequip, чтобы можно было сделать манекен гигантским
// TODO убрать сопротивление отбрасыванию и вынести его в отдельный предмет с иммунитетом к коллизии, а дать скорость
public class GiantsRing extends Item implements ICurioItem {

    private static final AttributeModifier STEP_HEIGHT_BONUS = new AttributeModifier(UUID.fromString("979b021d-47b6-42dc-b68c-4c296aca0b01"), "enigmaticdice:giants_ring_step_height", 1, AttributeModifier.Operation.ADDITION);
    private static final AttributeModifier SIZE_SCALE_MULTIPLIER = new AttributeModifier(UUID.fromString("b3d7b5e8-d381-45bf-8d9f-6291e7e5913d"), "enigmaticdice:giants_ring_size_scale", 0.5, AttributeModifier.Operation.MULTIPLY_TOTAL);
    private final Map<LivingEntity, Integer> stompCooldowns = new WeakHashMap<>();

    public GiantsRing(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return true;
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (slotContext.entity().level().isClientSide || !(slotContext.entity() instanceof Player))
            return;
        AttributeInstance stepHeight = slotContext.entity().getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get());
        if (stepHeight != null && stepHeight.hasModifier(STEP_HEIGHT_BONUS)) {
            stepHeight.removeModifier(STEP_HEIGHT_BONUS);
        }
        AttributeInstance sizeScale = slotContext.entity().getAttribute(ModAttributes.SIZE_SCALE.get());
        if (sizeScale != null && sizeScale.hasModifier(SIZE_SCALE_MULTIPLIER)) {
            sizeScale.removeModifier(SIZE_SCALE_MULTIPLIER);
        }
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (slotContext.entity().level().isClientSide || !(slotContext.entity() instanceof Player))
            return;
        AttributeInstance stepHeight = slotContext.entity().getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get());
        if (stepHeight != null && !stepHeight.hasModifier(STEP_HEIGHT_BONUS)) {
            stepHeight.addTransientModifier(STEP_HEIGHT_BONUS);
        }
        AttributeInstance sizeScale = slotContext.entity().getAttribute(ModAttributes.SIZE_SCALE.get());
        if (sizeScale != null && !sizeScale.hasModifier(SIZE_SCALE_MULTIPLIER)) {
            sizeScale.addTransientModifier(SIZE_SCALE_MULTIPLIER);
        }
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity().level().isClientSide || !(slotContext.entity() instanceof Player))
            return;

        Player player = (Player) slotContext.entity();

        if (!player.isSprinting() || player.isSwimming()) return;

        int currentTick = player.tickCount;

        double totalDamage = player.getAttributeValue(Attributes.ATTACK_DAMAGE);

        ItemStack weapon = player.getMainHandItem();
        Multimap<Attribute, AttributeModifier> weaponModifiers = weapon.getAttributeModifiers(EquipmentSlot.MAINHAND);

        for (AttributeModifier mod : weaponModifiers.get(Attributes.ATTACK_DAMAGE)) {
            if (mod.getOperation() == AttributeModifier.Operation.ADDITION) {
                totalDamage -= mod.getAmount();
            }
        }

        float radius = 1f;

        AABB playerBox = player.getBoundingBox();

        AABB stompBox = new AABB(
                playerBox.minX - radius, player.getY() - 0.1, playerBox.minZ - radius,
                playerBox.maxX + radius, player.getY() + 0.4, playerBox.maxZ + radius
        );

        List<LivingEntity> victims = player.level().getEntitiesOfClass(
                LivingEntity.class, stompBox,
                e -> e != player && e.isAlive() && e.isPickable()
        );

        for (LivingEntity victim : victims) {
            Integer nextAvailableTick = stompCooldowns.getOrDefault(victim, 0);

            if (currentTick >= nextAvailableTick) {
                double playerVolume = player.getBoundingBox().getXsize() * player.getBoundingBox().getYsize() * player.getBoundingBox().getZsize();
                double victimVolume = victim.getBoundingBox().getXsize() * victim.getBoundingBox().getYsize() * victim.getBoundingBox().getZsize();

                if (playerVolume > victimVolume && checkFriendlyFire(victim, player)) {
                    victim.hurt(player.damageSources().playerAttack(player), (float) totalDamage);
                    stompCooldowns.put(victim, currentTick + 20);
                }
            }
        }
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

    public static boolean isWearingGiantRing(LivingEntity livingEntity) {
        return CuriosApi.getCuriosInventory(livingEntity)
                .map(handler -> !handler.findCurios(ModItems.GIANTS_RING.get()).isEmpty())
                .orElse(false);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> attributes = HashMultimap.create();
        attributes.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(UUID.fromString("cc5c05c0-916a-4165-bd3f-61a6b9ccfab0"), EnigmaticDice.MOD_ID+":giants_ring_damage_bonus", Config.GiantsRingAttackDamage, AttributeModifier.Operation.ADDITION));
        attributes.put(Attributes.MAX_HEALTH, new AttributeModifier(UUID.fromString("44aeda56-97d8-40d8-9c57-3727f50bea16"), EnigmaticDice.MOD_ID+":giants_ring_health_bonus", Config.GiantsRingMaxHealth, AttributeModifier.Operation.ADDITION));
        attributes.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.fromString("3c9941fd-4924-4510-9e25-0d8f420e5266"), EnigmaticDice.MOD_ID+":giants_ring_knockback_resistance_bonus", Config.GiantsRingKnockbackResistance, AttributeModifier.Operation.ADDITION));
        return attributes;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (Screen.hasShiftDown()) {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.giants_ring_1"));
            pTooltipComponents.add(Component.literal(" "));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.giants_ring_2"));
        } else {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.giants_ring_0"));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.holdShift"));
        }
    }
}
