package net.jrdemiurge.enigmaticdice.event;

import net.jrdemiurge.enigmaticdice.Config;
import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EnigmaticDice.MOD_ID)
public class MobDropHandler {

    @SubscribeEvent
    public static void onMobDeath(LivingDropsEvent event) {
        DamageSource source = event.getSource();
        if (source.getEntity() instanceof Player && !(source.getEntity() instanceof FakePlayer)) {
            Player player = (Player) source.getEntity();

            if (player.level().random.nextFloat() < Config.EnigmaticDieMobDropChance) {
                ItemStack drop = new ItemStack(ModItems.ENIGAMTIC_DIE.get());

                event.getDrops().add(new ItemEntity(player.level(),
                        event.getEntity().getX(),
                        event.getEntity().getY(),
                        event.getEntity().getZ(),
                        drop));

                player.displayClientMessage(Component.translatable("enigmaticdice.mob_drop."+ player.level().random.nextInt(5)), false);
            }
        }
    }
}
