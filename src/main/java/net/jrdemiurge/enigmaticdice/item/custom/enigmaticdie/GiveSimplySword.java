package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class GiveSimplySword implements RandomEvent {
    private final int rarity;

    public GiveSimplySword(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean  execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!RandomEvent.rollChance(pLevel, pPlayer, rarity)) return false;
        }

        String[] swordNames = {
                "brimstone_claymore",
                "watcher_claymore",
                "storms_edge",
                "stormbringer",
                "bramblethorn",
                "watching_warglaive",
                "toxic_longsword",
                "emberblade",
                "hearthflame",
                "soulkeeper",
                "twisted_blade",
                "soulstealer",
                "soulrender",
                "soulpyre",
                "frostfall",
                "molten_edge",
                "livyatan",
                "icewhisper",
                "arcanethyst",
                "thunderbrand",
                "mjolnir",
                "slumbering_lichblade",
                "shadowsting",
                "dormant_relic",
                "whisperwind",
                "emberlash",
                "waxweaver",
                "hiveheart",
                "stars_edge",
                "wickpiercer",
                "tempest",
                "flamewind",
                "ribboncleaver",
                "caelestis"
        };

        String randomSword = swordNames[pLevel.getRandom().nextInt(swordNames.length)];
        String itemIdentifier = "simplyswords:" + randomSword;

        ResourceLocation resourceLocation = new ResourceLocation(itemIdentifier);
        Item item = ForgeRegistries.ITEMS.getValue(resourceLocation);


        // ItemStack itemStack = new ItemStack(item);

        ItemEntity keyEntity = new ItemEntity(
                pLevel,
                pPlayer.getX(),
                pPlayer.getY() + 1,
                pPlayer.getZ(),
                new ItemStack(item)
        );
        pLevel.addFreshEntity(keyEntity);

        MutableComponent message = Component.literal("Here, it's a gift.")
                .withStyle(getColorForRarity(rarity));
        pPlayer.displayClientMessage(message, false);
        return true;
    }

    private ChatFormatting getColorForRarity(int rarity) {
        return switch (rarity) {
            case 1 -> ChatFormatting.BLUE;      // Синий
            case 2 -> ChatFormatting.DARK_PURPLE; // Фиолетовый
            case 3 -> ChatFormatting.GOLD;      // Оранжевый
            case 4 -> ChatFormatting.DARK_RED; // Бирюзовый
            default -> ChatFormatting.WHITE;    // Белый
        };
    }

    @Override
    public boolean simulationExecute(Level pLevel, Player pPlayer) {
        return RandomEvent.rollChance(pLevel, pPlayer, rarity);
    }
}
