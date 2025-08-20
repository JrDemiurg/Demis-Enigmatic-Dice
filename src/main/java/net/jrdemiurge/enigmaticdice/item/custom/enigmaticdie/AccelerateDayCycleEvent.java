package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.jrdemiurge.enigmaticdice.network.NetworkHandler;
import net.jrdemiurge.enigmaticdice.network.TimeAccelStartPacket;
import net.jrdemiurge.enigmaticdice.scheduler.Scheduler;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

// TODO: Проверить с шейдерами и посмотреть как бегут облака, чтобы выбрать скорость
public class AccelerateDayCycleEvent extends RandomEvent {
    public static boolean active;

    public AccelerateDayCycleEvent(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!rollChance(pLevel, pPlayer, rarity)) return false;
        }
        if (!pLevel.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) return false;

        if (active) return false;
        active = true;

        int multiplier = 40;
        int durationTicks = 20 * 60 * 5;

        NetworkHandler.INSTANCE.sendToServer(new TimeAccelStartPacket(multiplier, durationTicks));

        Scheduler.schedule(() -> {
            if (!(pLevel instanceof ServerLevel serverLevel)) return;
            long currentTime = serverLevel.getDayTime();
            for(ServerLevel serverlevel : pPlayer.getServer().getAllLevels()) {
                serverlevel.setDayTime(currentTime + multiplier - 1);
            }
        }, 0, 1, durationTicks);

        Scheduler.schedule(() -> {
            active = false;
            NetworkHandler.INSTANCE.sendToServer(new TimeAccelStartPacket(1, 0));
        }, durationTicks);

        MutableComponent message = Component.translatable("enigmaticdice.event.accelerate_day_cycle");
        pPlayer.displayClientMessage(message, false);
        return true;
    }
}
