package net.jrdemiurge.enigmaticdice.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.jrdemiurge.enigmaticdice.EnigmaticDice;
import net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie.RandomEventManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class EnigmaticDiceSimulateCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("enigmaticDiceSimulate")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("count", IntegerArgumentType.integer(1))
                        .executes(EnigmaticDiceSimulateCommand::simulateEvents));
    }

    private static int simulateEvents(CommandContext<CommandSourceStack> context) {
        if (EnigmaticDice.eventManager == null) EnigmaticDice.eventManager = new RandomEventManager();

        CommandSourceStack source = context.getSource();
        Level pLevel = source.getLevel();
        ServerPlayer pPlayer = source.getPlayer();

        int count = IntegerArgumentType.getInteger(context, "count");

        EnigmaticDice.eventManager.simulationRandomEvent(pLevel, pPlayer, count);
        EnigmaticDice.eventManager.saveLuckRangeResultsToFile(pLevel,EnigmaticDice.eventManager.buildLuckRangeResults(pLevel, count),count);
        pPlayer.displayClientMessage(Component.literal("Simulated " + count + " events successfully!"), false);
        pPlayer.displayClientMessage(Component.literal("Results saved in the world folder as: enigmatic_dice_simulation_results.txt"), false);
        return 1;
    }
}
