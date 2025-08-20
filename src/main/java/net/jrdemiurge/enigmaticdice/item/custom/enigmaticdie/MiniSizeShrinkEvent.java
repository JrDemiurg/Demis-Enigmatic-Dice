package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.jrdemiurge.enigmaticdice.attribute.ModAttributes;
import net.jrdemiurge.enigmaticdice.scheduler.Scheduler;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;


public class MiniSizeShrinkEvent extends RandomEvent {
    private static final AttributeModifier MINI_SIZE_SCALE_MULTIPLIER = new AttributeModifier(UUID.fromString("848682fe-ea22-4185-a838-146c328f6bc2"), "enigmaticdice:mini_size_scale", -0.5, AttributeModifier.Operation.MULTIPLY_TOTAL);

    public MiniSizeShrinkEvent(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!rollChance(pLevel, pPlayer, rarity)) return false;
        }

        int debuffDuration = 20 * 60 * 10;

        AttributeInstance sizeScaleAttribute = pPlayer.getAttribute(ModAttributes.SIZE_SCALE.get());
        if (sizeScaleAttribute == null) return false;

        if (sizeScaleAttribute.hasModifier(MINI_SIZE_SCALE_MULTIPLIER)) return false;
        if (sizeScaleAttribute.hasModifier(AntSizeShrinkEvent.ANT_SIZE_SCALE_MULTIPLIER)) return false;

        sizeScaleAttribute.addTransientModifier(MINI_SIZE_SCALE_MULTIPLIER);

        Scheduler.schedule(() -> {
            sizeScaleAttribute.removeModifier(MINI_SIZE_SCALE_MULTIPLIER);
        }, debuffDuration);

        MutableComponent message = Component.translatable("enigmaticdice.event.mini_size_shrink");
        pPlayer.displayClientMessage(message, false);
        return true;
    }
}
