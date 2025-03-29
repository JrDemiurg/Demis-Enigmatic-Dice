package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

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
        events.add(new ExplosionEvent(4f, false, false, 0));
        eventNames.add("explosion");
        events.add(new LightningStrikeEvent(0));
        eventNames.add("lightning_strike");
        /*events.add(new GoldenRain(0));
        eventNames.add("golden_rain");*/
        events.add(new PermanentBuffEvent(0));
        eventNames.add("permanent_buff");
        events.add(new AncientDebrisCageEvent(2));
        eventNames.add("ancient_debris_cage");

        if (ModList.get().isLoaded("simplyswords")) {
            events.add(new GiveSimplySword(3));
            eventNames.add("give_simply_sword");
            events.add(new GiveItemEvent("simplyswords:runic_tablet", 2));
            eventNames.add("give_runic_tablet");
        }

        if (ModList.get().isLoaded("born_in_chaos_v1")) {
            events.add(new GiveItemEvent("born_in_chaos_v1:phantom_bomb", 1, 16));
            eventNames.add("give_phantom_bomb");
            events.add(new GiveItemEvent("born_in_chaos_v1:bone_heart", 1, 16));
            eventNames.add("give_bone_heart");
            events.add(new GiveItemEvent("born_in_chaos_v1:dark_atrium", 1, 4));
            eventNames.add("give_dark_atrium");
            events.add(new GiveItemEvent("born_in_chaos_v1:elixir_of_vampirism", 1, 8));
            eventNames.add("give_elixir_of_vampirism");
            events.add(new GiveItemEvent("born_in_chaos_v1:potion_of_rampage", 1, 8));
            eventNames.add("give_potion_of_rampage");
        }

        if (ModList.get().isLoaded("supplementaries")) {
            events.add(new GiveItemEvent("supplementaries:bomb", 1, 32));
            eventNames.add("give_bomb");
            events.add(new GiveItemEvent("supplementaries:bomb_blue", 1, 8));
            eventNames.add("give_bomb_blue");
        }

        if (ModList.get().isLoaded("enigmaticlegacy")) {
            events.add(new GiveSpellStone(3));
            eventNames.add("give_spell_stone");
            events.add(new GiveItemEvent("enigmaticlegacy:soul_crystal", 4, 1));
            eventNames.add("give_soul_crystal");
            events.add(new GiveItemEvent("enigmaticlegacy:redemption_potion", 3, 1));
            eventNames.add("give_redemption_potion");
            events.add(new GiveNamedItemEvent("enigmaticlegacy:earth_heart","enigmaticdice.renameditem.earth_heart", 2, 1)); // переименовывание в Heart of Creation наверное розовым
            eventNames.add("give_earth_heart");
            events.add(new GiveNamedItemEvent("enigmaticlegacy:golden_ring","enigmaticdice.renameditem.the_one_ring", 2, 1));
            eventNames.add("give_the_one_ring");
            events.add(new GiveItemEvent("enigmaticlegacy:cosmic_cake", 3, 1));
            eventNames.add("give_cosmic_cake");
            // ещё мб бутылочку мёда с названием зелья искупления
            // предмет с остротой с названием судья ???
            // проклятие вечной привязки на 1 элемент брони
            // доп слот под амулет ???
            // забыть всю книгу ???
        }

        if (ModList.get().isLoaded("alexsmobs")) {
            events.add(new SummonWarpedToadsEvent(1));
            eventNames.add("summon_warped_toads");
            events.add(new SummonCentipedeEvent(1));
            eventNames.add("summon_centipede");
        }

        if (ModList.get().isLoaded("artifacts")) {
            events.add(new SummonMimic(2));
            eventNames.add("summon_mimic");
            events.add(new WonderlandField(2));
            eventNames.add("wonderland_field");
        }

        // клетка из обломков, эпик
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