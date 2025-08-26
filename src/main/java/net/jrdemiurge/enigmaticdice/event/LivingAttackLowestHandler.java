package net.jrdemiurge.enigmaticdice.event;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.custom.CrucibleOfRile;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnigmaticDice.MOD_ID)
public class LivingAttackLowestHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerHurt(LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide) return;

        if (CrucibleOfRile.isHeldMainHand(entity) && !event.isCanceled()) {
            if (event.getSource().getEntity() != null &&
                    event.getSource().getEntity() != event.getEntity() &&
                    event.getAmount() > 1) {
                CrucibleOfRile.handleOnOwnerAttacked(entity);
            }
        }
    }
}
