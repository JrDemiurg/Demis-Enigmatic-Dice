package net.jrdemiurge.enigmaticdice.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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

public class UnfinishedWeapon extends SwordItem {

    public UnfinishedWeapon(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack item = pPlayer.getItemInHand(pUsedHand);

        if (pLevel.isClientSide) return InteractionResultHolder.pass(item);

        MutableComponent prefix = Component.translatable("tooltip.enigmaticdice.unfinished_item_discord");

        MutableComponent link = Component.literal("Discord")
                .withStyle(Style.EMPTY
                        .withColor(ChatFormatting.BLUE)
                        .withUnderlined(true)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/ZMKnAB92GZ")));

        pPlayer.displayClientMessage(prefix.append(" ").append(link), false);
        return InteractionResultHolder.success(item);
    }

    // Возможно тогда в будущем реально придётся создать место DemiurgesForge в игре, ультра редкое место и там есть все добавленные и не добавленные предметы, возможно это полигон
    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (Screen.hasShiftDown()) {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.unfinished_item_1"));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.unfinished_item_2"));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.unfinished_item_3"));
            pTooltipComponents.add(Component.literal(" "));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.unfinished_item_4"));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.unfinished_item_5"));
        } else {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.holdShift"));
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
