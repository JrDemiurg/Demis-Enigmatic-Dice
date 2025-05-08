package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.config.EnigmaticDiceConfig;
import net.jrdemiurge.enigmaticdice.config.UniqueEventConfig;
import net.jrdemiurge.enigmaticdice.config.ItemEventConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.fml.ModList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RandomEventManager {
    private final List<RandomEvent> events = new ArrayList<>();
    private final List<String> eventNames = new ArrayList<>();

    public RandomEventManager() {
        if (EnigmaticDiceConfig.configData != null) {
            if (EnigmaticDiceConfig.configData.uniqueEvents != null) {
                loadUniqueEvents();
            }
            if (EnigmaticDiceConfig.configData.itemEvents != null) {
                loadItemEvents();
            }
        }
    }

    private void loadItemEvents() {
        for (Map.Entry<String, ItemEventConfig> entry : EnigmaticDiceConfig.configData.itemEvents.entrySet()) {
            String eventName = entry.getKey();
            ItemEventConfig config = entry.getValue();

            if (!config.enabled){
                EnigmaticDice.LOGGER.info("Config {} is disabled.", eventName);
                continue;
            }

            if (!ModList.get().isLoaded(config.requiredMod)) {
                EnigmaticDice.LOGGER.info("Skipping event {}: required mod {} is missing.", eventName, config.requiredMod);
                continue;
            }

            events.add(new GiveItemEvent(config.item, config.rarity, config.amount, config.nbt, config.chatMessage));
            eventNames.add(eventName);
        }
    }

    private void loadUniqueEvents() {
        for (Map.Entry<String, UniqueEventConfig> entry : EnigmaticDiceConfig.configData.uniqueEvents.entrySet()) {
            String eventName = entry.getKey();
            UniqueEventConfig config = entry.getValue();

            if (!config.enabled) {
                EnigmaticDice.LOGGER.info("Config {} is disabled.", eventName);
                continue;
            }

            if (!ModList.get().isLoaded(config.requiredMod)) {
                EnigmaticDice.LOGGER.info("Skipping event {}: required mod {} is missing.", eventName, config.requiredMod);
                continue;
            }

            switch (eventName) {
                case "minecraft_explosion" -> {
                    events.add(new ExplosionEvent(4f, false, false,config.rarity));
                    eventNames.add(eventName);
                }
                case "minecraft_lightning_strike" -> {
                    events.add(new LightningStrikeEvent(config.rarity));
                    eventNames.add(eventName);
                }
                case "minecraft_permanent_buff" -> {
                    events.add(new PermanentBuffEvent(config.rarity));
                    eventNames.add(eventName);
                }
                case "minecraft_ancient_debris_cage" -> {
                    events.add(new AncientDebrisCageEvent(config.rarity));
                    eventNames.add(eventName);
                }
                case "minecraft_teleport_to_biome" -> {
                    events.add(new TeleportToBiomeEvent(config.rarity));
                    eventNames.add(eventName);
                }
                case "minecraft_teleport_to_structure" -> {
                    events.add(new TeleportToStructureEvent(config.rarity));
                    eventNames.add(eventName);
                }
                case "minecraft_teleport_to_world_edge" -> {
                    events.add(new TeleportToWorldEdgeEvent(config.rarity));
                    eventNames.add(eventName);
                }
                case "minecraft_summon_horse" -> {
                    events.add(new SummonTamedHorse(config.rarity));
                    eventNames.add(eventName);
                }
                case "alexsmobs_summon_warped_toads" -> {
                    events.add(new SummonWarpedToadsEvent(config.rarity));
                    eventNames.add(eventName);
                }
                case "alexsmobs_summon_centipede" -> {
                    events.add(new SummonCentipedeEvent(config.rarity));
                    eventNames.add(eventName);
                }
                case "alexscaves_nuclear_bomb" -> {
                    events.add(new NuclearBombEvent(config.rarity));
                    eventNames.add(eventName);
                }
                case "alexscaves_summon_raycat" -> {
                    events.add(new SummonRaycatEvent(config.rarity));
                    eventNames.add(eventName);
                }
                case "alexscaves_summon_subterranodon" -> {
                    events.add(new SummonSubterranodonEvent(config.rarity));
                    eventNames.add(eventName);
                }
                case "alexscaves_summon_vallumraptor" -> {
                    events.add(new SummonVallumraptorEvent(config.rarity));
                    eventNames.add(eventName);
                }
                case "alexscaves_summon_candicorn" -> {
                    events.add(new SummonCandicornEvent(config.rarity));
                    eventNames.add(eventName);
                }
                case "artifacts_summon_mimic" -> {
                    events.add(new SummonMimic(config.rarity));
                    eventNames.add(eventName);
                }
                case "artifacts_wonderland_field" -> {
                    events.add(new WonderlandField(config.rarity));
                    eventNames.add(eventName);
                }
                case "netherexp_summon_carcass" -> {
                    events.add(new SummonCarcassEvent(config.rarity));
                    eventNames.add(eventName);
                }
                default -> EnigmaticDice.LOGGER.warn("Unknown fixed event in config: {}", eventName);
            }
        }
    }

    public void triggerRandomEvent(Level pLevel, Player pPlayer) {
        RandomEvent event;
        boolean eventTriggered;

        do {
            event = events.get(pLevel.getRandom().nextInt(events.size()));
            eventTriggered = event.execute(pLevel, pPlayer);
        } while (!eventTriggered);
    }

    public void simulationRandomEvent(Level pLevel, Player pPlayer, int simulationsCount) {
        Map<String, Integer> eventWinCounts = new HashMap<>();

        for (String eventName : eventNames) {
            eventWinCounts.put(eventName, 0);
        }

        for (int i = 0; i < simulationsCount; i++) {
            RandomEvent event;
            String eventName;
            boolean eventTriggered;

            do {
                int numberOfEvent = pLevel.getRandom().nextInt(events.size());
                event = events.get(numberOfEvent);
                eventName = eventNames.get(numberOfEvent);
                eventTriggered = event.simulationExecute(pLevel, pPlayer);
            } while (!eventTriggered);

            eventWinCounts.put(eventName, eventWinCounts.get(eventName) + 1);
        }

        saveResultsToFile(pLevel,pPlayer, eventWinCounts, simulationsCount);
    }

    private void saveResultsToFile(Level pLevel, Player pPlayer, Map<String, Integer> eventWinCounts, int totalSimulations) {
        MinecraftServer server = pLevel.getServer();
        if (server == null) return; // Безопасность на случай, если уровень не загружен на сервере

        File worldFolder = server.getWorldPath(LevelResource.ROOT).toFile();
        File resultFile = new File(worldFolder, "enigmatic_die_simulation_results.txt");

        try (FileWriter writer = new FileWriter(resultFile)) {
            writer.write("Total simulations: " + totalSimulations + "\n");
            writer.write("Player Luck: " + String.format("%.2f", pPlayer.getLuck()) + "\n\n");

            List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(eventWinCounts.entrySet());
            sortedEntries.sort((e1, e2) -> {
                int index1 = eventNames.indexOf(e1.getKey());
                int index2 = eventNames.indexOf(e2.getKey());
                return Integer.compare(index1, index2); // Сортировка по порядку в eventNames
            });

            for (Map.Entry<String, Integer> entry : sortedEntries) {
                String eventName = entry.getKey();
                int count = entry.getValue();
                double percentage = (count / (double) totalSimulations) * 100;
                writer.write(eventName + ": " + count + " occurrences (" + String.format("%.2f", percentage) + "%)\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RandomEvent getEventByName(String name) {
        int index = eventNames.indexOf(name);
        if (index != -1) {
            return events.get(index);
        }
        return null;
    }

    public List<String> getEventNames() {
        return new ArrayList<>(eventNames);
    }
}