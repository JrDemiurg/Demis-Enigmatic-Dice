package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


public class MassCreeperSummonEvent extends RandomEvent {
    private final int eventLevel;
    private static final int CREEPER_COUNT = 50;
    private static final int SPAWN_RADIUS = 30;

    public MassCreeperSummonEvent(int rarity, int eventLevel) {
        this.rarity     = rarity;
        this.eventLevel = eventLevel;
    }

    @Override
    public boolean  execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!rollChance(pLevel, pPlayer, rarity)) return false;
        }

        Random random = new Random();

        for (int i = 0; i < CREEPER_COUNT; i++) {
            Vec3 spawnPos = findValidSpawnPosition(pLevel, pPlayer, random);

            Entity entity = EntityType.CREEPER.create(pLevel);
            System.out.println(i);
            if (!(entity instanceof Creeper creeper)) continue;
            System.out.println(1);
            creeper.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, 0, 0);

            if (random.nextFloat() < 0.5f) {
                CompoundTag tag = creeper.saveWithoutId(new CompoundTag());
                tag.putBoolean("powered", true);
                creeper.load(tag);
            }

            creeper.setPersistenceRequired();
            creeper.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);

            applyLevelEffects(creeper, random);

            pLevel.addFreshEntity(creeper);
        }

        MutableComponent message = Component.translatable("enigmaticdice.event.creeper_party.level." + eventLevel);
        pPlayer.displayClientMessage(message, false);
        return true;
    }

    private Vec3 findValidSpawnPosition(Level level, Player player, Random random) {
        double angle = random.nextDouble() * Math.PI * 2;
        double distance = SPAWN_RADIUS * (0.15 + 0.85 * random.nextDouble());
        int x = (int) (player.getX() + Math.cos(angle) * distance);
        int z = (int) (player.getZ() + Math.sin(angle) * distance);
        BlockPos surfacePos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(x, 0, z));

        return new Vec3(surfacePos.getX() + 0.5, surfacePos.getY(), surfacePos.getZ() + 0.5);
    }

    private void applyLevelEffects(Creeper creeper, Random random) {
        switch (eventLevel) {
            case 1:
                break;

            case 2:
                creeper.addEffect(new MobEffectInstance(
                        MobEffects.INVISIBILITY,
                        MobEffectInstance.INFINITE_DURATION,
                        0,
                        true,
                        true
                ));
                break;

            case 3:
                creeper.addEffect(new MobEffectInstance(
                        MobEffects.INVISIBILITY,
                        MobEffectInstance.INFINITE_DURATION,
                        0,
                        false,
                        false
                ));
                break;

            case 4:
                creeper.addEffect(new MobEffectInstance(
                        MobEffects.INVISIBILITY,
                        MobEffectInstance.INFINITE_DURATION,
                        0,
                        false,
                        false
                ));

                MobEffect randomEffect = pickRandomEffect(random);
                if (randomEffect != null) {
                    creeper.addEffect(new MobEffectInstance(
                            randomEffect,
                            MobEffectInstance.INFINITE_DURATION,
                            0,
                            false,
                            false
                    ));
                }
                break;

            default:
                break;
        }
    }

    private MobEffect pickRandomEffect(Random random) {
        List<MobEffect> effects = BuiltInRegistries.MOB_EFFECT.stream()
                .filter(effect -> effect != MobEffects.INVISIBILITY)
                .filter(effect -> !effect.isInstantenous())
                .collect(Collectors.toList());

        if (effects.isEmpty()) return null;

        return effects.get(random.nextInt(effects.size()));
    }
}
