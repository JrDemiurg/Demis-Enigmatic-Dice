 package net.jrdemiurge.enigmaticdice.event;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnigmaticDice.MOD_ID)
public class AdvancementHandler {

    @SubscribeEvent
    public static void onAdvancementEarned(AdvancementEvent.AdvancementEarnEvent  event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        Level level = player.level();
        Advancement advancement = event.getAdvancement();
        DisplayInfo display = advancement.getDisplay();

        if (display != null) {
            FrameType frame = display.getFrame();
            if (frame == FrameType.GOAL || frame == FrameType.CHALLENGE) {
                ItemStack drop = new ItemStack(ModItems.ENIGAMTIC_DIE.get());

                ItemEntity entity = new ItemEntity(level,
                        player.getX() + 0.5,
                        player.getY() + 0.5,
                        player.getZ() + 0.5,
                        drop);

                level.addFreshEntity(entity);
                player.displayClientMessage(Component.translatable("enigmaticdice.advancement_earned."+ level.random.nextInt(5)), false);
            }
        }
    }
}
