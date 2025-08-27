package net.jrdemiurge.enigmaticdice.event;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.ModItems;
import net.jrdemiurge.enigmaticdice.item.custom.CrucibleOfRile;
import net.jrdemiurge.enigmaticdice.sound.ModSounds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;

@Mod.EventBusSubscriber(modid = EnigmaticDice.MOD_ID)
public class LivingDeathLowestHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.getPersistentData().getBoolean("enigmaticdice_should_die") && event.isCanceled()) {
            event.setCanceled(false);
            entity.setHealth(0);
        }

        if (event.getSource().getDirectEntity() instanceof ServerPlayer attacker) {
            ItemStack weapon = attacker.getMainHandItem();

            if (weapon.is(ModItems.CRUCIBLE_OF_RILE.get())) {
                String typeId = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString();
                Set<String> seen = CrucibleOfRile.getUniqueKills(weapon);

                if (!seen.contains(typeId) && seen.add(typeId)) {
                    CrucibleOfRile.setUniqueKills(weapon, seen);

                    entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                            ModSounds.CRUCIBLE_OF_RILE_UNIQUE_KILL.get(), SoundSource.PLAYERS, 0.5F, 1.0F);
                }
            }
        }

    }
}
