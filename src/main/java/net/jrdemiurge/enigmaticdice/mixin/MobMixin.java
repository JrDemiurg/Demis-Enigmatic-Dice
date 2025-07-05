package net.jrdemiurge.enigmaticdice.mixin;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.ISlotType;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Map;
import java.util.Set;

@Mixin(Mob.class)
public abstract class MobMixin {

    /*@Inject(method = "equipItemIfPossible", at = @At("HEAD"), cancellable = true)
    private void equipCurioIfPossible(ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        Mob mob = (Mob) (Object) this;

        if (!(stack.getItem() instanceof ICurioItem curioItem)) return;

        Map<String, ISlotType> map = CuriosApi.getItemStackSlots(stack, mob);
        Set<String> slotTypes = map.keySet();

        CuriosApi.getCuriosInventory(mob).ifPresent(handler -> {
            for (Map.Entry<String, ICurioStacksHandler> entry : handler.getCurios().entrySet()) {
                String identifier = entry.getKey();
                ICurioStacksHandler stacksHandler = entry.getValue();
                int slots = stacksHandler.getSlots();

                for (int i = 0; i < slots; i++) {
                    ItemStack inSlot = stacksHandler.getStacks().getStackInSlot(i);

                    if (inSlot.isEmpty()) {
                        SlotContext context = new SlotContext(identifier, mob, i, false, true);

                        if (curioItem.canEquip(context, stack) && (slotTypes.contains(identifier) || slotTypes.contains("curio"))) {
                            curioItem.onEquip(context, ItemStack.EMPTY, stack);
                            handler.setEquippedCurio(identifier, i, stack);
                            cir.setReturnValue(stack);
                            return;
                        }
                    }
                }
            }
        });
    }*/
}