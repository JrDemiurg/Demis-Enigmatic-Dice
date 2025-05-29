package net.jrdemiurge.enigmaticdice.network;

import net.jrdemiurge.enigmaticdice.item.custom.GravityCore;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DoubleJumpPacket {
    public static void encode(DoubleJumpPacket msg, FriendlyByteBuf buf) {}
    public static DoubleJumpPacket decode(FriendlyByteBuf buf) {
        return new DoubleJumpPacket();
    }

    public static void handle(DoubleJumpPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                GravityCore.jump(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
