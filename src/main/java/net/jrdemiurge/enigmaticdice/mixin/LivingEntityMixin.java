package net.jrdemiurge.enigmaticdice.mixin;

import net.jrdemiurge.enigmaticdice.attribute.ModAttributes;
import net.jrdemiurge.enigmaticdice.item.custom.MoaiCharm;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    protected LivingEntityMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Unique
    private float demis_Enigmatic_Dice_1_20_1$scale = 1F;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity)(Object)this;

        AttributeInstance inst = livingEntity.getAttribute(ModAttributes.SIZE_SCALE.get());
        if (inst == null) return;
        float newScale = (float) inst.getValue();

        if (demis_Enigmatic_Dice_1_20_1$scale != newScale) {
            demis_Enigmatic_Dice_1_20_1$scale = newScale;
            this.refreshDimensions();
        }
    }

    @Inject(method = "getDimensions", at = @At("TAIL"), cancellable = true)
    public void getDimensions(Pose pPose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (demis_Enigmatic_Dice_1_20_1$scale != 1F) {
            EntityDimensions original = cir.getReturnValue();
            cir.setReturnValue(original.scale(demis_Enigmatic_Dice_1_20_1$scale));
        }
    }

    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
    public void onIsPushable(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof Player player && MoaiCharm.isWearingMoaiCharm(player)) {
            cir.setReturnValue(false);
        }
    }
}