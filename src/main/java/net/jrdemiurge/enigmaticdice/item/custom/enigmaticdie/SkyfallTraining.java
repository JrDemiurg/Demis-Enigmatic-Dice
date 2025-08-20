package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;


public class SkyfallTraining extends RandomEvent {

    public SkyfallTraining(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!rollChance(pLevel, pPlayer, rarity)) return false;
        }

        if (!pLevel.dimension().equals(Level.OVERWORLD)) {
            return false;
        }

        int emptySlot = -1;
        for (int i = 0; i < 9; i++) {
            if (pPlayer.getInventory().getItem(i).isEmpty()) {
                emptySlot = i;
                break;
            }
        }

        if (emptySlot == -1) {
            return false;
        }

        pPlayer.getInventory().setItem(emptySlot, new ItemStack(Items.WATER_BUCKET));

        double newY = pPlayer.getY() + 200;
        pPlayer.teleportTo(pPlayer.getX(), newY, pPlayer.getZ());

        MutableComponent message = Component.translatable("enigmaticdice.event.skyfall_training");
        pPlayer.displayClientMessage(message, false);
        return true;
    }
}
