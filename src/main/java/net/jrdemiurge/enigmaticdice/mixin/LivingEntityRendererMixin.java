package net.jrdemiurge.enigmaticdice.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.jrdemiurge.enigmaticdice.attribute.ModAttributes;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>>
        extends EntityRenderer<T> {

    protected LivingEntityRendererMixin(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Unique private float enigmatic$baseShadowRadius = Float.NaN;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInIt(EntityRendererProvider.Context pContext, EntityModel pModel, float pShadowRadius, CallbackInfo ci) {
        this.enigmatic$baseShadowRadius = pShadowRadius;
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void modifyRenderSize(LivingEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        AttributeInstance inst = entity.getAttribute(ModAttributes.SIZE_SCALE.get());
        if (inst == null) return;
        float scale = (float) inst.getValue();
        if (scale != 1F) {
            poseStack.pushPose();
            poseStack.scale(scale, scale, scale);
        }

        ((EntityRendererAccessor) this).setShadowRadius(enigmatic$baseShadowRadius * scale);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void onRenderEnd(LivingEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        AttributeInstance inst = entity.getAttribute(ModAttributes.SIZE_SCALE.get());
        if (inst == null) return;
        float scale = (float) inst.getValue();

        if (scale != 1F) {
            poseStack.popPose();
        }
    }
}
