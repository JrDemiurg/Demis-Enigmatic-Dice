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

    private static final ForgeConfigSpec.ConfigValue<Integer> MOB_DICE_TIME_INTERVAL = BUILDER
            .comment("Defines the time requirement (in minutes) for earning Enigmatic Dice from killing mobs.\n" +
                    "This is NOT a cooldown.\n" +
                    "For every X minutes the player spends in the world, they are allowed to obtain 1 die from mobs.\n" +
                    "Example: If set to 10, then after 60 minutes of total playtime, the player may have earned up to 6 dice from mobs.\n" +
                    "Set to 0 to disable the limitation entirely.")
            .defineInRange("mobDiceTimeInterval", 10, 0, 100000);

    private static final ForgeConfigSpec.ConfigValue<Double> ENIGMATIC_DIE_BLOCK_DROP_CHANCE = BUILDER
            .comment("Chance for Enigmatic Die to drop from a block when broken by a player.\n" +
                    "For example, a value of 0.001 means a 0.1% chance to drop.")
            .defineInRange("enigmaticDieBlockDropChance", 0.001, 0.0, 1.0);

    private static final ForgeConfigSpec.ConfigValue<Integer> BLOCK_DICE_TIME_INTERVAL = BUILDER
            .comment("Defines the time requirement (in minutes) for earning Enigmatic Dice from breaking blocks.\n" +
                    "This is NOT a cooldown.\n" +
                    "For every X minutes the player spends in the world, they are allowed to obtain 1 die from blocks.\n" +
                    "Example: If set to 10, then after 60 minutes of total playtime, the player may have earned up to 6 dice from blocks.\n" +
                    "Set to 0 to disable the limitation entirely.")
            .defineInRange("blockDiceTimeInterval", 10, 0, 100000);

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
            .defineInRange("biomeSearchRadius", 4480, 256, 100000);

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

    private static final ForgeConfigSpec.ConfigValue<Double> UNEQUAL_EXCHANGE_ATTACK_DAMAGE = BUILDER
            .comment("Unequal Exchange Attack Damage.")
            .define("unequalExchangeAttackDamage", 1.0);

    private static final ForgeConfigSpec.ConfigValue<Double> UNEQUAL_EXCHANGE_ATTACK_SPEED = BUILDER
            .comment("Unequal Exchange Attack Speed.")
            .define("unequalExchangeAttackSpeed", 1.6);

    private static final ForgeConfigSpec.ConfigValue<Double> SOUL_EATER_CHARGED_ATTACK_DAMAGE_PER_HP = BUILDER
            .comment("Extra magic damage of Soul Eater's charged attack per HP spent.")
            .defineInRange("soulEaterChargedAttackDamagePerHP", 2.5, 0.0, 100.0);

    private static final ForgeConfigSpec.ConfigValue<Integer> SOUL_EATER_CHARGE_DURATION = BUILDER
            .comment("The duration of the effect of a charged Soul Eater attack (in seconds).")
            .defineInRange("soulEaterChargeDuration", 60, 0, 100000);

    private static final ForgeConfigSpec.ConfigValue<Double> SOUL_EATER_HEAL_PERCENT_ON_KILL = BUILDER
            .comment("Percent of killed enemy's max HP restored to the player.\n" +
                    "Example: 0.1 = 10% of target's max HP.")
            .defineInRange("soulEaterHealPercentOnKill", 0.1, 0.0, 1.0);

    private static final ForgeConfigSpec.ConfigValue<Double> SOUL_EATER_MAX_HEALTH_STEAL_PERCENT = BUILDER
            .comment("Percent of killed enemy's max HP added to player's max HP as bonus.\n" +
                    "Example: 0.1 = +10% max HP of killed enemy.")
            .defineInRange("soulEaterMaxHealthStealPercent", 0.1, 0.0, 1.0);

    private static final ForgeConfigSpec.ConfigValue<Integer> SOUL_EATER_MAX_HEALTH_BUFF_DURATION = BUILDER
            .comment("Duration (in seconds) of max health buff after killing an enemy.")
            .defineInRange("soulEaterMaxHealthBuffDuration", 300, 0, 100000);

    private static final ForgeConfigSpec.ConfigValue<Double> SOUL_EATER_MAX_HEALTH_MULTIPLIER_LIMIT = BUILDER
            .comment("Maximum multiplier for player's max HP from Soul Eater.\n" +
                    "Example: 1.0 = Player's max HP can be increased up to 100%.")
            .defineInRange("soulEaterMaxHealthMultiplierLimit", 1.0, 0.0, 100.0);

    private static final ForgeConfigSpec.ConfigValue<Double> SOUL_EATER_ATTACK_DAMAGE = BUILDER
            .comment("Soul Eater Attack Damage.")
            .define("soulEaterAttackDamage", 10.0);

    private static final ForgeConfigSpec.ConfigValue<Double> SOUL_EATER_ATTACK_SPEED = BUILDER
            .comment("Soul Eater Attack Speed.")
            .define("soulEaterAttackSpeed", 2.0);

    private static final ForgeConfigSpec.ConfigValue<Double> GIANTS_RING_ATTACK_DAMAGE = BUILDER
            .comment("Bonus attack damage granted by Giant's Ring.")
            .defineInRange("giantsRingAttackDamage", 3.0, 0.0, 1024.0);

    private static final ForgeConfigSpec.ConfigValue<Double> GIANTS_RING_MAX_HEALTH = BUILDER
            .comment("Bonus max health granted by Giant's Ring.")
            .defineInRange("giantsRingMaxHealth", 10.0, 0.0, 1024.0);

    private static final ForgeConfigSpec.ConfigValue<Double> GIANTS_RING_KNOCKBACK_RESISTANCE = BUILDER
            .comment("Bonus knockback resistance granted by Giant's Ring.")
            .defineInRange("giantsRingKnockbackResistance", 1, 0.0, 1.0);

    private static final ForgeConfigSpec.ConfigValue<Double> MOON_SHARD_GRAVITY_REDUCTION  = BUILDER
            .comment("Gravity change applied while Moon Shard is in hotbar.\n" +
                    "Default Minecraft gravity is 0.08.")
            .defineInRange("moonShardGravityReduction", -0.06, -1.0, 1.0);

    private static final ForgeConfigSpec.ConfigValue<Double> MOON_GRAVITY_REDUCTION  = BUILDER
            .comment("Gravity change applied while holding the Moon item.\n" +
                    "Default Minecraft gravity is 0.08.")
            .defineInRange("moonGravityReduction", -0.1, -1.0, 1.0);

    private static final ForgeConfigSpec.ConfigValue<Double> GRAVITY_CORE_GRAVITY_MULTIPLIER = BUILDER
            .comment("Multiplier applied to gravity when holding SHIFT with Gravity Core.")
            .defineInRange("gravityCoreGravityMultiplier", 5.0, 0.0, 100.0);

    private static final ForgeConfigSpec.ConfigValue<Double> GRAVITY_CORE_IMPACT_RADIUS_COEFFICIENT = BUILDER
            .comment("Impact radius coefficient when falling with Gravity Core.\n" +
                    "Impact radius = fall speed * this value.")
            .defineInRange("gravityCoreImpactRadiusCoefficient", 1.0, 0.0, 10.0);

    private static final ForgeConfigSpec.ConfigValue<Double> GRAVITY_CORE_IMPACT_DAMAGE_COEFFICIENT = BUILDER
            .comment("Impact damage coefficient when falling with Gravity Core.\n" +
                    "Damage = player's attack damage * fall speed * this value.")
            .defineInRange("gravityCoreImpactDamageCoefficient", 0.5, 0.0, 10.0);

    private static final ForgeConfigSpec.ConfigValue<Double> GRAVITY_CORE_JUMP_STRENGTH = BUILDER
            .comment("Jump strength boost applied when using Gravity Core.")
            .defineInRange("gravityCoreJumpStrength", 5.0, 0.0, 50.0);

    private static final ForgeConfigSpec.IntValue GRAVITY_CORE_COOLDOWN = BUILDER
            .comment("Cooldown in ticks for Gravity Core jump (20 ticks = 1 second).")
            .defineInRange("gravityCoreCooldown", 0, 0, 100000);

    private static final ForgeConfigSpec.ConfigValue<Double> RING_OF_AGILITY_CHANCE_SCALE = BUILDER
            .comment("Multiplier for movement speed in dodge chance formula.\n" +
                    "Formula: dodgeChance = 1.0 - pow(0.99, movementSpeed * 100 * scale)")
            .defineInRange("ringOfAgilityChanceScale", 1.0, 0.0, 100.0);

    private static final ForgeConfigSpec.ConfigValue<Double> RING_OF_AGILITY_MAX_DODGE_CHANCE = BUILDER
            .comment("Maximum dodge chance.\n" +
                    "Example: 0.9 = 90% maximum dodge chance.")
            .defineInRange("ringOfAgilityMaxDodgeChance", 0.9, 0.0, 1.0);

    private static final ForgeConfigSpec.ConfigValue<Integer> DIVINE_SHIELD_COOLDOWN_TICKS = BUILDER
            .comment("Cooldown duration of the Divine Shield in ticks. (1 second = 20 ticks)")
            .defineInRange("divineShieldCooldownTicks", 400, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.ConfigValue<Integer> DIVINE_SHIELD_IMMUNITY_TICKS = BUILDER
            .comment("Duration of invulnerability after blocking damage with Divine Shield, in ticks. (1 second = 20 ticks)")
            .defineInRange("divineShieldImmunityTicks", 40, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.ConfigValue<Double> PERMAFROST_ATTACK_DAMAGE = BUILDER
            .comment("Permafrost Attack Damage.")
            .define("permafrostAttackDamage", 10.0);

    private static final ForgeConfigSpec.ConfigValue<Double> PERMAFROST_ATTACK_SPEED = BUILDER
            .comment("Permafrost Attack Speed.")
            .define("permafrostAttackSpeed", 2.0);

    private static final ForgeConfigSpec.ConfigValue<Integer> PERMAFROST_ATTACK_DEBUFF_DURATION = BUILDER
            .comment("Duration of the Permafrost debuff applied when hitting a target, in ticks. (1 second = 20 ticks)")
            .defineInRange("permafrostAttackDebuffDuration", 600, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.ConfigValue<Integer> PERMAFROST_ATTACK_MAX_STACKS = BUILDER
            .comment("Maximum number of Permafrost stacks applied from attacks.")
            .defineInRange("permafrostAttackMaxStacks", 10, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.ConfigValue<Double> PERMAFROST_ATTACK_REDUCTION_PER_STACK = BUILDER
            .comment("Movement/Flying speed reduction per Permafrost stack applied from attacks (0.05 = 5% slower).")
            .defineInRange("permafrostAttackReductionPerStack", 0.05, 0.0, 1.0);

    private static final ForgeConfigSpec.ConfigValue<Integer> PERMAFROST_AURA_RADIUS = BUILDER
            .comment("Radius of the Permafrost aura effect in blocks.")
            .defineInRange("permafrostAuraRadius", 5, 0, 32);

    private static final ForgeConfigSpec.ConfigValue<Integer> PERMAFROST_AURA_DEBUFF_DURATION = BUILDER
            .comment("Duration of the Permafrost aura debuff in ticks. (1 second = 20 ticks)")
            .defineInRange("permafrostAuraDebuffDuration", 100, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.ConfigValue<Double> PERMAFROST_AURA_REDUCTION_FACTOR = BUILDER
            .comment("Movement/Flying speed reduction factor from Permafrost aura. (0.2 = 20% slower)")
            .defineInRange("permafrostAuraReductionFactor", 0.2, 0.0, 1.0);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static double EnigmaticDieMobDropChance;
    public static double EnigmaticDieBlockDropChance;
    public static double EnigmaticDieChestChance;
    public static int BlockDiceTimeInterval;
    public static int MobDiceTimeInterval;
    public static int StructureSearchRadius;
    public static int BiomeSearchRadius;
    public static int BiomeHorizontalStep;
    public static int BiomeVerticalStep;
    public static double UnequalExchangeTargetHealthReduction;
    public static double UnequalExchangePlayerHealthReduction;
    public static double UnequalExchangeStatDebuff;
    public static int UnequalExchangeDebuffDuration;
    public static double UnequalExchangeAttackDamage;
    public static double UnequalExchangeAttackSpeed;
    public static double SoulEaterChargedAttackDamagePerHP;
    public static int SoulEaterChargeDuration;
    public static double SoulEaterHealPercentOnKill;
    public static double SoulEaterMaxHealthStealPercent;
    public static int SoulEaterMaxHealthBuffDuration;
    public static double SoulEaterMaxHealthMultiplierLimit;
    public static double SoulEaterAttackDamage;
    public static double SoulEaterAttackSpeed;
    public static double GiantsRingAttackDamage;
    public static double GiantsRingMaxHealth;
    public static double GiantsRingKnockbackResistance;
    public static double MoonShardGravityReduction;
    public static double MoonGravityReduction;
    public static double GravityCoreGravityMultiplier;
    public static double GravityCoreImpactRadiusCoefficient;
    public static double GravityCoreImpactDamageCoefficient;
    public static double GravityCoreJumpStrength;
    public static int GravityCoreCooldown;
    public static double RingOfAgilityChanceScale;
    public static double RingOfAgilityMaxDodgeChance;
    public static int DivineShieldCooldownTicks;
    public static int DivineShieldImmunityTicks;
    public static double PermafrostAttackDamage;
    public static double PermafrostAttackSpeed;
    public static int PermafrostAttackDebuffDuration;
    public static int PermafrostAttackMaxStacks;
    public static double PermafrostAttackReductionPerStack;
    public static int PermafrostAuraRadius;
    public static int PermafrostAuraDebuffDuration;
    public static double PermafrostAuraReductionFactor;

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
        BlockDiceTimeInterval = BLOCK_DICE_TIME_INTERVAL.get();
        MobDiceTimeInterval = MOB_DICE_TIME_INTERVAL.get();
        StructureSearchRadius = STRUCTURE_SEARCH_RADIUS.get();
        BiomeSearchRadius = BIOME_SEARCH_RADIUS.get();
        BiomeHorizontalStep = BIOME_HORIZONTAL_STEP.get();
        BiomeVerticalStep = BIOME_VERTICAL_STEP.get();
        UnequalExchangeTargetHealthReduction = UNEQUAL_EXCHANGE_TARGET_HEALTH_REDUCTION.get();
        UnequalExchangePlayerHealthReduction = UNEQUAL_EXCHANGE_PLAYER_HEALTH_REDUCTION.get();
        UnequalExchangeStatDebuff = UNEQUAL_EXCHANGE_STAT_DEBUFF.get();
        UnequalExchangeDebuffDuration = UNEQUAL_EXCHANGE_DEBUFF_DURATION.get();
        UnequalExchangeAttackDamage = UNEQUAL_EXCHANGE_ATTACK_DAMAGE.get();
        UnequalExchangeAttackSpeed = UNEQUAL_EXCHANGE_ATTACK_SPEED.get();
        SoulEaterChargedAttackDamagePerHP = SOUL_EATER_CHARGED_ATTACK_DAMAGE_PER_HP.get();
        SoulEaterChargeDuration = SOUL_EATER_CHARGE_DURATION.get();
        SoulEaterHealPercentOnKill = SOUL_EATER_HEAL_PERCENT_ON_KILL.get();
        SoulEaterMaxHealthStealPercent = SOUL_EATER_MAX_HEALTH_STEAL_PERCENT.get();
        SoulEaterMaxHealthBuffDuration = SOUL_EATER_MAX_HEALTH_BUFF_DURATION.get();
        SoulEaterMaxHealthMultiplierLimit = SOUL_EATER_MAX_HEALTH_MULTIPLIER_LIMIT.get();
        SoulEaterAttackDamage = SOUL_EATER_ATTACK_DAMAGE.get();
        SoulEaterAttackSpeed = SOUL_EATER_ATTACK_SPEED.get();
        GiantsRingAttackDamage = GIANTS_RING_ATTACK_DAMAGE.get();
        GiantsRingMaxHealth = GIANTS_RING_MAX_HEALTH.get();
        GiantsRingKnockbackResistance = GIANTS_RING_KNOCKBACK_RESISTANCE.get();
        MoonShardGravityReduction = MOON_SHARD_GRAVITY_REDUCTION.get();
        MoonGravityReduction = MOON_GRAVITY_REDUCTION.get();
        GravityCoreGravityMultiplier = GRAVITY_CORE_GRAVITY_MULTIPLIER.get();
        GravityCoreImpactRadiusCoefficient = GRAVITY_CORE_IMPACT_RADIUS_COEFFICIENT.get();
        GravityCoreImpactDamageCoefficient = GRAVITY_CORE_IMPACT_DAMAGE_COEFFICIENT.get();
        GravityCoreJumpStrength = GRAVITY_CORE_JUMP_STRENGTH.get();
        GravityCoreCooldown = Config.GRAVITY_CORE_COOLDOWN.get();
        RingOfAgilityChanceScale = RING_OF_AGILITY_CHANCE_SCALE.get();
        RingOfAgilityMaxDodgeChance = RING_OF_AGILITY_MAX_DODGE_CHANCE.get();
        DivineShieldCooldownTicks = DIVINE_SHIELD_COOLDOWN_TICKS.get();
        DivineShieldImmunityTicks = DIVINE_SHIELD_IMMUNITY_TICKS.get();
        PermafrostAttackDamage = PERMAFROST_ATTACK_DAMAGE.get();
        PermafrostAttackSpeed = PERMAFROST_ATTACK_SPEED.get();
        PermafrostAttackDebuffDuration = PERMAFROST_ATTACK_DEBUFF_DURATION.get();
        PermafrostAttackMaxStacks = PERMAFROST_ATTACK_MAX_STACKS.get();
        PermafrostAttackReductionPerStack = PERMAFROST_ATTACK_REDUCTION_PER_STACK.get();
        PermafrostAuraRadius = PERMAFROST_AURA_RADIUS.get();
        PermafrostAuraDebuffDuration = PERMAFROST_AURA_DEBUFF_DURATION.get();
        PermafrostAuraReductionFactor = PERMAFROST_AURA_REDUCTION_FACTOR.get();

        lootTables = LOOT_TABLES.get().stream()
                .map(ResourceLocation::new)
                .collect(Collectors.toList());
    }
}
