package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.jrdemiurge.enigmaticdice.scheduler.Scheduler;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public class DayOfInvisibility extends RandomEvent {
    private final int rarity;
    public static final Set<UUID> activePlayers = new HashSet<>();

    public DayOfInvisibility(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!RandomEvent.rollChance(pLevel, pPlayer, rarity, isPositiveEvent())) return false;
        }

        if (!(pLevel instanceof ServerLevel serverLevel)) {
            return false;
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
            AABB area = pPlayer.getBoundingBox().inflate(150);
            for (LivingEntity entity : serverLevel.getEntitiesOfClass(LivingEntity.class, area)) {
                if (entity.isAlive()) {
                    entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 200, 0, false, true));
                }
            }
        }, 0, period, repeatCount);

        Scheduler.schedule(() -> activePlayers.remove(playerId), dayDuration);

        MutableComponent message = Component.translatable("enigmaticdice.event.day_of_invisibility");
        pPlayer.displayClientMessage(message, false);
        return true;
    }

    @Override
    public boolean simulationExecute(Level pLevel, Player pPlayer) {
        return RandomEvent.rollChance(pLevel, pPlayer, rarity, isPositiveEvent());
    }
}
