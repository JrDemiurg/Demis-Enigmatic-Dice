package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Map;

public class PermanentBuffEvent implements RandomEvent {
    private static final Map<MobEffect, String> EFFECT_MESSAGES = Map.of(
            // MobEffects.LUCK, "enigmaticdice.effect.luck",
            MobEffects.DAMAGE_BOOST, "enigmaticdice.effect.damage_boost",
            MobEffects.MOVEMENT_SPEED, "enigmaticdice.effect.movement_speed",
            MobEffects.DIG_SPEED, "enigmaticdice.effect.dig_speed",
            MobEffects.HEALTH_BOOST, "enigmaticdice.effect.health_boost",
            MobEffects.NIGHT_VISION, "enigmaticdice.effect.night_vision",
            MobEffects.INVISIBILITY, "enigmaticdice.effect.invisibility",
            MobEffects.FIRE_RESISTANCE, "enigmaticdice.effect.fire_resistance",
            MobEffects.REGENERATION, "enigmaticdice.effect.regeneration"
    );

    private static final int MAX_LEVEL = 2;
    private static final int MAX_LEVEL_SINGLE = 1;
    private final int rarity;

    public PermanentBuffEvent(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!RandomEvent.rollChance(pLevel, pPlayer, rarity, true)) return false;
        }

        MobEffect[] effects = EFFECT_MESSAGES.keySet().toArray(new MobEffect[0]);

        for (int i = 0; i < effects.length; i++) {
            MobEffect effect = effects[pLevel.getRandom().nextInt(effects.length)];
            MobEffectInstance existingEffect = pPlayer.getEffect(effect);
            int maxLevel = (effect == MobEffects.NIGHT_VISION || effect == MobEffects.FIRE_RESISTANCE || effect == MobEffects.INVISIBILITY) ? MAX_LEVEL_SINGLE : MAX_LEVEL;

            if (existingEffect == null) {
                pPlayer.addEffect(new MobEffectInstance(effect, MobEffectInstance.INFINITE_DURATION, 0, false, false));
                pPlayer.sendSystemMessage(Component.translatable(EFFECT_MESSAGES.get(effect)));
                return true;
            } else if (existingEffect.getAmplifier() < maxLevel - 1) {
                pPlayer.addEffect(new MobEffectInstance(effect, MobEffectInstance.INFINITE_DURATION, existingEffect.getAmplifier() + 1, false, false));
                pPlayer.sendSystemMessage(Component.translatable(EFFECT_MESSAGES.get(effect)));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean simulationExecute(Level pLevel, Player pPlayer) {
        return RandomEvent.rollChance(pLevel, pPlayer, rarity, true);
    }
}

