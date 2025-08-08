package net.jrdemiurge.enigmaticdice.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.entity.custom.DragonclawHookEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class DragonclawHookRender extends EntityRenderer<DragonclawHookEntity> {
    private final DragonclawHookModel model;

    public DragonclawHookRender(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new DragonclawHookModel(pContext.bakeLayer(ModModelLayers.DRAGONCLAW_HOOK_LAYER));
    }

    @Override
    public ResourceLocation getTextureLocation(DragonclawHookEntity pEntity) {
        return new ResourceLocation(EnigmaticDice.MOD_ID, "textures/entity/dragonclaw_hook.png");
    }

    @Override
    public void render(DragonclawHookEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();

        float yRot = Mth.lerp(pPartialTick, pEntity.yRotO, pEntity.getYRot());
        float xRot = Mth.lerp(pPartialTick, pEntity.xRotO, pEntity.getXRot());

        pPoseStack.mulPose(Axis.YP.rotationDegrees(yRot - 90.0F));
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(xRot + 90.0F));
        pPoseStack.scale(0.65F, 0.65F, 0.65F);
        VertexConsumer vertexConsumer = pBuffer.getBuffer(this.model.renderType(getTextureLocation(pEntity)));
        model.renderToBuffer(pPoseStack, vertexConsumer, pPackedLight, OverlayTexture.NO_OVERLAY,1,1,1,1);

        pPoseStack.popPose();
    }
}
