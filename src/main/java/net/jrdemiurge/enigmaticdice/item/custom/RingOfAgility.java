package net.jrdemiurge.enigmaticdice.item.custom;

import net.jrdemiurge.enigmaticdice.Config;
import net.jrdemiurge.enigmaticdice.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
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

import java.util.List;
import java.util.Random;

// работает у мобов
public class RingOfAgility extends Item implements ICurioItem {
    private static final Random RANDOM = new Random();

    public RingOfAgility(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return true;
    }

    public static boolean isWearingRingOfAgility(LivingEntity livingEntity) {
        return CuriosApi.getCuriosInventory(livingEntity)
                .map(handler -> !handler.findCurios(ModItems.RING_OF_AGILITY.get()).isEmpty())
                .orElse(false);
    }

    public static boolean shouldDodge(LivingEntity livingEntity) {
        var movementSpeedAttr = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeedAttr == null) return false;

        double movementSpeed = movementSpeedAttr.getValue();
        double scale = Config.RingOfAgilityChanceScale;
        double maxChance = Config.RingOfAgilityMaxDodgeChance;

        double dodgeChance = 1.0 - Math.pow(0.99, movementSpeed * 100 * scale);
        dodgeChance = Math.min(dodgeChance, maxChance);

        return RANDOM.nextDouble() < dodgeChance;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (Screen.hasShiftDown()) {
            double dodgeChance;
            if (Minecraft.getInstance().player != null) {
                var movementSpeedAttr = Minecraft.getInstance().player.getAttribute(Attributes.MOVEMENT_SPEED);
                double movementSpeed = movementSpeedAttr.getValue();
                double scale = Config.RingOfAgilityChanceScale;
                double maxChance = Config.RingOfAgilityMaxDodgeChance;
                dodgeChance = 1.0 - Math.pow(0.99, movementSpeed * 100 * scale);
                dodgeChance = Math.min(dodgeChance, maxChance);
            } else {
                dodgeChance = 0.1;
            }
            String formattedChance = String.format("%.1f", dodgeChance * 100);

            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.ring_of_agility_1", formattedChance)
                        .withStyle(ChatFormatting.GOLD));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.ring_of_agility_2"));
        } else {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.ring_of_agility_0"));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.holdShift"));
        }
    }
}
