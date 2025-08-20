package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import com.mojang.datafixers.util.Pair;
import net.jrdemiurge.enigmaticdice.Config;
import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.config.EnigmaticDiceConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.List;

public class TeleportToStructureEvent extends RandomEvent {

    public TeleportToStructureEvent(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!rollChance(pLevel, pPlayer, rarity)) return false;
        }

        if (!(pLevel instanceof ServerLevel serverLevel)) {
            return false;
        }

        List<String> allStructures = EnigmaticDiceConfig.configData.teleportStructures;
        if (allStructures == null || allStructures.isEmpty()) {
            EnigmaticDice.LOGGER.error("Structure list is empty in config!");
            return false;
        }

        Registry<Structure> structureRegistry = serverLevel.registryAccess().registryOrThrow(Registries.STRUCTURE);

        List<String> validStructures = allStructures.stream()
                .filter(id -> structureRegistry.containsKey(new ResourceLocation(id)))
                .toList();

        if (validStructures.isEmpty()) {
            EnigmaticDice.LOGGER.error("No valid structures found! Check your config.");
            return false;
        }

        String structureId = validStructures.get(pLevel.random.nextInt(validStructures.size()));
        ResourceLocation structureRL = new ResourceLocation(structureId);

        try {
            Holder<Structure> structureHolder = structureRegistry.getHolder(structureRegistry.getResourceKey(
                    structureRegistry.get(structureRL)).orElseThrow()).orElseThrow();

            HolderSet<Structure> structureSet = HolderSet.direct(structureHolder);

            Pair<BlockPos, Holder<Structure>> result = serverLevel.getChunkSource().getGenerator().findNearestMapStructure(
                    serverLevel,
                    structureSet,
                    pPlayer.blockPosition(),
                    Config.StructureSearchRadius,
                    false
            );

            if (result == null) {
                EnigmaticDice.LOGGER.info("Structure not found: {}", structureId);
                return false;
            }

            BlockPos structurePos = result.getFirst();
            structurePos = structurePos.offset(100, 0, 0);

            serverLevel.getChunkSource().getChunk(structurePos.getX() >> 4, structurePos.getZ() >> 4, ChunkStatus.FULL, true);

            int surfaceY = serverLevel.getHeight(Heightmap.Types.WORLD_SURFACE, structurePos.getX(), structurePos.getZ());

            if (pPlayer instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.teleport(
                        structurePos.getX() + 0.5,
                        surfaceY + 1,
                        structurePos.getZ() + 0.5,
                        90.0F,
                        0.0F
                );
            }

            MutableComponent message = Component.translatable("enigmaticdice.event.teleport_to_structure." + pLevel.random.nextInt(4));
            pPlayer.displayClientMessage(message, false);

            return true;
        } catch (Exception e) {
            EnigmaticDice.LOGGER.error("Error while teleporting to structure {}: ", structureId, e);
            return false;
        }
    }
}
