package net.jrdemiurge.enigmaticdice.item.custom;

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

public class MoonShard extends Item {

    private static final UUID GRAVITY_MODIFIER_UUID = UUID.fromString("205dfaf9-1a96-4005-9a30-5d736a87c9b4");

    public MoonShard(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (!pLevel.isClientSide && pEntity instanceof Player player) {
            if (pSlotId >= 0 && pSlotId <= 8) {
                boolean isHoldingShift = player.isShiftKeyDown();
                AttributeInstance gravityAttribute = player.getAttribute(ForgeMod.ENTITY_GRAVITY.get());

                if (gravityAttribute != null) {
                    AttributeModifier existingModifier = gravityAttribute.getModifier(GRAVITY_MODIFIER_UUID);

                    if (!isHoldingShift) {
                        if (existingModifier == null) {
                            gravityAttribute.addTransientModifier(new AttributeModifier(
                                    GRAVITY_MODIFIER_UUID,
                                    "MoonShard gravity reduction",
                                    -0.06,
                                    AttributeModifier.Operation.ADDITION
                            ));
                        }
                    } else {
                        if (existingModifier != null) {
                            gravityAttribute.removeModifier(GRAVITY_MODIFIER_UUID);
                        }
                    }
                }

                Scheduler.schedule(() -> {
                    boolean found = false;
                    for (int i = 0; i <= 8; i++) {
                        ItemStack invStack = player.getInventory().getItem(i);
                        if (invStack.is(ModItems.MOON_SHARD.get())) {
                            found = true;
                            break;
                        }
                    }

                    AttributeInstance gravityAttributeLater = player.getAttribute(ForgeMod.ENTITY_GRAVITY.get());
                    if (!found && gravityAttributeLater != null) {
                        AttributeModifier modifier = gravityAttributeLater.getModifier(GRAVITY_MODIFIER_UUID);
                        if (modifier != null) {
                            gravityAttributeLater.removeModifier(GRAVITY_MODIFIER_UUID);
                        }
                    }
                }, 10, 0);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (Screen.hasShiftDown()) {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.moon_shard_0"));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.moon_shard_1"));
            pTooltipComponents.add(Component.literal(" "));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.moon_shard_2"));
        } else {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.holdShift"));
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
