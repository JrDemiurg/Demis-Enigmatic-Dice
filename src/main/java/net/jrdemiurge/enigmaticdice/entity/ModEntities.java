package net.jrdemiurge.enigmaticdice.entity;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.entity.custom.DragonclawHookEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, EnigmaticDice.MOD_ID);

    public static final RegistryObject<EntityType<DragonclawHookEntity>> DRAGONCLAW_HOOK =
            ENTITY_TYPES.register("dragonclaw_hook", () -> EntityType.Builder.<DragonclawHookEntity>of(DragonclawHookEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).build("dragonclaw_hook"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
