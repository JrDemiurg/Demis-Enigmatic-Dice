package net.jrdemiurge.enigmaticdice.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

public class EnigmaticDiceGetLuckCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("enigmaticDiceGetLuck")
                .executes(EnigmaticDiceGetLuckCommand::getPlayerLuck);
    }

    private static int getPlayerLuck(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerPlayer pPlayer = source.getPlayer();

        float luckValue = pPlayer.getLuck();

        MutableComponent message = Component.literal("Your luck: " + String.format("%.2f", luckValue))
                .withStyle(ChatFormatting.GREEN);
        pPlayer.displayClientMessage(message, false);

        return 1;
    }
}