package net.jrdemiurge.enigmaticdice.stat;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

public class ModStats {
    public static ResourceLocation OBTAINED_DICE_FROM_BLOCK;
    public static ResourceLocation OBTAINED_DICE_FROM_MOB;

    public static void registerCustomStats() {
        OBTAINED_DICE_FROM_BLOCK = makeCustomStat("obtained_dice_from_block", StatFormatter.DEFAULT);
        OBTAINED_DICE_FROM_MOB = makeCustomStat("obtained_dice_from_mob", StatFormatter.DEFAULT);
    }

    private static ResourceLocation makeCustomStat(String name, StatFormatter formatter) {
        ResourceLocation id = new ResourceLocation(EnigmaticDice.MOD_ID, name);
        Registry.register(BuiltInRegistries.CUSTOM_STAT, name, id);
        Stats.CUSTOM.get(id, formatter);
        return id;
    }
}
