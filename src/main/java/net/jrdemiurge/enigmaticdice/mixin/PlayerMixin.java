package net.jrdemiurge.enigmaticdice.mixin;

import net.jrdemiurge.enigmaticdice.item.custom.GiantsRing;
import net.jrdemiurge.enigmaticdice.item.custom.Permafrost;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
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

    private boolean isSmall = false;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        Player player = (Player)(Object)this;

        boolean shouldBeSmall = GiantsRing.isWearingGiantRing(player);

        if (shouldBeSmall != isSmall) {
            isSmall = shouldBeSmall;
            this.refreshDimensions();
        }
    }

    @Inject(method = "getDimensions", at = @At("TAIL"), cancellable = true)
    public void getDimensions(Pose pPose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (isSmall) {
            EntityDimensions original = cir.getReturnValue();
            cir.setReturnValue(original.scale(1.5F));
        }
    }

    @Inject(method = "getStandingEyeHeight", at = @At("RETURN"), cancellable = true)
    private void modifyEyeHeight(Pose pose, EntityDimensions size, CallbackInfoReturnable<Float> cir) {
        Player player = (Player)(Object)this;

        if (player.getInventory() != null && GiantsRing.isWearingGiantRing(player)) {
            Float original = cir.getReturnValue();
            cir.setReturnValue(original * 1.5F);
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

