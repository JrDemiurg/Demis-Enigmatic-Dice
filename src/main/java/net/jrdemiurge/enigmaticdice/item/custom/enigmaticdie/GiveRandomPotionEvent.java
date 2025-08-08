package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// можно сделать чтобы число эффектов, уровни и длительность увеличивались с удачей
public class GiveRandomPotionEvent implements RandomEvent {
    private final int rarity;
    private final boolean onlyVanillaEffects;
    private final Random random = new Random();

    public GiveRandomPotionEvent(int rarity, boolean onlyVanillaEffects) {
        this.rarity = rarity;
        this.onlyVanillaEffects = onlyVanillaEffects;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!RandomEvent.rollChance(pLevel, pPlayer, rarity, true)) return false;
        }

        ItemStack potion = new ItemStack(Items.SPLASH_POTION);

        int effectCount = 3 + random.nextInt(4); // 3 to 6 effects

        List<MobEffect> availableEffects = new ArrayList<>();
        for (MobEffect effect : ForgeRegistries.MOB_EFFECTS.getValues()) {
            if (onlyVanillaEffects) {
                var key = ForgeRegistries.MOB_EFFECTS.getKey(effect);
                if (key != null && "minecraft".equals(key.getNamespace())) {
                    availableEffects.add(effect);
                }
            } else {
                availableEffects.add(effect);
            }
        }

        Collections.shuffle(availableEffects, random);
        List<MobEffectInstance> selectedEffects = new ArrayList<>();

        for (int i = 0; i < Math.min(effectCount, availableEffects.size()); i++) {
            MobEffect effect = availableEffects.get(i);
            int amplifier;

            int duration;
            if (effect.isInstantenous()) {
                duration = 1;
                amplifier = random.nextInt(5);
            } else {
                int seconds = 10 + random.nextInt((15 * 60) - 10);
                duration = seconds * 20;
                amplifier = random.nextInt(3);
            }

            selectedEffects.add(new MobEffectInstance(effect, duration, amplifier));
        }

        PotionUtils.setCustomEffects(potion, selectedEffects);

        ListTag enchantments = new ListTag();
        CompoundTag fakeEnchant = new CompoundTag();
        fakeEnchant.putString("id", "0");
        fakeEnchant.putShort("lvl", (short) 0);
        enchantments.add(fakeEnchant);
        potion.getOrCreateTag().put("Enchantments", enchantments);

        if (onlyVanillaEffects) {
            potion.setHoverName(Component.translatable("item.enigmaticdice.vanilla_random_splash_potion")
                    .withStyle(style -> style.withItalic(false)));
        } else {
            potion.setHoverName(Component.translatable("item.enigmaticdice.modded_random_splash_potion")
                    .withStyle(style -> style.withItalic(false)));
        }

        ItemEntity entity = new ItemEntity(pLevel, pPlayer.getX(), pPlayer.getY() + 1, pPlayer.getZ(), potion);
        pLevel.addFreshEntity(entity);

        MutableComponent message;
        if (onlyVanillaEffects) {
            message = Component.translatable("enigmaticdice.event.vanilla_random_potion");
        } else {
            message = Component.translatable("enigmaticdice.event.modded_random_potion");
        }
        pPlayer.displayClientMessage(message, false);
        return true;
    }

    @Override
    public boolean simulationExecute(Level pLevel, Player pPlayer) {
        return RandomEvent.rollChance(pLevel, pPlayer, rarity, true);
    }
}
