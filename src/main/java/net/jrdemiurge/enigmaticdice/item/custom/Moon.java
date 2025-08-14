package net.jrdemiurge.enigmaticdice.item.custom;

import net.jrdemiurge.enigmaticdice.Config;
import net.jrdemiurge.enigmaticdice.item.ModItems;
import net.jrdemiurge.enigmaticdice.scheduler.Scheduler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class Moon extends Item {

    private static final UUID GRAVITY_MODIFIER_UUID = UUID.fromString("1fa5c3f2-4e37-44e2-a618-d747870fbd05");

    public Moon(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (!pLevel.isClientSide && pEntity instanceof Player player) {
            boolean isSneaking = player.isShiftKeyDown();
            boolean isInMainHand = player.getMainHandItem().is(ModItems.MOON.get());
            boolean isInOffHand = player.getOffhandItem().is(ModItems.MOON.get());
            AttributeInstance gravityAttribute = player.getAttribute(ForgeMod.ENTITY_GRAVITY.get());

            if (gravityAttribute != null) {
                AttributeModifier existingModifier = gravityAttribute.getModifier(GRAVITY_MODIFIER_UUID);

                if (isInMainHand || isInOffHand) {
                    if (!isSneaking) {
                        if (existingModifier == null) {
                            gravityAttribute.addTransientModifier(new AttributeModifier(
                                    GRAVITY_MODIFIER_UUID,
                                    "Moon gravity reduction",
                                    Config.MoonGravityReduction,
                                    AttributeModifier.Operation.ADDITION
                            ));
                        }
                    } else {
                        if (existingModifier != null) {
                            gravityAttribute.removeModifier(GRAVITY_MODIFIER_UUID);
                        }
                    }
                }
            }

            Scheduler.schedule(() -> {
                boolean stillHolding = player.getMainHandItem().is(ModItems.MOON.get()) || player.getOffhandItem().is(ModItems.MOON.get());
                AttributeInstance gravityAttributeLater = player.getAttribute(ForgeMod.ENTITY_GRAVITY.get());

                if (!stillHolding && gravityAttributeLater != null) {
                    AttributeModifier modifier = gravityAttributeLater.getModifier(GRAVITY_MODIFIER_UUID);
                    if (modifier != null) {
                        gravityAttributeLater.removeModifier(GRAVITY_MODIFIER_UUID);
                    }
                }
            }, 4, 0);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (Screen.hasShiftDown()) {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.while_held"));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.moon_1"));
            pTooltipComponents.add(Component.literal(" "));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.while_hotbar"));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.moon_2"));
            pTooltipComponents.add(Component.literal(" "));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.moon_3"));
        } else {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.holdShift"));
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
