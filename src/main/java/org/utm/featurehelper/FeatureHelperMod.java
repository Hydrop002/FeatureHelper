package org.utm.featurehelper;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.utm.featurehelper.command.CommandStructure;
import org.utm.featurehelper.event.EventListener;
import org.utm.featurehelper.network.MessageBoundingBox;
import org.utm.featurehelper.network.MessageCarveTrail;
import org.utm.featurehelper.network.MessageRenderControl;
import org.utm.featurehelper.network.NetworkManager;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(FeatureHelperMod.MODID)
public class FeatureHelperMod
{
    public static final String MODID = "featurehelper";

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public FeatureHelperMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
        MinecraftForge.EVENT_BUS.register(EventListener.instance);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        NetworkManager.instance.registerMessage(0, MessageBoundingBox.class, MessageBoundingBox::encode, MessageBoundingBox::new, MessageBoundingBox::handler);
        NetworkManager.instance.registerMessage(1, MessageCarveTrail.class, MessageCarveTrail::encode, MessageCarveTrail::new, MessageCarveTrail::handler);
        NetworkManager.instance.registerMessage(2, MessageRenderControl.class, MessageRenderControl::encode, MessageRenderControl::new, MessageRenderControl::handler);
    }

    private void serverStarting(FMLServerStartingEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getServer().getCommands().getDispatcher();
        CommandStructure.register(dispatcher);
        //CommandCarve.register(dispatcher);
        //CommandFeature.register(dispatcher);
    }
}
