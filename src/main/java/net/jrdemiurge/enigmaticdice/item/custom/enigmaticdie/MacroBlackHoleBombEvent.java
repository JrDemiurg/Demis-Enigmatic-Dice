package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.jrdemiurge.enigmaticdice.scheduler.Scheduler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public class MacroBlackHoleBombEvent implements RandomEvent {
    private final int rarity;

    public MacroBlackHoleBombEvent(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!RandomEvent.rollChance(pLevel, pPlayer, rarity, false)) return false;
        }

        Vec3 lookVec = pPlayer.getLookAngle();
        lookVec = new Vec3(lookVec.x, 0, lookVec.z).normalize();

        Vec3 spawnPos = pPlayer.position().add(lookVec.scale(2.5)).add(0, 2, 0);

        EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("terramity:macro_black_hole_bomb_entity"));
        if (entityType == null) return false;

        Scheduler.schedule(() -> {
            Entity entity = entityType.create(pLevel);
            if (entity != null) {
                entity.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, 0, 0);
                pLevel.addFreshEntity(entity);
                pLevel.playSound(null, BlockPos.containing(spawnPos), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.2F, 1.0F);
            }
        }, 20, 0);

        if (pPlayer instanceof ServerPlayer serverPlayer) {
            Component title = Component.literal("RUN").withStyle(ChatFormatting.DARK_RED);
            serverPlayer.connection.send(new ClientboundSetTitleTextPacket(title));
            serverPlayer.connection.send(new ClientboundSetTitlesAnimationPacket(0, 20, 0));
        }
        return true;
    }

    @Override
    public boolean simulationExecute(Level pLevel, Player pPlayer) {
        return RandomEvent.rollChance(pLevel, pPlayer, rarity, false);
    }
}