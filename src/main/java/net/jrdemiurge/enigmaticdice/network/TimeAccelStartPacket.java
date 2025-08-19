package net.jrdemiurge.enigmaticdice.network;

import net.jrdemiurge.enigmaticdice.event.ClientTimeHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TimeAccelStartPacket {
    private final int multiplier;
    private final int durationTicks;

    public TimeAccelStartPacket(int multiplier, int durationTicks) {
        this.multiplier = multiplier;
        this.durationTicks = durationTicks;
    }

    public static void encode(TimeAccelStartPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.multiplier);
        buf.writeVarInt(msg.durationTicks);
    }

    public static TimeAccelStartPacket decode(FriendlyByteBuf buf) {
        int mul = buf.readVarInt();
        int dur = buf.readVarInt();
        return new TimeAccelStartPacket(mul, dur);
    }

    public static void handle(TimeAccelStartPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientTimeHandler.start(msg.multiplier, msg.durationTicks);
        });
        ctx.get().setPacketHandled(true);
    }
}
