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


public class AntSizeShrinkEvent extends RandomEvent {
    public static final AttributeModifier ANT_SIZE_SCALE_MULTIPLIER = new AttributeModifier(UUID.fromString("3928ecf0-caed-43ed-a698-a289480df808"), "enigmaticdice:ant_size_scale", -0.9, AttributeModifier.Operation.MULTIPLY_TOTAL);

    public AntSizeShrinkEvent(int rarity) {
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

        if (sizeScaleAttribute.hasModifier(ANT_SIZE_SCALE_MULTIPLIER)) return false;

        sizeScaleAttribute.addTransientModifier(ANT_SIZE_SCALE_MULTIPLIER);

        Scheduler.schedule(() -> {
            sizeScaleAttribute.removeModifier(ANT_SIZE_SCALE_MULTIPLIER);
        }, debuffDuration);

        MutableComponent message = Component.translatable("enigmaticdice.event.ant_size_shrink");
        pPlayer.displayClientMessage(message, false);
        return true;
    }
}
