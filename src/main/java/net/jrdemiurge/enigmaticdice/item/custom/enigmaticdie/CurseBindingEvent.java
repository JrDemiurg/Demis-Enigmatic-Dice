package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import java.util.EnumSet;
import java.util.Map;


public class CurseBindingEvent extends RandomEvent {
    private final int rarity;

    public CurseBindingEvent(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!RandomEvent.rollChance(pLevel, pPlayer, rarity, isPositiveEvent())) return false;
        }

        boolean enchantedAny = false;

        for (EquipmentSlot slot : EnumSet.of(
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)) {

            ItemStack itemStack = pPlayer.getItemBySlot(slot);

            if (!itemStack.isEmpty() && !EnchantmentHelper.hasBindingCurse(itemStack)) {
                Map<Enchantment, Integer> enchantments =
                        EnchantmentHelper.getEnchantments(itemStack);
                enchantments.put(Enchantments.BINDING_CURSE, 1);
                EnchantmentHelper.setEnchantments(enchantments, itemStack);
                enchantedAny = true;
            }
        }

        if (enchantedAny) {
            pPlayer.displayClientMessage(Component.translatable("enigmaticdice.event.binding_curse"), false);
            return true;
        }

        return false;
    }

    @Override
    public boolean simulationExecute(Level pLevel, Player pPlayer) {
        return RandomEvent.rollChance(pLevel, pPlayer, rarity, isPositiveEvent());
    }
}
