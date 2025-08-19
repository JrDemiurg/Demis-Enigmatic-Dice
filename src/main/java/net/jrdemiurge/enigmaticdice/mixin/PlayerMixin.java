package net.jrdemiurge.enigmaticdice.mixin;

import net.jrdemiurge.enigmaticdice.attribute.ModAttributes;
import net.jrdemiurge.enigmaticdice.item.custom.Permafrost;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Unique
    private float demis_Enigmatic_Dice_1_20_1$scale = 1F;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        Player player = (Player)(Object)this;

        AttributeInstance inst = player.getAttribute(ModAttributes.SIZE_SCALE.get());
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

    @Inject(method = "getStandingEyeHeight", at = @At("RETURN"), cancellable = true)
    private void modifyEyeHeight(Pose pose, EntityDimensions size, CallbackInfoReturnable<Float> cir) {
        if (demis_Enigmatic_Dice_1_20_1$scale != 1F && demis_Enigmatic_Dice_1_20_1$scale != 0) {
            Float original = cir.getReturnValue();
            cir.setReturnValue(original * demis_Enigmatic_Dice_1_20_1$scale);
        }
    }

    @Inject(method = "tryToStartFallFlying", at = @At("HEAD"), cancellable = true)
    private void onTryToStartFallFlying(CallbackInfoReturnable<Boolean> cir) {
        Player self = (Player) (Object) this;
        UUID id = self.getUUID();

        if (Permafrost.stackMap.getOrDefault(id, 0) >= 10) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void onReadAdditionalSaveData(CompoundTag pCompound, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        UUID playerId = player.getUUID();

        float permafrostStacksFlySpeedReduction = player.getPersistentData().getFloat(Permafrost.PERSISTENT_DATA_PERMAFROST_STACKS_FLY_SPEED_REDUCTION);
        float permafrostAuraFlySpeedReduction = player.getPersistentData().getFloat(Permafrost.PERSISTENT_DATA_PERMAFROST_AURA_FLY_SPEED_REDUCTION);
        float permafrostFlySpeedRecovery = 0;

        if (permafrostStacksFlySpeedReduction != 0 && !Permafrost.stackMap.containsKey(playerId)) {
            permafrostFlySpeedRecovery += permafrostStacksFlySpeedReduction;
        }
        if (permafrostAuraFlySpeedReduction != 0 && !Permafrost.auraApplied.contains(playerId)) {
            permafrostFlySpeedRecovery += permafrostAuraFlySpeedReduction;
        }
        if (permafrostFlySpeedRecovery != 0) {
            float restoredFlySpeed = player.getAbilities().getFlyingSpeed() + permafrostFlySpeedRecovery;
            player.getAbilities().setFlyingSpeed(restoredFlySpeed);
            player.onUpdateAbilities();

            player.getPersistentData().remove(Permafrost.PERSISTENT_DATA_PERMAFROST_STACKS_FLY_SPEED_REDUCTION);
            player.getPersistentData().remove(Permafrost.PERSISTENT_DATA_PERMAFROST_AURA_FLY_SPEED_REDUCTION);
        }
    }
}

