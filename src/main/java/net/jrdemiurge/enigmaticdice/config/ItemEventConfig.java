package net.jrdemiurge.enigmaticdice.config;

public class ItemEventConfig {
    public boolean enabled;
    public String requiredMod;
    public int rarity;
    public String item;
    public int amount;
    public String nbt;
    public String chatMessage;

    public ItemEventConfig(boolean enabled, String requiredMod, int rarity, String item, int amount, String nbt, String chatMessage) {
        this.enabled = enabled;
        this.requiredMod = requiredMod;
        this.rarity = rarity;
        this.item = item;
        this.amount = amount;
        this.nbt = nbt;
        this.chatMessage = chatMessage;
    }
}
