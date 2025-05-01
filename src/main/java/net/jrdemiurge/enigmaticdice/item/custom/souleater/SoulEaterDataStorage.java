package net.jrdemiurge.enigmaticdice.item.custom.souleater;

import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SoulEaterDataStorage {
    private static final Map<UUID, SoulEaterData> PLAYER_DATA = new HashMap<>();

    public static SoulEaterData get(Player player) {
        return PLAYER_DATA.computeIfAbsent(player.getUUID(), id -> new SoulEaterData());
    }

    public static void remove(Player player) {
        PLAYER_DATA.remove(player.getUUID());
    }
}
