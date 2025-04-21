package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
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
    private final String nbtString;
    private final String chatMessage;

    public GiveItemEvent(String itemIdentifier, int rarity, int quantity, String nbtString, String chatMessage) {
        this.itemIdentifier = itemIdentifier;
        this.rarity = rarity;
        this.quantity = Math.max(quantity, 1);
        this.nbtString = nbtString;
        this.chatMessage = chatMessage;
    }

    @Override
    public boolean  execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!RandomEvent.rollChance(pLevel, pPlayer, rarity, true)) return false;
        }
        ResourceLocation resourceLocation = new ResourceLocation(itemIdentifier);
        Item item = ForgeRegistries.ITEMS.getValue(resourceLocation);

        if (item == null) {
            EnigmaticDice.LOGGER.error("Item not found: {}", itemIdentifier);
            return false;
        }

        ItemStack itemStack = new ItemStack(item, quantity);

        if (!nbtString.isEmpty()) {
            try {
                CompoundTag nbt = TagParser.parseTag(nbtString);
                itemStack.setTag(nbt);
            } catch (CommandSyntaxException e) {
                EnigmaticDice.LOGGER.error("Error while parsing nbt: ", e);
            }
        }

        ItemEntity keyEntity = new ItemEntity(
                pLevel,
                pPlayer.getX(),
                pPlayer.getY() + 1,
                pPlayer.getZ(),
                itemStack
        );
        pLevel.addFreshEntity(keyEntity);

        MutableComponent message = Component.translatable(chatMessage);
        pPlayer.displayClientMessage(message, false);
        return true;
    }

    @Override
    public boolean simulationExecute(Level pLevel, Player pPlayer) {
        return RandomEvent.rollChance(pLevel, pPlayer, rarity, true);
    }
}
