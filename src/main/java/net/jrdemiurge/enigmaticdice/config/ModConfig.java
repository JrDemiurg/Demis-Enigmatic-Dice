package net.jrdemiurge.enigmaticdice.config;

import java.util.List;
import java.util.Map;

public class ModConfig {
    public Map<String, UniqueEventConfig> uniqueEvents;
    public Map<String, ItemEventConfig> itemEvents;
    public List<String> teleportBiomes;
    public List<String> teleportStructures;
    public List<String> negativeEvents;
    public List<String> neutralEvents;
}
