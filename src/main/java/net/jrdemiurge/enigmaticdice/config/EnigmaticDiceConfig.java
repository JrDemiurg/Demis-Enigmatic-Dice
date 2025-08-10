package net.jrdemiurge.enigmaticdice.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;

public class EnigmaticDiceConfig {
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("enigmaticdice.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static ModConfig configData;

    public static void loadConfig() {
        if (Files.exists(CONFIG_PATH)) {
            loadFromFile();
        } else {
            copyDefaultConfig();
        }

        if (configData == null) {
            EnigmaticDice.LOGGER.error("EnigmaticDiceConfig.configData is null! Config not loaded properly.");

            configData = new ModConfig();
            configData.uniqueEvents = new HashMap<>();
            configData.itemEvents = new HashMap<>();
            configData.teleportBiomes = new ArrayList<>();
            configData.teleportStructures = new ArrayList<>();
            configData.negativeEvents = new ArrayList<>();
        }
    }

    private static void loadFromFile() {
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            configData = GSON.fromJson(reader, ModConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copyDefaultConfig() {
        try (InputStream in = EnigmaticDiceConfig.class.getResourceAsStream("/assets/enigmaticdice/config/enigmaticdice.json")) {
            if (in == null) {
                EnigmaticDice.LOGGER.error("Не найден конфиг в ресурсах мода!");
                return;
            }

            Files.copy(in, CONFIG_PATH, StandardCopyOption.REPLACE_EXISTING);
            loadFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

