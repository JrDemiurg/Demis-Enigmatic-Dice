package net.jrdemiurge.enigmaticdice.item.custom;

import net.jrdemiurge.enigmaticdice.Config;
import net.jrdemiurge.enigmaticdice.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// надо сгенерить звук для щита или попросить людей его сгенерить
public class DivineShield extends Item implements ICurioItem {

    private static final Map<UUID, Long> activeImmunity = new HashMap<>();

    public DivineShield(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return true;
    }

    public static boolean isWearingDivineShield(LivingEntity livingEntity) {
        return CuriosApi.getCuriosInventory(livingEntity)
                .map(handler -> !handler.findCurios(ModItems.DIVINE_SHIELD.get()).isEmpty())
                .orElse(false);
    }

    public static boolean isOnCooldown(LivingEntity entity) {
        if (entity instanceof Player player) {
            return player.getCooldowns().isOnCooldown(ModItems.DIVINE_SHIELD.get());
        }
        return false;
    }

    public static void triggerCooldown(LivingEntity entity) {
        if (!(entity instanceof Player player)) return;

        player.getCooldowns().addCooldown(ModItems.DIVINE_SHIELD.get(), Config.DivineShieldCooldownTicks);
    }

    public static boolean hasActiveImmunity(LivingEntity entity) {
        long gameTime = entity.level().getGameTime();
        return activeImmunity.getOrDefault(entity.getUUID(), 0L) > gameTime;
    }

    public static void giveImmunity(LivingEntity entity) {
        long endTime = entity.level().getGameTime() + Config.DivineShieldImmunityTicks;
        activeImmunity.put(entity.getUUID(), endTime);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (Screen.hasShiftDown()) {
            String formattedCooldown = String.format("%.1f", Config.DivineShieldCooldownTicks / 20F);
            String formattedImmunityTime = String.format("%.1f", Config.DivineShieldImmunityTicks / 20F);

            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.divine_shield_1", formattedCooldown)
                    .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.divine_shield_2", formattedImmunityTime)
                    .withStyle(ChatFormatting.GOLD));
        } else {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.divine_shield_0"));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.holdShift"));
        }
    }
}
