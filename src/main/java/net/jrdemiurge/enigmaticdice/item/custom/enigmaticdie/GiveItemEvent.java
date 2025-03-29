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

public class GiveItemEvent implements RandomEvent {
    private final String itemIdentifier;
    private final int rarity;
    private final int quantity;

    public GiveItemEvent(String itemIdentifier, int rarity) {
        this.itemIdentifier = itemIdentifier;
        this.rarity = rarity;
        this.quantity = 1;
    }

    public GiveItemEvent(String itemIdentifier, int rarity, int quantity) {
        this.itemIdentifier = itemIdentifier;
        this.rarity = rarity;
        this.quantity = Math.max(quantity, 1);  // Минимальное количество — 1
    }

    @Override
    public boolean  execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!RandomEvent.rollChance(pLevel, pPlayer, rarity)) return false;
        }
        ResourceLocation resourceLocation = new ResourceLocation(itemIdentifier);
        Item item = ForgeRegistries.ITEMS.getValue(resourceLocation);

        if (item == null) {
            pPlayer.displayClientMessage(Component.literal("Item not found: " + itemIdentifier), false);
            return false;
        }

        // ItemStack itemStack = new ItemStack(item);

        ItemEntity keyEntity = new ItemEntity(
                pLevel,
                pPlayer.getX(),
                pPlayer.getY() + 1,
                pPlayer.getZ(),
                new ItemStack(item, quantity)
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
