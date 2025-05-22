package net.jrdemiurge.enigmaticdice.mixin;

import artifacts.client.mimic.MimicRenderer;
import artifacts.entity.MimicEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(MimicRenderer.class)
public abstract class MimicRendererMixin {

    @Inject(method = "render", at = @At("HEAD"), require = 0, remap = false)
    private void modifyRenderSize(MimicEntity mimic, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if ("Minic".equalsIgnoreCase(mimic.getName().getString())) {
            matrixStack.pushPose();
            matrixStack.scale(0.5F, 0.5F, 0.5F); // Уменьшаем модель в 2 раза
        }
    }

    @Inject(method = "render", at = @At("RETURN"), remap = false)
    private void onRenderEnd(MimicEntity mimic, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if ("Minic".equalsIgnoreCase(mimic.getName().getString())) {
            matrixStack.popPose();
        }
    }
}
