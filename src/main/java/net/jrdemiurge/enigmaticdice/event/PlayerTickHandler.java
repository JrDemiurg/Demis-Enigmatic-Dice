package net.jrdemiurge.enigmaticdice.event;

import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.custom.UnequalExchange;
import net.jrdemiurge.enigmaticdice.item.custom.unequalexchange.UnequalExchangeDataStorage;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = EnigmaticDice.MOD_ID)
public class PlayerTickHandler {

    private static final UUID HEALTH_DEBUFF_UUID = UUID.fromString("5f1d9b20-dfd6-4284-9d3a-004776a87bfd");
    private static final UUID ATTACK_SPEED_DEBUFF_UUID = UUID.fromString("27a72973-be25-44f0-9613-187812e4627c");
    private static final UUID ARMOR_DEBUFF_UUID = UUID.fromString("44371e08-d7ae-4344-8cfa-95c9fd4825c2");
    private static final UUID ARMOR_TOUGHNESS_DEBUFF_UUID = UUID.fromString("a6fb771d-97de-4883-86a8-1ba2b8db59c2");
    private static final UUID SPEED_DEBUFF_UUID = UUID.fromString("68403af9-c76e-4b08-8196-f264d872876d");

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (!player.level().isClientSide) {
            var data = UnequalExchangeDataStorage.get(player);
            data.tick();

            if (data.isExpired()) {
                if (player.getAttribute(Attributes.MAX_HEALTH).getModifier(HEALTH_DEBUFF_UUID) != null) {
                    player.getAttribute(Attributes.MAX_HEALTH).removeModifier(HEALTH_DEBUFF_UUID);
                }
                if (player.getAttribute(Attributes.ATTACK_SPEED).getModifier(ATTACK_SPEED_DEBUFF_UUID) != null) {
                    player.getAttribute(Attributes.ATTACK_SPEED).removeModifier(ATTACK_SPEED_DEBUFF_UUID);
                }
                if (player.getAttribute(Attributes.ARMOR).getModifier(ARMOR_DEBUFF_UUID) != null) {
                    player.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_DEBUFF_UUID);
                }
                if (player.getAttribute(Attributes.ARMOR_TOUGHNESS).getModifier(ARMOR_TOUGHNESS_DEBUFF_UUID) != null) {
                    player.getAttribute(Attributes.ARMOR_TOUGHNESS).removeModifier(ARMOR_TOUGHNESS_DEBUFF_UUID);
                }
                if (player.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(SPEED_DEBUFF_UUID) != null) {
                    player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SPEED_DEBUFF_UUID);
                }
                data.reset();
                return;
            }

            if (data.getHitCount() > 0 && player.getAttribute(Attributes.MAX_HEALTH).getModifier(HEALTH_DEBUFF_UUID) == null) {
                UnequalExchange.updateModifiers(player, data);
            }
        }
    }
}
