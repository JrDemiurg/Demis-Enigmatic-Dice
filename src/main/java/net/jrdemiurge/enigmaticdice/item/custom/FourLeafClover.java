package net.jrdemiurge.enigmaticdice.item.custom;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FourLeafClover extends Item {
    public FourLeafClover(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (!pLevel.isClientSide && pEntity instanceof Player player) {

            boolean isFirstClover = false;
            for (ItemStack itemStack : player.getInventory().items) {
                if (itemStack.getItem() instanceof FourLeafClover) {
                    if (itemStack == pStack) {
                        isFirstClover = true;
                    }
                    break;
                }
            }

            if (!isFirstClover) return;

            int cloverCount = 0;

            for (ItemStack itemStack : player.getInventory().items) {
                if (itemStack.getItem() instanceof FourLeafClover) {
                    cloverCount += itemStack.getCount();
                }
            }

            if (cloverCount > 0) {
                player.addEffect(new MobEffectInstance(
                        MobEffects.LUCK,
                        100,
                        cloverCount - 1,
                        true,
                        false
                ));
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (Screen.hasShiftDown()) {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.four_leaf_clever"));
        } else {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.holdShift"));
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
