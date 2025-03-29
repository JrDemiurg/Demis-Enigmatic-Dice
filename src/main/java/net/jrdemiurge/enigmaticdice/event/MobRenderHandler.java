package net.jrdemiurge.enigmaticdice.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnigmaticDice.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class MobRenderHandler {
    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Pre<?, ?> event) {
        Entity entity = event.getEntity();

        // Проверяем, является ли моб тем, которого мы хотим уменьшить
        if (shouldScale(entity)) {
            PoseStack poseStack = event.getPoseStack();
            poseStack.pushPose(); // Сохраняем текущее состояние

            float scale = 0.75f; // Уменьшаем размер в 2 раза
            poseStack.scale(scale, scale, scale);
        }
    }

    @SubscribeEvent
    public static void onRenderLivingPost(RenderLivingEvent.Post<?, ?> event) {
        Entity entity = event.getEntity();

        if (shouldScale(entity)) {
            PoseStack poseStack = event.getPoseStack();
            poseStack.popPose(); // Восстанавливаем состояние после рендера
        }
    }

    // Здесь можно задать условие для изменения размера
    private static boolean shouldScale(Entity entity) {
        return entity.hasCustomName() && "Minic".equals(entity.getCustomName().getString()) && entity.getType() == EntityType.byString("artifacts:mimic").orElse(null);
    }
}
