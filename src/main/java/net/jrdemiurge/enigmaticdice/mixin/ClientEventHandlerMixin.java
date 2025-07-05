package net.jrdemiurge.enigmaticdice.mixin;

import net.jrdemiurge.enigmaticdice.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.client.ClientEventHandler;

import java.util.List;

@Pseudo
@Mixin(ClientEventHandler.class)
public abstract class ClientEventHandlerMixin{

    @Inject(method = "onTooltip", at = @At("TAIL"), remap = false)
    private void onTick(ItemTooltipEvent evt, CallbackInfo ci) {
        ItemStack stack = evt.getItemStack();

        if (stack.getItem() == ModItems.GIANTS_RING.get()
                || stack.getItem() == ModItems.RING_OF_AGILITY.get()) {
            List<Component> tooltip = evt.getToolTip();

            if (tooltip.size() > 1) {
                tooltip.remove(1);
            }
        }
    }
}

