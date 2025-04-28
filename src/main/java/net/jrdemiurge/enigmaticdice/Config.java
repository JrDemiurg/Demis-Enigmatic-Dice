package net.jrdemiurge.enigmaticdice;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.*;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = EnigmaticDice.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final List<String> defaultLootTables = new ArrayList<>();

    static {
        // сундуки
        defaultLootTables.add("minecraft:chests/abandoned_mineshaft");
        defaultLootTables.add("minecraft:chests/ancient_city");
        defaultLootTables.add("minecraft:chests/ancient_city_ice_box");
        defaultLootTables.add("minecraft:chests/bastion_bridge");
        defaultLootTables.add("minecraft:chests/bastion_hoglin_stable"); //незерит
        defaultLootTables.add("minecraft:chests/bastion_other"); // незерит
        defaultLootTables.add("minecraft:chests/bastion_treasure"); // незерит много
        defaultLootTables.add("minecraft:chests/buried_treasure");
        defaultLootTables.add("minecraft:chests/desert_pyramid");
        defaultLootTables.add("minecraft:chests/end_city_treasure");
        defaultLootTables.add("minecraft:chests/igloo_chest");
        defaultLootTables.add("minecraft:chests/jungle_temple");
        defaultLootTables.add("minecraft:chests/nether_bridge");
        defaultLootTables.add("minecraft:chests/pillager_outpost");
        defaultLootTables.add("minecraft:chests/ruined_portal");
        defaultLootTables.add("minecraft:chests/shipwreck_map");
        defaultLootTables.add("minecraft:chests/shipwreck_supply");
        defaultLootTables.add("minecraft:chests/shipwreck_treasure");
        defaultLootTables.add("minecraft:chests/simple_dungeon");
        defaultLootTables.add("minecraft:chests/spawn_bonus_chest");
        defaultLootTables.add("minecraft:chests/stronghold_corridor");
        defaultLootTables.add("minecraft:chests/stronghold_crossing");
        defaultLootTables.add("minecraft:chests/stronghold_library");
        defaultLootTables.add("minecraft:chests/underwater_ruin_big");
        defaultLootTables.add("minecraft:chests/underwater_ruin_small");
        defaultLootTables.add("minecraft:chests/village/village_armorer"); // норм
        defaultLootTables.add("minecraft:chests/village/village_snowy_house"); // прикольно что там снежки, но так хрень
        defaultLootTables.add("minecraft:chests/village/village_temple"); // храм, там бывает благославление
        defaultLootTables.add("minecraft:chests/village/village_toolsmith"); // тут железо только, но сойдёт
        defaultLootTables.add("minecraft:chests/village/village_weaponsmith"); // норм
        defaultLootTables.add("minecraft:chests/woodland_mansion");

        if (ModList.get().isLoaded("born_in_chaos_v1")) {
            defaultLootTables.add("minecraft:chests/basic_chest");
            defaultLootTables.add("minecraft:chests/chest_level_1");
            defaultLootTables.add("minecraft:chests/chest_level_2");
            defaultLootTables.add("minecraft:chests/chest_level_3");
            defaultLootTables.add("minecraft:chests/farm_drop");
            defaultLootTables.add("minecraft:chests/firewell_d");
            defaultLootTables.add("minecraft:chests/shater");
        }

        if (ModList.get().isLoaded("iceandfire")) {
            defaultLootTables.add("iceandfire:chest/cyclops_cave");
            defaultLootTables.add("iceandfire:chest/fire_dragon_female_cave");
            defaultLootTables.add("iceandfire:chest/fire_dragon_male_cave");
            defaultLootTables.add("iceandfire:chest/fire_dragon_roost");
            defaultLootTables.add("iceandfire:chest/graveyard");
            defaultLootTables.add("iceandfire:chest/hydra_cave");
            defaultLootTables.add("iceandfire:chest/ice_dragon_female_cave");
            defaultLootTables.add("iceandfire:chest/ice_dragon_male_cave");
            defaultLootTables.add("iceandfire:chest/ice_dragon_roost");
            defaultLootTables.add("iceandfire:chest/lightning_dragon_female_cave");
            defaultLootTables.add("iceandfire:chest/lightning_dragon_male_cave");
            defaultLootTables.add("iceandfire:chest/lightning_dragon_roost");
            defaultLootTables.add("iceandfire:chest/mausoleum_chest");
            defaultLootTables.add("iceandfire:chest/myrmex_desert_food_chest");
            defaultLootTables.add("iceandfire:chest/myrmex_jungle_food_chest");
            defaultLootTables.add("iceandfire:chest/myrmex_loot_chest");
            defaultLootTables.add("iceandfire:chest/myrmex_trash_chest");
            defaultLootTables.add("iceandfire:chest/village_scribe");
        }

        if (ModList.get().isLoaded("call_of_yucutan")) {
            defaultLootTables.add("call_of_yucutan:chest/crypt_chest_loot");
            defaultLootTables.add("call_of_yucutan:chest/overgrown_chest_loot");
        }
    }

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> LOOT_TABLES = BUILDER
            .comment("The list of chests in which the Enigmatic Die is added")
            .defineListAllowEmpty("lootTables", defaultLootTables, Config::validateLootTable);

    private static final ForgeConfigSpec.ConfigValue<Double> ENIGMATIC_DIE_MOB_DROP_CHANCE = BUILDER
            .comment("Chance for Enigmatic Die to drop from a mob when killed by a player.\n" +
                    "For example, a value of 0.005 means a 0.5% chance to drop.")
            .defineInRange("enigmaticDieDropChance", 0.005, 0.0, 1.0);

    private static final ForgeConfigSpec.ConfigValue<Double> ENIGMATIC_DIE_BLOCK_DROP_CHANCE = BUILDER
            .comment("Chance for Enigmatic Die to drop from a block when broken by a player.\n" +
                    "For example, a value of 0.001 means a 0.1% chance to drop.")
            .defineInRange("enigmaticDieBlockDropChance", 0.001, 0.0, 1.0);

    private static final ForgeConfigSpec.ConfigValue<Double> ENIGMATIC_DIE_CHEST_CHANCE = BUILDER
            .comment("Chance for Enigmatic Die to appear in a chest.\n" +
                    "Value is between 0.0 and 1.0. For example, 0.1 means a 10% chance.")
            .defineInRange("enigmaticDieChestChance", 0.1, 0.0, 1.0);

    private static final ForgeConfigSpec.ConfigValue<Integer> STRUCTURE_SEARCH_RADIUS = BUILDER
            .comment("Radius within which the structure is searched for during the 'minecraft_teleport_to_structure' event. Default is 100.\n" +
                    "Higher values search further but may impact performance.")
            .defineInRange("structureSearchRadius", 100, 1, 10000);

    private static final ForgeConfigSpec.ConfigValue<Integer> BIOME_SEARCH_RADIUS = BUILDER
            .comment("Radius (in blocks) to search for the target biome during the 'minecraft_teleport_to_biome' event.\n" +
                    "Higher values search further but may impact performance.")
            .defineInRange("biomeSearchRadius", 4480, 256, 10000);

    private static final ForgeConfigSpec.ConfigValue<Integer> BIOME_HORIZONTAL_STEP = BUILDER
            .comment("Horizontal search step when scanning for biomes in the 'minecraft_teleport_to_biome' event.\n" +
                    "Smaller values may increase accuracy but reduce performance.")
            .defineInRange("biomeHorizontalStep", 64, 1, 512);

    private static final ForgeConfigSpec.ConfigValue<Integer> BIOME_VERTICAL_STEP = BUILDER
            .comment("Vertical search step when scanning for biomes in the 'minecraft_teleport_to_biome' event.\n" +
                    "Smaller values may increase accuracy but reduce performance.")
            .defineInRange("biomeVerticalStep", 128, 1, 512);

    private static final ForgeConfigSpec.ConfigValue<Double> UNEQUAL_EXCHANGE_TARGET_HEALTH_REDUCTION = BUILDER
            .comment("Percentage of target's max health reduced per hit by Unequal Exchange sword.\n" +
                    "Example: 0.10 = 10% of target's max health.")
            .defineInRange("unequalExchangeTargetHealthReduction", 0.10, 0.0, 1.0);

    private static final ForgeConfigSpec.ConfigValue<Double> UNEQUAL_EXCHANGE_PLAYER_HEALTH_REDUCTION = BUILDER
            .comment("Percentage of player's max health reduced per hit by Unequal Exchange sword.\n" +
                    "Example: 0.20 = 20% of player's max health.")
            .defineInRange("unequalExchangePlayerHealthReduction", 0.20, 0.0, 1.0);

    private static final ForgeConfigSpec.ConfigValue<Double> UNEQUAL_EXCHANGE_STAT_DEBUFF = BUILDER
            .comment("Percentage reduction of player's stats (armor, speed, etc.) per hit by Unequal Exchange sword.\n" +
                    "Example: 0.20 = 20% reduction.")
            .defineInRange("unequalExchangeStatDebuff", 0.20, 0.0, 1.0);

    private static final ForgeConfigSpec.ConfigValue<Integer> UNEQUAL_EXCHANGE_DEBUFF_DURATION = BUILDER
            .comment("Duration of the Unequal Exchange debuff (stat reduction) in seconds.\n" +
                    "Each hit refreshes this timer.")
            .defineInRange("unequalExchangeDebuffDuration", 30, 0, 10000);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static double EnigmaticDieMobDropChance;
    public static double EnigmaticDieBlockDropChance;
    public static double EnigmaticDieChestChance;
    public static int StructureSearchRadius;
    public static int BiomeSearchRadius;
    public static int BiomeHorizontalStep;
    public static int BiomeVerticalStep;
    public static double UnequalExchangeTargetHealthReduction;
    public static double UnequalExchangePlayerHealthReduction;
    public static double UnequalExchangeStatDebuff;
    public static int UnequalExchangeDebuffDuration;

    public static List<ResourceLocation> lootTables;

    private static boolean validateLootTable(final Object obj)
    {
        return obj instanceof final String lootTable && ResourceLocation.isValidResourceLocation(lootTable);
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        EnigmaticDieMobDropChance = ENIGMATIC_DIE_MOB_DROP_CHANCE.get();
        EnigmaticDieBlockDropChance = ENIGMATIC_DIE_BLOCK_DROP_CHANCE.get();
        EnigmaticDieChestChance = ENIGMATIC_DIE_CHEST_CHANCE.get();
        StructureSearchRadius = STRUCTURE_SEARCH_RADIUS.get();
        BiomeSearchRadius = BIOME_SEARCH_RADIUS.get();
        BiomeHorizontalStep = BIOME_HORIZONTAL_STEP.get();
        BiomeVerticalStep = BIOME_VERTICAL_STEP.get();
        UnequalExchangeTargetHealthReduction = UNEQUAL_EXCHANGE_TARGET_HEALTH_REDUCTION.get();
        UnequalExchangePlayerHealthReduction = UNEQUAL_EXCHANGE_PLAYER_HEALTH_REDUCTION.get();
        UnequalExchangeStatDebuff = UNEQUAL_EXCHANGE_STAT_DEBUFF.get();
        UnequalExchangeDebuffDuration = UNEQUAL_EXCHANGE_DEBUFF_DURATION.get();

        lootTables = LOOT_TABLES.get().stream()
                .map(ResourceLocation::new)
                .collect(Collectors.toList());
    }
}
