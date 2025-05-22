package net.jrdemiurge.enigmaticdice;

import com.mojang.logging.LogUtils;
import net.jrdemiurge.enigmaticdice.commands.EnigmaticDiceCommand;
import net.jrdemiurge.enigmaticdice.commands.EnigmaticDiceGetLuckCommand;
import net.jrdemiurge.enigmaticdice.commands.EnigmaticDiceSimulateCommand;
import net.jrdemiurge.enigmaticdice.effect.ModEffects;
import net.jrdemiurge.enigmaticdice.event.BlockBreakHandler;
import net.jrdemiurge.enigmaticdice.event.LootEventHandler;
import net.jrdemiurge.enigmaticdice.event.MobDropHandler;
import net.jrdemiurge.enigmaticdice.item.ModCreativeTabs;
import net.jrdemiurge.enigmaticdice.item.ModItems;
import net.jrdemiurge.enigmaticdice.item.custom.Antimatter;
import net.jrdemiurge.enigmaticdice.scheduler.Scheduler;
import net.jrdemiurge.enigmaticdice.sound.ModSounds;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(EnigmaticDice.MOD_ID)
public class EnigmaticDice {
    public static final String MOD_ID = "enigmaticdice";
    public static final Logger LOGGER = LogUtils.getLogger();

    public EnigmaticDice() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModCreativeTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModSounds.register(modEventBus);
        ModEffects.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new Scheduler());
        MinecraftForge.EVENT_BUS.register(new LootEventHandler());
        MinecraftForge.EVENT_BUS.register(new MobDropHandler());
        MinecraftForge.EVENT_BUS.register(new BlockBreakHandler());
        modEventBus.addListener(this::addCreative);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(Antimatter::init);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        event.getServer().getCommands().getDispatcher().register(
                EnigmaticDiceCommand.create()
        );
        event.getServer().getCommands().getDispatcher().register(
                EnigmaticDiceSimulateCommand.create()
        );
        event.getServer().getCommands().getDispatcher().register(
                EnigmaticDiceGetLuckCommand.create()
        );
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }
}
