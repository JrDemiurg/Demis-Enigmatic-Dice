package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

public class SummonCentipedeEvent implements RandomEvent {
    List<String> messages = List.of(
            "Why are there so many legs in this world? Oh right… That’s your problem.",
            "How many legs does a centipede have? I hope you have enough time to count them.",
            "Did someone order an extra-long monster? No refunds!",
            "It could have been bigger… but then the game would crash.",
            "If this is a dream, it’s time to wake up. If it’s not… then you’re out of luck.",
            "Congratulations! You’ve summoned the longest pet! Too bad it doesn’t like you.",
            "Out of all possible outcomes… You got the one with the most legs.",
            "Bugs in the house are annoying. But when the house is inside the bug, it’s even worse.",
            "You woke it up. It got out on the wrong foot… or maybe the wrong hundred."
    );
    private final int rarity;

    public SummonCentipedeEvent(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean  execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!RandomEvent.rollChance(pLevel, pPlayer, rarity)) return false;
        }

        Vec3 lookVec = pPlayer.getLookAngle();
        lookVec = new Vec3(lookVec.x, 0, lookVec.z).normalize();

        Vec3 spawnPos = pPlayer.position().add(lookVec.scale(6)).add(0, 1, 0);

        if (!pLevel.getBlockState(BlockPos.containing(spawnPos)).getCollisionShape(pLevel, BlockPos.containing(spawnPos)).isEmpty()) {
            return false;
        }

        EntityType<?> entityType = EntityType.byString("alexsmobs:centipede_head").orElse(null);
        if (entityType == null) return false;

        Entity entity = entityType.create(pLevel);
        if (entity == null) return false;

        entity.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, 0, 0);
        pLevel.addFreshEntity(entity);

        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100);
        }

        try {
            Class<?> centipedeClass = Class.forName("com.github.alexthe666.alexsmobs.entity.EntityCentipedeHead");
            if (centipedeClass.isInstance(entity)) {
                Method setCommandMethod = centipedeClass.getMethod("setSegmentCount", int.class);
                setCommandMethod.invoke(entity, 20);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Random random = new Random();
        String randomMessage = messages.get(random.nextInt(messages.size()));

        MutableComponent message = Component.literal(randomMessage)
                .withStyle(getColorForRarity(rarity));

        pPlayer.displayClientMessage(message, false);
        return true;
    }

    private ChatFormatting getColorForRarity(int rarity) {
        return switch (rarity) {
            case 1 -> ChatFormatting.BLUE;      // Синий
            case 2 -> ChatFormatting.DARK_PURPLE; // Фиолетовый
            case 3 -> ChatFormatting.GOLD;      // Оранжевый
            case 4 -> ChatFormatting.DARK_RED; // Бирюзовый
            default -> ChatFormatting.WHITE;    // Белый
        };
    }

    @Override
    public boolean simulationExecute(Level pLevel, Player pPlayer) {
        return RandomEvent.rollChance(pLevel, pPlayer, rarity);
    }
}
