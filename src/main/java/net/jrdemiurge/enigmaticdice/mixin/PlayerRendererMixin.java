package net.jrdemiurge.enigmaticdice.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.jrdemiurge.enigmaticdice.item.ModItems;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.CuriosApi;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<Player, PlayerModel<Player>> {

    public PlayerRendererMixin(EntityRendererProvider.Context ctx, PlayerModel<Player> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    private boolean isWearingGiantRing(Player player) {
        return CuriosApi.getCuriosInventory(player)
                .map(handler -> !handler.findCurios(ModItems.GIANTS_RING.get()).isEmpty())
                .orElse(false);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void scaleSmallPlayer(AbstractClientPlayer player, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if (isWearingGiantRing(player)) {
            poseStack.pushPose();
            poseStack.scale(1.5F, 1.5F, 1.5F);
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void scaleSmallPlayerEnd(AbstractClientPlayer player, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if (isWearingGiantRing(player)) {
            poseStack.popPose();
        }
    }
}
