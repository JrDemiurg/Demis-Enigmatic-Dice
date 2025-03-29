package net.jrdemiurge.enigmaticdice.item;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> MOD_CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EnigmaticDice.MOD_ID);

    public static final RegistryObject<CreativeModeTab> ENIGMATICDICE_TAB = MOD_CREATIVE_TABS.register("enigmaticdice_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.ENIGAMTIC_DIE.get()))
                    .title(Component.translatable("creativetab.enigmaticdice_tab"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.ENIGAMTIC_DIE.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        MOD_CREATIVE_TABS.register(eventBus);
    }
}
