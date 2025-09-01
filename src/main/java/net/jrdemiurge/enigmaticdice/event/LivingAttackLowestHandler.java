package net.jrdemiurge.enigmaticdice.event;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.custom.CrucibleOfRile;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnigmaticDice.MOD_ID)
public class LivingAttackLowestHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingAttack(LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();
        Entity src = event.getSource().getEntity();
        double dmg = event.getAmount();

        if (entity.level().isClientSide) return;

        if (CrucibleOfRile.isHeldMainHand(entity) && !event.isCanceled()) {
            if (src != null && src != entity && dmg > 1) {
                CrucibleOfRile.handleOnOwnerAttacked(entity);
            }
        }
    }
}
