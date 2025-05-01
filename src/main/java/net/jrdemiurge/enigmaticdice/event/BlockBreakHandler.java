package net.jrdemiurge.enigmaticdice.event;

import net.jrdemiurge.enigmaticdice.Config;
import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnigmaticDice.MOD_ID)
public class BlockBreakHandler {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        Level level = (Level) event.getLevel();

        if (!level.isClientSide && player != null && !(player instanceof FakePlayer)) {

            if (level.random.nextFloat() < Config.EnigmaticDieBlockDropChance) {
                ItemStack drop = new ItemStack(ModItems.ENIGAMTIC_DIE.get());

                // Создаем предмет в мире на месте сломанного блока
                ItemEntity entity = new ItemEntity(level,
                        event.getPos().getX() + 0.5,
                        event.getPos().getY() + 0.5,
                        event.getPos().getZ() + 0.5,
                        drop);

                level.addFreshEntity(entity);
                player.displayClientMessage(Component.literal("§6How did this end up here?"), false);
            }
        }
    }
}
