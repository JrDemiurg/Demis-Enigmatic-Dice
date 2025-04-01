package net.jrdemiurge.enigmaticdice.config;

public class UniqueEventConfig {
    public boolean enabled;
    public String requiredMod;
    public int rarity;

    public UniqueEventConfig(boolean enabled, String requiredMod, int rarity) {
        this.enabled = enabled;
        this.requiredMod = requiredMod;
        this.rarity = rarity;
    }
}
