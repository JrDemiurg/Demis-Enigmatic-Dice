package net.jrdemiurge.enigmaticdice.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie.RandomEvent;
import net.jrdemiurge.enigmaticdice.item.custom.enigmaticdie.RandomEventManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.concurrent.CompletableFuture;

public class EnigmaticDiceCommand {

    private static final RandomEventManager eventManager = new RandomEventManager();

    public static LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("enigmaticDice")
                .requires(source -> source.hasPermission(2))
                .executes(EnigmaticDiceCommand::triggerRandomEvent)
                .then(Commands.argument("eventType", StringArgumentType.string())
                        .suggests(EnigmaticDiceCommand::suggestEventTypes)
                        .then(Commands.argument("forceExecution", BoolArgumentType.bool())
                                .executes(EnigmaticDiceCommand::triggerSpecificEventWithForce))
                        .executes(EnigmaticDiceCommand::triggerSpecificEvent));
    }

    private static CompletableFuture<Suggestions> suggestEventTypes(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        for (String eventName : eventManager.getEventNames()) {
            builder.suggest(eventName);
        }
        return builder.buildFuture();
    }

    private static int triggerSpecificEvent(CommandContext<CommandSourceStack> context) {
        return executeEvent(context, false);
    }

    private static int triggerSpecificEventWithForce(CommandContext<CommandSourceStack> context) {
        boolean forceExecution = BoolArgumentType.getBool(context, "forceExecution");
        return executeEvent(context, forceExecution);
    }

    private static int executeEvent(CommandContext<CommandSourceStack> context, boolean forceExecution) {
        CommandSourceStack source = context.getSource();
        Level pLevel = source.getLevel();
        ServerPlayer pPlayer = source.getPlayer();
        String eventType = StringArgumentType.getString(context, "eventType");

        RandomEvent event = eventManager.getEventByName(eventType);
        if (event != null) {
            event.execute(pLevel, pPlayer, forceExecution);
            return 1;
        }

        return 0;
    }
    /*private static int triggerSpecificEvent(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Level pLevel = source.getLevel();
        ServerPlayer pPlayer = source.getPlayer();
        String eventType = StringArgumentType.getString(context, "eventType");

        if (eventManager.getEventByName(eventType) != null) {
            eventManager.getEventByName(eventType).execute(pLevel, pPlayer);
            return 1;
        }

        return 0;
    }*/

    private static int triggerRandomEvent(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Level pLevel = source.getLevel();
        ServerPlayer pPlayer = source.getPlayer();

        eventManager.triggerRandomEvent(pLevel, pPlayer);
        return 1;
    }
}
