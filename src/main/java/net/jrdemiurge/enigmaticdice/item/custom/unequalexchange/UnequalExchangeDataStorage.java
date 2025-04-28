package net.jrdemiurge.enigmaticdice.item.custom.unequalexchange;

import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UnequalExchangeDataStorage {
    private static final Map<UUID, UnequalExchangeData> PLAYER_DATA = new HashMap<>();

    public static UnequalExchangeData get(Player player) {
        return PLAYER_DATA.computeIfAbsent(player.getUUID(), id -> new UnequalExchangeData());
    }

    public static void remove(Player player) {
        PLAYER_DATA.remove(player.getUUID());
    }
}
