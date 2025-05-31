package net.jrdemiurge.enigmaticdice.event;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = EnigmaticDice.MOD_ID)
public class LivingFallHandler {

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (isWearingGravityCore(player)){
            event.setDamageMultiplier(0);
            return;
        }

        for (int i = 0; i <= 8; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(ModItems.MOON_SHARD.get()) || stack.is(ModItems.MOON.get())) {
                event.setDamageMultiplier(0);
                break;
            }
        }
    }

    private static boolean isWearingGravityCore(Player player) {
        return CuriosApi.getCuriosInventory(player)
                .map(handler -> !handler.findCurios(ModItems.GRAVITY_CORE.get()).isEmpty())
                .orElse(false);
    }
}
