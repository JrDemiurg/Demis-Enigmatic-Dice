package net.jrdemiurge.enigmaticdice.effect;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.effect.custom.SoulEaterChargedHit;
import net.jrdemiurge.enigmaticdice.effect.custom.SoulEaterHealthBoost;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, EnigmaticDice.MOD_ID);

    public static final RegistryObject<MobEffect> SOUL_EATER_CHARGED_HIT = MOB_EFFECTS.register("soul_eater_charged_hit",
            () -> new SoulEaterChargedHit(MobEffectCategory.BENEFICIAL, 0x5084d4));

    public static final RegistryObject<MobEffect> SOUL_EATER_HEALTH_BOOST = MOB_EFFECTS.register("soul_eater_health_boost",
            () -> new SoulEaterHealthBoost(MobEffectCategory.BENEFICIAL, 0x72cee1));

    public static final RegistryObject<MobEffect> UNEQUAL_EXCHANGE_DEBUFFS = MOB_EFFECTS.register("unequal_exchange_debuffs",
            () -> new SoulEaterHealthBoost(MobEffectCategory.NEUTRAL, 0x411214));

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
