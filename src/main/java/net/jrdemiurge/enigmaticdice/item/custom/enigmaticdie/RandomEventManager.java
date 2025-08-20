package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import com.mojang.authlib.GameProfile;
import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.config.EnigmaticDiceConfig;
import net.jrdemiurge.enigmaticdice.config.UniqueEventConfig;
import net.jrdemiurge.enigmaticdice.config.ItemEventConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.ModList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

            if (EnigmaticDiceConfig.configData.negativeEvents != null) {
                for (int i = 0; i < events.size(); i++) {
                    String name = eventNames.get(i);
                    if (EnigmaticDiceConfig.configData.negativeEvents.contains(name)) {
                        events.get(i).setNegativeEvent(true);
                    }
                }
            }
            if (EnigmaticDiceConfig.configData.neutralEvents != null) {
                for (int i = 0; i < events.size(); i++) {
                    String name = eventNames.get(i);
                    if (EnigmaticDiceConfig.configData.neutralEvents.contains(name)) {
                        events.get(i).setNeutralEvent(true);
                    }
                }
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
                case "minecraft_mass_creeper_summon_1" -> {
                    events.add(new MassCreeperSummonEvent(config.rarity, 1));
                    eventNames.add(eventName);
                }
                case "minecraft_mass_creeper_summon_2" -> {
                    events.add(new MassCreeperSummonEvent(config.rarity, 2));
                    eventNames.add(eventName);
                }
                case "minecraft_mass_creeper_summon_3" -> {
                    events.add(new MassCreeperSummonEvent(config.rarity, 3));
                    eventNames.add(eventName);
                }
                case "minecraft_mass_creeper_summon_4" -> {
                    events.add(new MassCreeperSummonEvent(config.rarity, 4));
                    eventNames.add(eventName);
                }
                case "minecraft_give_vanilla_random_potion" -> {
                    events.add(new GiveRandomPotionEvent(config.rarity, true));
                    eventNames.add(eventName);
                }
                case "minecraft_give_modded_random_potion" -> {
                    events.add(new GiveRandomPotionEvent(config.rarity, false));
                    eventNames.add(eventName);
                }
                case "minecraft_curse_binding" -> {
                    events.add(new CurseBindingEvent(config.rarity));
                    eventNames.add(eventName);
                }
                case "minecraft_skyfall_training" -> {
                    events.add(new SkyfallTraining(config.rarity));
                    eventNames.add(eventName);
                }
                case "minecraft_day_of_invisibility" -> {
                    events.add(new DayOfInvisibility(config.rarity));
                    eventNames.add(eventName);
                }
                case "minecraft_teleport_monsters_to_player" -> {
                    events.add(new TeleportMonstersToPlayer(config.rarity));
                    eventNames.add(eventName);
                }
                case "minecraft_accelerate_day_cycle" -> {
                    events.add(new AccelerateDayCycleEvent(config.rarity));
                    eventNames.add(eventName);
                }
                case "minecraft_mini_size_shrink" -> {
                    events.add(new MiniSizeShrinkEvent(config.rarity));
                    eventNames.add(eventName);
                }
                case "minecraft_ant_size_shrink" -> {
                    events.add(new AntSizeShrinkEvent(config.rarity));
                    eventNames.add(eventName);
                }
                case "minecraft_summon_spider_fleas" -> {
                    events.add(new SummonSpiderFleasEvent(config.rarity));
                    eventNames.add(eventName);
                }
                case "enigmaticlegacy_eternal_binding" -> {
                    events.add(new EternalBindingEvent(config.rarity));
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
                case "artifacts_summon_dummy_chest" -> {
                    events.add(new SummonDummyChest(config.rarity));
                    eventNames.add(eventName);
                }
                case "netherexp_summon_carcass" -> {
                    events.add(new SummonCarcassEvent(config.rarity));
                    eventNames.add(eventName);
                }
                case "terramity_black_hole_bomb" -> {
                    events.add(new BlackHoleBombEvent(config.rarity));
                    eventNames.add(eventName);
                }
                case "terramity_macro_black_hole_bomb" -> {
                    events.add(new MacroBlackHoleBombEvent(config.rarity));
                    eventNames.add(eventName);
                }
                case "terramity_antimatter_bomb" -> {
                    events.add(new AntimatterBombEvent(config.rarity));
                    eventNames.add(eventName);
                }
                case "quark_give_ancient_tome" -> {
                    events.add(new GiveAncientTomeEvent(config.rarity));
                    eventNames.add(eventName);
                }
                case "born_in_chaos_brood_awakens" -> {
                    events.add(new BroodAwakensEvent(config.rarity));
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

        saveResultsToFile(pLevel, pPlayer, eventWinCounts, simulationsCount);
    }

    private void saveResultsToFile(Level pLevel, Player pPlayer, Map<String, Integer> eventWinCounts, int totalSimulations) {
        MinecraftServer server = pLevel.getServer();
        if (server == null) return;

        File worldFolder = server.getWorldPath(LevelResource.ROOT).toFile();
        File resultFile = new File(worldFolder, "enigmatic_dice_simulation_results.txt");

        // Категории из конфига
        Set<String> negativeSet = null;
        Set<String> neutralSet  = null;
        if (EnigmaticDiceConfig.configData != null) {
            if (EnigmaticDiceConfig.configData.negativeEvents != null) {
                negativeSet = new HashSet<>(EnigmaticDiceConfig.configData.negativeEvents);
            }
            if (EnigmaticDiceConfig.configData.neutralEvents != null) {
                neutralSet = new HashSet<>(EnigmaticDiceConfig.configData.neutralEvents);
            }
        }
        // Разруливаем пересечения: negative приоритетнее
        if (negativeSet != null && neutralSet != null) {
            negativeSet.removeAll(neutralSet);
        }

        try (FileWriter writer = new FileWriter(resultFile)) {
            writer.write("Total simulations: " + totalSimulations + "\n");
            writer.write("Player Luck: " + String.format("%.2f", pPlayer.getLuck()) + "\n");
            writer.write("\n");

            // Подсчёты по категориям
            int negativeCount = 0;
            int neutralCount  = 0;
            int positiveCount = 0;

            // Если сетов нет — считаем их пустыми
            Set<String> neg = (negativeSet != null) ? negativeSet : Collections.emptySet();
            Set<String> neu = (neutralSet  != null) ? neutralSet  : Collections.emptySet();

            for (Map.Entry<String, Integer> e : eventWinCounts.entrySet()) {
                String name = e.getKey();
                int count = (e.getValue() != null) ? e.getValue() : 0;

                if (neg.contains(name)) {
                    negativeCount += count;
                } else if (neu.contains(name)) {
                    neutralCount += count;
                } else {
                    positiveCount += count; // всё остальное — положительное
                }
            }

            // Проценты (защита от деления на ноль)
            double denom = totalSimulations > 0 ? totalSimulations : 1.0;
            double negativeChance = (negativeCount / denom) * 100.0;
            double neutralChance  = (neutralCount  / denom) * 100.0;
            double positiveChance = (positiveCount / denom) * 100.0;

            writer.write("Negative events total: " + negativeCount + " occurrences (" +
                    String.format("%.2f", negativeChance) + "%)\n");
            writer.write("Neutral events total: "  + neutralCount  + " occurrences (" +
                    String.format("%.2f", neutralChance)  + "%)\n");
            writer.write("Positive events total: " + positiveCount + " occurrences (" +
                    String.format("%.2f", positiveChance) + "%)\n\n");

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

    /**
     * Выполняет симуляции для всех уровней удачи от -10 до 10 и возвращает карту:
     * luckLevel -> ( eventName -> chancePercent )
     */
    public Map<Integer, Map<String, Double>> buildLuckRangeResults(Level pLevel, int simulationsCount) {
        // Создаём одного фейкового игрока на весь прогон
        FakePlayer fakePlayer = createCleanFakePlayer((ServerLevel) pLevel);

        Map<Integer, Map<String, Double>> luckPercentages = new LinkedHashMap<>(); // сохраняет порядок ключей

        // Локальные ссылки чтобы немного ускорить доступ
        List<RandomEvent> localEvents = this.events;
        List<String> localEventNames = this.eventNames;
        int eventsCount = localEvents.size();
        if (eventsCount == 0) return luckPercentages; // ничего не делать

        // Для каждого уровня удачи один цикл симуляций
        List<Integer> luckLevels = buildCustomLuckLevels();
        for (int luck : luckLevels) {
            // Устанавливаем удачу фейковому игроку
            AttributeInstance luckAttr = fakePlayer.getAttribute(Attributes.LUCK);
            if (luckAttr != null) {
                luckAttr.setBaseValue(luck);
            }

            // Счётчики
            Map<String, Integer> counts = new LinkedHashMap<>();
            for (String name : localEventNames) counts.put(name, 0);

            // Основные симуляции
            for (int i = 0; i < simulationsCount; i++) {
                String eventName;
                boolean triggered;
                RandomEvent event;
                // выбираем случайный ивент, пока не сработает (как у тебя было)
                do {
                    int idx = pLevel.getRandom().nextInt(eventsCount);
                    event = localEvents.get(idx);
                    eventName = localEventNames.get(idx);
                    triggered = event.simulationExecute(pLevel, fakePlayer);
                } while (!triggered);

                counts.put(eventName, counts.get(eventName) + 1);
            }

            // Конвертируем в проценты и сохраняем в результат
            Map<String, Double> percentages = new LinkedHashMap<>();
            for (String name : localEventNames) {
                int c = counts.getOrDefault(name, 0);
                double pct = (c / (double) simulationsCount) * 100.0;
                percentages.put(name, pct);
            }

            luckPercentages.put(luck, percentages);
        }

        return luckPercentages;
    }

    private static FakePlayer createCleanFakePlayer(ServerLevel serverLevel) {
        // Стабильный UUID по строке, чтобы не плодить сущности при множественных вызовах (но можно и randomUUID())
        UUID uuid = UUID.nameUUIDFromBytes("enigmaticdice-sim".getBytes(StandardCharsets.UTF_8));
        FakePlayer fp = FakePlayerFactory.get(serverLevel, new GameProfile(uuid, "[EnigmaticDiceSim]"));

        // Позиционируем в безопасную точку (например, спавн мира), чтобы редкие ивенты с позицией не падали
        BlockPos spawn = serverLevel.getSharedSpawnPos();
        fp.teleportTo(serverLevel, spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5, 0.0F, 0.0F);

        // Чистим состояние на всякий случай
        fp.removeAllEffects();
        fp.getInventory().clearContent();

        // Обнуляем базовую удачу (модификаторов у FakePlayer обычно нет)
        AttributeInstance luckAttr = fp.getAttribute(Attributes.LUCK);
        if (luckAttr != null) luckAttr.setBaseValue(0.0);

        return fp;
    }

    /**
     * Сохраняет карту luckPercentages в текстовый файл таблично.
     * Формат: строка = eventName, затем колонки -10..10 (проценты с 2 знаками).
     * В конце добавляется строка "Negative Events Total".
     */
    public void saveLuckRangeResultsToFile(Level pLevel,
                                           Map<Integer, Map<String, Double>> luckPercentages,
                                           int simulationsCount) {
        MinecraftServer server = pLevel.getServer();
        if (server == null) return;

        File worldFolder = server.getWorldPath(LevelResource.ROOT).toFile();
        File resultFile = new File(worldFolder, "enigmatic_dice_luck_range_results.txt");

        // Настройки форматирования
        final int nameColWidth = 40;
        final int luckColWidth = 8;

        List<Integer> luckLevels = buildCustomLuckLevels();

        Set<String> negativeSet = null;
        if (EnigmaticDiceConfig.configData != null && EnigmaticDiceConfig.configData.negativeEvents != null) {
            negativeSet = new HashSet<>(EnigmaticDiceConfig.configData.negativeEvents);
        }
        Set<String> neutralSet = null;
        if (EnigmaticDiceConfig.configData != null && EnigmaticDiceConfig.configData.neutralEvents != null) {
            neutralSet = new HashSet<>(EnigmaticDiceConfig.configData.neutralEvents);
        }

        if (negativeSet != null && neutralSet != null) {
            negativeSet.removeAll(neutralSet);
        }

        try (FileWriter writer = new FileWriter(resultFile)) {
            writer.write("Simulations per luck level: " + simulationsCount + "\n\n");

            // Заголовок
            writer.write(String.format("%-" + nameColWidth + "s", "Event Name                  Luck level"));
            for (int luck : luckLevels) {
                writer.write(String.format("%" + luckColWidth + "d", luck));
            }
            writer.write("\n");

            // Проверка на пустые данные
            if (luckPercentages.isEmpty()) {
                writer.write("No data (no events or simulation not run).\n");
                return;
            }

            // Используем порядок eventNames чтобы строки были в том же порядке
            for (String eventName : this.eventNames) {
                writer.write(String.format("%-" + nameColWidth + "." + (nameColWidth - 1) + "s", eventName));
                for (int luck : luckLevels) {
                    Map<String, Double> map = luckPercentages.get(luck);
                    double pct = 0.0;
                    if (map != null && map.containsKey(eventName)) pct = map.get(eventName);
                    writer.write(String.format("%" + luckColWidth + ".2f", pct));
                }
                writer.write("\n");
            }


            // ------- Totals -------
            writer.write("\n");

            // 1) Positive Total (всё, что не в negative и не в neutral)
            writer.write(String.format("%-" + nameColWidth + "s", "Positive Events Total"));
            for (int luck : luckLevels) {
                Map<String, Double> map = luckPercentages.get(luck);
                double positiveSum = 0.0;
                if (map != null) {
                    for (Map.Entry<String, Double> e : map.entrySet()) {
                        String name = e.getKey();
                        if ((negativeSet == null || !negativeSet.contains(name)) &&
                                (neutralSet == null || !neutralSet.contains(name))) {
                            Double v = e.getValue();
                            if (v != null) positiveSum += v;
                        }
                    }
                }
                writer.write(String.format("%" + luckColWidth + ".2f", positiveSum));
            }
            writer.write("\n");

            // 2) Neutral Total
            writer.write(String.format("%-" + nameColWidth + "s", "Neutral Events Total"));
            for (int luck : luckLevels) {
                writer.write(String.format("%" + luckColWidth + ".2f",
                        sumForSet(luckPercentages.get(luck), neutralSet)));
            }
            writer.write("\n");

            // 3) Negative Total
            writer.write(String.format("%-" + nameColWidth + "s", "Negative Events Total"));
            for (int luck : luckLevels) {
                writer.write(String.format("%" + luckColWidth + ".2f",
                        sumForSet(luckPercentages.get(luck), negativeSet)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Суммирует значения событий из map только для имен, присутствующих в заданном наборе. */
    private static double sumForSet(Map<String, Double> map, Set<String> set) {
        if (map == null || set == null || set.isEmpty()) return 0.0;
        double sum = 0.0;
        for (String name : set) {
            Double v = map.get(name);
            if (v != null) sum += v;
        }
        return sum;
    }

    /**
     * Генерирует список уровней удачи по заданным правилам.
     */
    private List<Integer> buildCustomLuckLevels() {
        List<Integer> levels = new ArrayList<>();

        // От -1000 до -100 с шагом 100
        for (int i = -1000; i <= -100; i += 100) {
            levels.add(i);
        }

        // От -100 до -20 с шагом 5
        for (int i = -100; i <= -20; i += 5) {
            if (!levels.contains(i)) levels.add(i);
        }

        // От -20 до 0 с шагом 1
        for (int i = -20; i <= 0; i++) {
            if (!levels.contains(i)) levels.add(i);
        }

        // От 1 до 20 с шагом 1
        for (int i = 1; i <= 20; i++) {
            levels.add(i);
        }

        // От 25 до 100 с шагом 5
        for (int i = 25; i <= 100; i += 5) {
            levels.add(i);
        }

        // От 200 до 1000 с шагом 100
        for (int i = 200; i <= 1000; i += 100) {
            levels.add(i);
        }

        return levels;
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