package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.jrdemiurge.enigmaticdice.scheduler.Scheduler;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Method;
import java.util.List;

public class SummonCarcassEvent extends RandomEvent {

    public SummonCarcassEvent(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!rollChance(pLevel, pPlayer, rarity)) return false;
        }

        Vec3 playerPos = pPlayer.position();

        for (int i = 0; i < 5; i++) {
            final int index = i;

            Scheduler.schedule(() -> {
                double radius = 3.0;
                double angle = Math.toRadians(index * 72);
                double offsetX = radius * Math.cos(angle);
                double offsetZ = radius * Math.sin(angle);
                double offsetY = 5.0;

                Vec3 pos = playerPos.add(offsetX, offsetY, offsetZ);

                EntityType<?> entityType = EntityType.byString("netherexp:carcass").orElse(null);
                if (entityType == null) return;

                Entity entity = entityType.create(pLevel);
                if (entity == null) return;

                entity.moveTo(pos.x, pos.y, pos.z, 0, 0);
                pLevel.addFreshEntity(entity);

            }, 10 * i);
        }

        MutableComponent message = Component.translatable("enigmaticdice.event.carcass");
        pPlayer.displayClientMessage(message, false);
        return true;
    }
}
