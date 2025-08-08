package net.jrdemiurge.enigmaticdice.mixin;

import artifacts.entity.MimicEntity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(MimicEntity.class)
public abstract class MimicEntityMixin extends Mob {

    protected MimicEntityMixin(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    private boolean dimensionsRefreshed = false;

    @Inject(method = "tick", at = @At("TAIL"))
    private void injectRefreshDimensions(CallbackInfo ci) {
        if (!dimensionsRefreshed &&
                ("Minic".equalsIgnoreCase(this.getName().getString()) || "Dummy Chest".equalsIgnoreCase(this.getName().getString()))) {
            this.refreshDimensions();
            dimensionsRefreshed = true;
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        if ("Minic".equalsIgnoreCase(this.getName().getString())) {
            return super.getDimensions(pose).scale(0.5F);
        }
        if ("Dummy Chest".equalsIgnoreCase(this.getName().getString())) {
            return super.getDimensions(pose).scale(2F);
        }
        return super.getDimensions(pose);
    }
}
