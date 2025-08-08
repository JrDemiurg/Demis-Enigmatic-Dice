package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.EnumSet;
import java.util.Map;


public class EternalBindingEvent implements RandomEvent {
    private final int rarity;
    private static final ResourceLocation ETERNAL_BINDING_ID = new ResourceLocation("enigmaticlegacy", "eternal_binding");

    public EternalBindingEvent(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!RandomEvent.rollChance(pLevel, pPlayer, rarity, false)) return false;
        }

        Enchantment eternalBinding = ForgeRegistries.ENCHANTMENTS.getValue(ETERNAL_BINDING_ID);
        if (eternalBinding == null) return false;

        boolean enchantedAny = false;

        for (EquipmentSlot slot : EnumSet.of(
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)) {

            ItemStack itemStack = pPlayer.getItemBySlot(slot);

            if (!itemStack.isEmpty() && !EnchantmentHelper.getEnchantments(itemStack).containsKey(eternalBinding)) {
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(itemStack);
                enchantments.put(eternalBinding, 1);
                EnchantmentHelper.setEnchantments(enchantments, itemStack);
                enchantedAny = true;
            }
        }

        if (enchantedAny) {
            pPlayer.displayClientMessage(Component.translatable("enigmaticdice.event.eternal_binding"), false);
            return true;
        }

        return false;
    }

    @Override
    public boolean simulationExecute(Level pLevel, Player pPlayer) {
        return RandomEvent.rollChance(pLevel, pPlayer, rarity, false);
    }
}
