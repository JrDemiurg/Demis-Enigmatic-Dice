package net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie;

import net.jrdemiurge.enigmaticdice.attribute.ModAttributes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

public class SummonSpiderFleasEvent extends RandomEvent {

    public SummonSpiderFleasEvent(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public boolean execute(Level pLevel, Player pPlayer, boolean guaranteed) {
        if (!guaranteed) {
            if (!rollChance(pLevel, pPlayer, rarity)) return false;
        }

        final int count = 8;
        final double radius = 4.0;

        for (int i = 0; i < count; i++) {
            double angle = pLevel.getRandom().nextDouble() * Math.PI * 2.0;

            double targetX = pPlayer.getX() + Math.cos(angle) * radius;
            double targetZ = pPlayer.getZ() + Math.sin(angle) * radius;

            int surfaceY = pLevel.getHeight(
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    (int) Math.floor(targetX),
                    (int) Math.floor(targetZ)
            );
            double targetY = surfaceY + 0.1;

            Spider spider = EntityType.SPIDER.create(pLevel);
            if (spider == null) continue;

            spider.moveTo(targetX, targetY, targetZ, 0.0F, 0.0F);

            AttributeInstance sizeAttr = spider.getAttribute(ModAttributes.SIZE_SCALE.get());
            if (sizeAttr != null) {
                sizeAttr.setBaseValue(0.1);
            }

            spider.setTarget(pPlayer);
            spider.setAggressive(true);
            spider.setPersistenceRequired();

            pLevel.addFreshEntity(spider);
        }

        MutableComponent message = Component.translatable("enigmaticdice.event.summon_spider_fleas");
        pPlayer.displayClientMessage(message, false);
        return true;
    }
}
