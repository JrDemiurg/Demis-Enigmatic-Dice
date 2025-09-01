package net.jrdemiurge.enigmaticdice.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.jrdemiurge.enigmaticdice.Config;
import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.minecraftforge.fml.ModList;
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
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static ModConfig configData;

    public static void loadConfig() {
        Path path = getConfigPath();

        if (Files.exists(path)) {
            loadFromFile(path);
        } else {
            copyDefaultConfigTo(path);
            loadFromFile(path);
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

    private static Path getConfigPath() {
        boolean versioned = Config.UseVersionedJson;
        Path dir = FMLPaths.CONFIGDIR.get();
        if (versioned) {
            String ver = getModVersionSafe(); // напр. "1.3.0"
            return dir.resolve("enigmatic_dice_" + ver + ".json");
        } else {
            return dir.resolve("enigmatic_dice.json");
        }
    }

    private static String getModVersionSafe() {
        return ModList.get().getModContainerById(EnigmaticDice.MOD_ID)
                .map(c -> c.getModInfo().getVersion().toString())
                .orElse("unknown");
    }

    private static void loadFromFile(Path path) {
        try (Reader reader = Files.newBufferedReader(path)) {
            configData = GSON.fromJson(reader, ModConfig.class);
        } catch (IOException e) {
            EnigmaticDice.LOGGER.error("Failed to read JSON config {}", path, e);
        }
    }

    private static void copyDefaultConfigTo(Path target) {
        try {
            try (InputStream in = EnigmaticDiceConfig.class.getResourceAsStream("/assets/enigmaticdice/config/enigmatic_dice.json")) {
                if (in == null) {
                    EnigmaticDice.LOGGER.error("Default JSON config not found in mod resources!");
                    return;
                }
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            EnigmaticDice.LOGGER.error("Failed to copy default JSON to {}", target, e);
        }
    }
}

