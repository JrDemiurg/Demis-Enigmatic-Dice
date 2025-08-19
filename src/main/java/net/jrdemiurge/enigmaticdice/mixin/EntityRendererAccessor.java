package net.jrdemiurge.enigmaticdice.mixin;

import net.minecraft.client.renderer.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityRenderer.class)
public interface EntityRendererAccessor {
    @Accessor("shadowRadius")
    float getShadowRadius();

    @Accessor("shadowRadius")
    void setShadowRadius(float value);
}
