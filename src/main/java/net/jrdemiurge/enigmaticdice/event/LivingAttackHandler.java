package net.jrdemiurge.enigmaticdice.event;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.custom.DivineShield;
import net.jrdemiurge.enigmaticdice.item.custom.RingOfAgility;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnigmaticDice.MOD_ID)
public class LivingAttackHandler {

    @SubscribeEvent
    public static void onPlayerHurt(LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide) return;

        if (RingOfAgility.isWearingRingOfAgility(entity) && RingOfAgility.shouldDodge(entity)) {
            event.setCanceled(true);
            return;
        }

        if (DivineShield.isWearingDivineShield(entity)) {
            if (DivineShield.hasActiveImmunity(entity)) {
                event.setCanceled(true);
                return;
            }
            if (!DivineShield.isOnCooldown(entity)) {
                event.setCanceled(true);
                DivineShield.giveImmunity(entity);
                DivineShield.triggerCooldown(entity);
                return;
            }
        }
    }
}
