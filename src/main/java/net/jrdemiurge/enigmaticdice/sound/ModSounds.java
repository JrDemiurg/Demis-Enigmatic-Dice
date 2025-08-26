package net.jrdemiurge.enigmaticdice.sound;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, EnigmaticDice.MOD_ID);

    public static final RegistryObject<SoundEvent> UNEQUAL_EXCHANGE_HIT = registerSoundEvents("unequal_exchange_hit");

    public static final RegistryObject<SoundEvent> SOUL_EATER_CHARGED_HIT = registerSoundEvents("soul_eater_charged_hit");

    public static final RegistryObject<SoundEvent> PERMAFROST_FROZEN = registerSoundEvents("permafrost_frozen");

    public static final RegistryObject<SoundEvent> PERMAFROST_UNFROZEN = registerSoundEvents("permafrost_unfrozen");

    public static final RegistryObject<SoundEvent> CRUCIBLE_OF_RILE_PREATTACK = registerSoundEvents("crucible_of_rile_preattack");

    public static final RegistryObject<SoundEvent> CRUCIBLE_OF_RILE_ATTACK = registerSoundEvents("crucible_of_rile_attack");

    public static final RegistryObject<SoundEvent> CRUCIBLE_OF_RILE_USE = registerSoundEvents("crucible_of_rile_use");

    public static final RegistryObject<SoundEvent> CRUCIBLE_OF_RILE_HELIX = registerSoundEvents("crucible_of_rile_helix");

    private static RegistryObject<SoundEvent> registerSoundEvents(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(EnigmaticDice.MOD_ID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
