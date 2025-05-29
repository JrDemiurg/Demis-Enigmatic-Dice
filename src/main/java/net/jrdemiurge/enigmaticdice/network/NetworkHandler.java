package net.jrdemiurge.enigmaticdice.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("enigmaticdice", "simple_channel"),
            () -> "1.0",
            s -> true,
            s -> true
    );

    public static void register() {
        int id = 0;
        INSTANCE.registerMessage(id++, DoubleJumpPacket.class,
                DoubleJumpPacket::encode,
                DoubleJumpPacket::decode,
                DoubleJumpPacket::handle
        );
    }
}
