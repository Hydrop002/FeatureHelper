package org.utm.featurehelper;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.utm.featurehelper.event.EventListener;

@Mod(
        modid = FeatureHelperMod.MODID,
        name = FeatureHelperMod.NAME,
        version = FeatureHelperMod.VERSION,
        acceptedMinecraftVersions = FeatureHelperMod.ACCEPTEDMINECRAFTVERSIONS
)
public class FeatureHelperMod
{
    public static final String MODID = "featurehelper";
    public static final String NAME = "Feature Helper";
    public static final String VERSION = "1.0.0";
    public static final String ACCEPTEDMINECRAFTVERSIONS = "[1.8,1.9)";

    @Mod.Instance(MODID)
    public static FeatureHelperMod instance;

    @SidedProxy(clientSide = "org.utm.featurehelper.ClientProxy", serverSide = "org.utm.featurehelper.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }
    
    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(EventListener.instance);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }
}
