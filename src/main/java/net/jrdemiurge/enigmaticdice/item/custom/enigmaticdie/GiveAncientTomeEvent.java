package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class GiveAncientTomeEvent implements RandomEvent {
    private final int rarity;

    public GiveAncientTomeEvent(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean  execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!RandomEvent.rollChance(pLevel, pPlayer, rarity, true)) return false;
        }

        ResourceLocation resourceLocation = new ResourceLocation("quark:ancient_tome");
        Item item = ForgeRegistries.ITEMS.getValue(resourceLocation);
        if (item == null) {
            EnigmaticDice.LOGGER.error("Item not found: {}", "quark:ancient_tome");
            return false;
        }

        List<Enchantment> allEnchantments = new ArrayList<>(ForgeRegistries.ENCHANTMENTS.getValues());
        if (allEnchantments.isEmpty()) {
            EnigmaticDice.LOGGER.warn("No enchantments found in registry.");
            return false;
        }

        Enchantment chosenEnchantment = allEnchantments.get(pLevel.random.nextInt(allEnchantments.size()));
        ResourceLocation enchantmentId = ForgeRegistries.ENCHANTMENTS.getKey(chosenEnchantment);
        if (enchantmentId == null) {
            EnigmaticDice.LOGGER.warn("Chosen enchantment has no ID.");
            return false;
        }

        ItemStack itemStack = new ItemStack(item);

        ListTag enchantmentList = new ListTag();
        CompoundTag enchantmentTag = new CompoundTag();
        enchantmentTag.putString("id", enchantmentId.toString());
        enchantmentTag.putShort("lvl", (short) 1); // Уровень 1
        enchantmentList.add(enchantmentTag);

        CompoundTag tag = new CompoundTag();
        tag.put("StoredEnchantments", enchantmentList);
        itemStack.setTag(tag);

        ItemEntity keyEntity = new ItemEntity(
                pLevel,
                pPlayer.getX(),
                pPlayer.getY() + 1,
                pPlayer.getZ(),
                itemStack
        );
        pLevel.addFreshEntity(keyEntity);

        MutableComponent message = Component.translatable("enigmaticdice.gift.blue");
        pPlayer.displayClientMessage(message, false);
        return true;
    }

    @Override
    public boolean simulationExecute(Level pLevel, Player pPlayer) {
        return RandomEvent.rollChance(pLevel, pPlayer, rarity, true);
    }
}
