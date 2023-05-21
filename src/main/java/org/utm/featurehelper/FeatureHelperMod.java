package org.utm.featurehelper;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.utm.featurehelper.event.EventListener;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(
    modid = FeatureHelperMod.MODID,
    name = FeatureHelperMod.NAME,
    version = FeatureHelperMod.VERSION,
    acceptedMinecraftVersions = FeatureHelperMod.ACCEPTEDMINECRAFTVERSIONS
)
public class FeatureHelperMod {

    public static final String MODID = "featurehelper";
    public static final String NAME = "Feature Helper";
    public static final String VERSION = "1.0.0";
    public static final String ACCEPTEDMINECRAFTVERSIONS = "[1.7,1.8)";

    @Instance(MODID)
    public static FeatureHelperMod instance;

    @SidedProxy(clientSide = "org.utm.featurehelper.ClientProxy", serverSide = "org.utm.featurehelper.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(EventListener.instance);
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }

}
