package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.jrdemiurge.enigmaticdice.effect.ModEffects;
import net.jrdemiurge.enigmaticdice.scheduler.Scheduler;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public class DayOnTheMoon extends RandomEvent {
    public static final Set<UUID> activePlayers = new HashSet<>();

    public DayOnTheMoon(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!rollChance(pLevel, pPlayer, rarity)) return false;
        }

        UUID playerId = pPlayer.getUUID();
        if (activePlayers.contains(playerId)) {
            return false;
        }
        activePlayers.add(playerId);

        int dayDuration = 24000;
        int period = 100;
        int repeatCount = dayDuration / period;

        Scheduler.schedule(() -> {
            if (pPlayer.level() instanceof ServerLevel serverLevel) {
                AABB area = pPlayer.getBoundingBox().inflate(150);
                for (LivingEntity entity : serverLevel.getEntitiesOfClass(LivingEntity.class, area)) {
                    if (entity.isAlive()) {
                        entity.addEffect(new MobEffectInstance(ModEffects.DAY_ON_THE_MOON.get(), 200, 0, false, false));
                    }
                }
            }
        }, 0, period, repeatCount);

        Scheduler.schedule(() -> activePlayers.remove(playerId), dayDuration);

        MutableComponent message = Component.translatable("enigmaticdice.event.day_on_the_moon");
        pPlayer.displayClientMessage(message, false);
        return true;
    }
}
