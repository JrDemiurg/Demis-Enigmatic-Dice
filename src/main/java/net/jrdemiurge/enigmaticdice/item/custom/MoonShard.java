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
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;
import java.util.UUID;
import java.util.WeakHashMap;

public class MoonShard extends Item {

    private static final UUID GRAVITY_MODIFIER_UUID = UUID.fromString("205dfaf9-1a96-4005-9a30-5d736a87c9b4");
    private static final WeakHashMap<Player, Boolean> gravityDisable = new WeakHashMap<>();

    public MoonShard(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (!pLevel.isClientSide && pEntity instanceof Player player) {
            if (pSlotId >= 0 && pSlotId <= 8) {
                boolean isSneaking = player.isShiftKeyDown();
                AttributeInstance gravityAttribute = player.getAttribute(ForgeMod.ENTITY_GRAVITY.get());

                if (gravityAttribute != null) {
                    AttributeModifier existingModifier = gravityAttribute.getModifier(GRAVITY_MODIFIER_UUID);

                    if (player.getMainHandItem().is(ModItems.MOON.get()) || player.getOffhandItem().is(ModItems.MOON.get())){
                        if (existingModifier != null) {
                            gravityAttribute.removeModifier(GRAVITY_MODIFIER_UUID);
                        }
                        return;
                    }

                    if (isWearingGravityCore(player)){
                        boolean gravityDisabled = gravityDisable.getOrDefault(player, false);

                        if (isSneaking && !player.onGround() && !gravityDisabled) {
                            gravityDisable.put(player, true);
                            if (existingModifier != null) {
                                gravityAttribute.removeModifier(GRAVITY_MODIFIER_UUID);
                            }
                        }

                        if (player.onGround()) {
                            gravityDisable.put(player, false);
                            if (existingModifier == null) {
                                gravityAttribute.addTransientModifier(new AttributeModifier(
                                        GRAVITY_MODIFIER_UUID,
                                        "MoonShard gravity reduction",
                                        Config.MoonShardGravityReduction,
                                        AttributeModifier.Operation.ADDITION
                                ));
                            }
                        }
                    } else {
                        if (!isSneaking) {
                            if (existingModifier == null) {
                                gravityAttribute.addTransientModifier(new AttributeModifier(
                                        GRAVITY_MODIFIER_UUID,
                                        "MoonShard gravity reduction",
                                        Config.MoonShardGravityReduction,
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
                }, 10);
            }
        }
    }

    private static boolean isWearingGravityCore(Player player) {
        return CuriosApi.getCuriosInventory(player)
                .map(handler -> !handler.findCurios(ModItems.GRAVITY_CORE.get()).isEmpty())
                .orElse(false);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (Screen.hasShiftDown()) {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.while_hotbar"));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.moon_shard_1"));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.moon_shard_2"));
            pTooltipComponents.add(Component.literal(" "));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.moon_shard_3"));
        } else {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.holdShift"));
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
