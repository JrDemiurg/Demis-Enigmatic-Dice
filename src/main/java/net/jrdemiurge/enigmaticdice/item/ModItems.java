package net.jrdemiurge.enigmaticdice.item;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.custom.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, EnigmaticDice.MOD_ID);

    public static final RegistryObject<Item> ENIGAMTIC_DIE = ITEMS.register("enigmatic_die",
            () -> new EnigmaticDie(new Item.Properties()));

    public static final RegistryObject<Item> UNEQUAL_EXCHANGE = ITEMS.register("unequal_exchange",
            () -> new UnequalExchange(Tiers.NETHERITE, -4, -2.4F, new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> SOUL_EATER = ITEMS.register("soul_eater",
            () -> new SoulEater(Tiers.NETHERITE, 5, -2F, new Item.Properties().fireResistant()));

    public static final RegistryObject<Item> ANTIMATTER = ITEMS.register("antimatter",
            () -> new Antimatter(new Item.Properties()));

    public static final RegistryObject<Item> FOUR_LEAF_CLEVER = ITEMS.register("four_leaf_clever",
            () -> new FourLeafClover(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> GIANTS_RING = ITEMS.register("giants_ring",
            () -> new GiantsRing(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> MOON_SHARD = ITEMS.register("moon_shard",
            () -> new MoonShard(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> GRAVITY_CORE = ITEMS.register("gravity_core",
            () -> new GravityCore(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> MOON = ITEMS.register("moon",
            () -> new Moon(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
