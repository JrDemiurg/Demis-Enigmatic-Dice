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
                        output.accept(ModItems.UNEQUAL_EXCHANGE.get());
                        output.accept(ModItems.SOUL_EATER.get());
                        output.accept(ModItems.PERMAFROST.get());
                        output.accept(ModItems.CRUCIBLE_OF_RILE.get());
                        output.accept(ModItems.FOUR_LEAF_CLEVER.get());
                        output.accept(ModItems.ANTIMATTER.get());
                        output.accept(ModItems.GIANTS_RING.get());
                        output.accept(ModItems.MOON_SHARD.get());
                        output.accept(ModItems.MOON.get());
                        output.accept(ModItems.GRAVITY_CORE.get());
                        output.accept(ModItems.RING_OF_AGILITY.get());
                        output.accept(ModItems.DIVINE_SHIELD.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        MOD_CREATIVE_TABS.register(eventBus);
    }
}
