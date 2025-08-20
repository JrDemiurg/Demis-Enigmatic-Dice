package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.jrdemiurge.enigmaticdice.scheduler.Scheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ExplosionEvent extends RandomEvent {
    private final float power;
    private final boolean causesFire;
    private final boolean breakBlocks;

    public ExplosionEvent(float power, boolean causesFire, boolean breakBlocks, int rarity) {
        this.power = power;
        this.causesFire = causesFire;
        this.breakBlocks = breakBlocks;
        this.rarity = rarity;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!rollChance(pLevel, pPlayer, rarity)) return false;
        }

        BlockPos pos = pPlayer.blockPosition();

        pLevel.playSound(null, pos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.2F, 1.0F);

        PrimedTnt tnt = new PrimedTnt(pLevel, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, null);
        pLevel.addFreshEntity(tnt);

        Scheduler.schedule(() -> {
            BlockPos tntPos = tnt.blockPosition();
            tnt.remove(Entity.RemovalReason.KILLED);
            pLevel.explode(null, tntPos.getX(), tntPos.getY(), tntPos.getZ(), power, causesFire, breakBlocks ? Level.ExplosionInteraction.BLOCK : Level.ExplosionInteraction.NONE);
        }, 75);

        pPlayer.displayClientMessage(Component.translatable("enigmaticdice.event.explosion"), false);
        return true;
    }
}