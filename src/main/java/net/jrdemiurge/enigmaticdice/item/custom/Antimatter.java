package net.jrdemiurge.enigmaticdice.item.custom;

import net.jrdemiurge.enigmaticdice.item.ModItems;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Antimatter extends Item {
    public Antimatter(Properties pProperties) {
        super(pProperties);
    }

    public static void init() {
        DispenserBlock.registerBehavior(ModItems.ANTIMATTER.get(), new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                Level level = source.getLevel();
                Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
                BlockPos targetPos = source.getPos().relative(direction);

                if (!level.isClientSide) {
                    level.destroyBlock(targetPos, false);

                    level.explode(null,
                            targetPos.getX() + 0.5,
                            targetPos.getY() + 0.5,
                            targetPos.getZ() + 0.5,
                            10F,
                            false,
                            Level.ExplosionInteraction.NONE);

                    stack.shrink(1);
                }

                return stack;
            }
        });
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Player player = pContext.getPlayer();
        Level level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();
        ItemStack stack = pContext.getItemInHand();

        if (player != null && player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                level.destroyBlock(pos, false);

                level.explode(null,
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        10F,
                        false,
                        Level.ExplosionInteraction.NONE);

                stack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (Screen.hasShiftDown()) {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.antimatter.1"));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.antimatter.2"));
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.antimatter.3"));
        } else {
            pTooltipComponents.add(Component.translatable("tooltip.enigmaticdice.holdShift"));
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
