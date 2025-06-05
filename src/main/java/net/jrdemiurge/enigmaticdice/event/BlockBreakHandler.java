package net.jrdemiurge.enigmaticdice.event;

import net.jrdemiurge.enigmaticdice.Config;
import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.ModItems;
import net.jrdemiurge.enigmaticdice.stat.ModStats;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
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
                if (canObtainDice(player, ModStats.OBTAINED_DICE_FROM_BLOCK)) {
                    ItemStack drop = new ItemStack(ModItems.ENIGAMTIC_DIE.get());

                    ItemEntity entity = new ItemEntity(level,
                            event.getPos().getX() + 0.5,
                            event.getPos().getY() + 0.5,
                            event.getPos().getZ() + 0.5,
                            drop);

                    level.addFreshEntity(entity);

                    player.awardStat(ModStats.OBTAINED_DICE_FROM_BLOCK);
                    player.displayClientMessage(Component.translatable("enigmaticdice.block_break"), false);
                }
            }
        }
    }

    public static boolean canObtainDice(Player player, ResourceLocation statKey) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (Config.BlockDiceTimeInterval == 0) return true;
            int playTimeTicks = serverPlayer.getStats().getValue(Stats.CUSTOM.get(Stats.TOTAL_WORLD_TIME));
            int playTimeMinutes = playTimeTicks / (20 * 60);
            int allowedCount = playTimeMinutes / Config.BlockDiceTimeInterval;

            int currentCount = serverPlayer.getStats().getValue(Stats.CUSTOM.get(statKey));

            return currentCount < allowedCount;
        }
        return false;
    }
}
