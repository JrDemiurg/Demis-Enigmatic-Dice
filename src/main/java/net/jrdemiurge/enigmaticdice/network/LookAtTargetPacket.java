package net.jrdemiurge.enigmaticdice.network;

import net.jrdemiurge.enigmaticdice.item.custom.crucibleofrile.ClientLookController;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class LookAtTargetPacket {
    private final UUID targetUuid;
    private final int durationTicks;

    public LookAtTargetPacket(UUID targetUuid, int durationTicks) {
        this.targetUuid = targetUuid;
        this.durationTicks = durationTicks;
    }

    public static void encode(LookAtTargetPacket pkt, FriendlyByteBuf buf) {
        buf.writeUUID(pkt.targetUuid);
        buf.writeVarInt(pkt.durationTicks);
    }

    public static LookAtTargetPacket decode(FriendlyByteBuf buf) {
        return new LookAtTargetPacket(buf.readUUID(), buf.readVarInt());
    }

    public static void handle(LookAtTargetPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientLookController.startOrExtend(pkt.targetUuid, pkt.durationTicks));
        ctx.get().setPacketHandled(true);
    }
}

