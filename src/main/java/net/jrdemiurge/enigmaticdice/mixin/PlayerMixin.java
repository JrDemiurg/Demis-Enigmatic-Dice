package net.jrdemiurge.enigmaticdice.mixin;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    private boolean isSmall = false;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        ItemStack mainHand = this.getMainHandItem();

        // Замените YourShrinkItem на ваш предмет
        boolean shouldBeSmall = mainHand.is(Items.STICK);

        if (shouldBeSmall != isSmall) {
            isSmall = shouldBeSmall;
            this.refreshDimensions();
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        EntityDimensions base = super.getDimensions(pose);
        if (isSmall) {
            return base.scale(0.5F);
        }
        return base;
    }

    @Inject(method = "getStandingEyeHeight", at = @At("HEAD"), cancellable = true)
    private void modifyEyeHeight(Pose pose, EntityDimensions size, CallbackInfoReturnable<Float> cir) {
        Player player = (Player)(Object)this;

        // Пример: если держит палку — уменьшаем точку обзора
        if (player.getInventory() != null && player.getMainHandItem().is(Items.STICK)) {
            switch (pose) {
                case SWIMMING:
                case FALL_FLYING:
                case SPIN_ATTACK:
                    cir.setReturnValue(0.2F); // половина от 0.4F
                    break;
                case CROUCHING:
                    cir.setReturnValue(0.635F); // половина от 1.27F
                    break;
                default:
                    cir.setReturnValue(0.81F); // половина от 1.62F
                    break;
            }
        }
    }
}

