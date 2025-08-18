package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.jrdemiurge.enigmaticdice.scheduler.Scheduler;
import net.minecraft.ChatFormatting;
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

public class SummonWarpedToadsEvent extends RandomEvent {
    private static final List<String> NAMES = List.of(
            "Jabba the Hopp", "Kermit's Cousin", "Trevor", "Pepe", "Sir Ribbit",
            "Croakmaster", "Frogzilla", "The Toadfather", "Frogger", "Toadally Buggin",
            "Hopkins", "Ribbert", "Slippy", "Rana", "Greninja",
            "Jiraiya", "Quackson"
    );
    private final int rarity;

    public SummonWarpedToadsEvent(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!RandomEvent.rollChance(pLevel, pPlayer, rarity, isPositiveEvent())) return false;
        }

        Vec3 playerPos = pPlayer.position();

        for (int i = 0; i < 3; i++) {
            final int index = i;

            Scheduler.schedule(() -> {
                double radius = 2.0;
                double angle = Math.toRadians(index * 120);
                double offsetX = radius * Math.cos(angle);
                double offsetZ = radius * Math.sin(angle);
                double offsetY = 5.0;

                Vec3 pos = playerPos.add(offsetX, offsetY, offsetZ);

                EntityType<?> entityType = EntityType.byString("alexsmobs:warped_toad").orElse(null);
                if (entityType == null) return;

                Entity entity = entityType.create(pLevel);
                if (entity == null) return;

                entity.moveTo(pos.x, pos.y, pos.z, 0, 0);
                pLevel.addFreshEntity(entity);

                if (entity instanceof TamableAnimal tamable) {
                    tamable.tame(pPlayer); // Приручаем лягушку
                    tamable.setOwnerUUID(pPlayer.getUUID()); // Привязываем к игроку
                    tamable.setCustomName(Component.literal(NAMES.get(pLevel.getRandom().nextInt(NAMES.size())))); // Название
                    tamable.addEffect(new MobEffectInstance(MobEffects.REGENERATION, MobEffectInstance.INFINITE_DURATION, 0, false, false));
                }

                try {
                    Class<?> toadClass = Class.forName("com.github.alexthe666.alexsmobs.entity.EntityWarpedToad");
                    if (toadClass.isInstance(entity)) {
                        Method setCommandMethod = toadClass.getMethod("setCommand", int.class);
                        setCommandMethod.invoke(entity, 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }, 10 * i); // 10 тиков = 0.5 секунд задержки
        }

        MutableComponent message = Component.translatable("enigmaticdice.event.warped_toads");
        pPlayer.displayClientMessage(message, false);
        return true;
    }

    @Override
    public boolean simulationExecute(Level pLevel, Player pPlayer) {
        return RandomEvent.rollChance(pLevel, pPlayer, rarity, isPositiveEvent());
    }
}
