package net.jrdemiurge.enigmaticdice.event;

import net.jrdemiurge.enigmaticdice.Config;
import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnigmaticDice.MOD_ID)
public class LootEventHandler {

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation lootTable = event.getName();
        for (ResourceLocation targetChest : Config.lootTables) {
            if (lootTable.equals(targetChest)) {
                addEnigmaticDieToLoot(event);
            }
        }
    }

    private static void addEnigmaticDieToLoot(LootTableLoadEvent event) {
        double chance = Config.EnigmaticDieChestChance; // Получаем значение из конфига

        int totalWeight = 100; // Общее количество весов (можно изменить)
        int enigmaticDieWeight = (int) (chance * totalWeight); // Вес предмета
        int emptyWeight = totalWeight - enigmaticDieWeight; // Вес пустого слота

        // Убеждаемся, что веса корректны
        if (enigmaticDieWeight <= 0) enigmaticDieWeight = 1;
        if (emptyWeight <= 0) emptyWeight = 1;

        LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(ModItems.ENIGAMTIC_DIE.get())
                .setWeight(enigmaticDieWeight);

        LootPoolEntryContainer.Builder<?> emptyEntry = LootItem.lootTableItem(net.minecraft.world.item.Items.AIR)
                .setWeight(emptyWeight);

        LootPool pool = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .setBonusRolls(ConstantValue.exactly(0))
                .add(entry)
                .add(emptyEntry)
                .build();

        event.getTable().addPool(pool);
    }
}
