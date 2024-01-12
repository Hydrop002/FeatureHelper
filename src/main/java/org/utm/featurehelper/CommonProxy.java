package org.utm.featurehelper;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import org.utm.featurehelper.command.*;
import org.utm.featurehelper.network.*;


public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        NetworkManager.instance.registerMessage(MessageBoundingBox.handler, MessageBoundingBox.class, 0, Side.CLIENT);
        NetworkManager.instance.registerMessage(MessageCaveTrail.handler, MessageCaveTrail.class, 1, Side.CLIENT);
        NetworkManager.instance.registerMessage(MessageCaveHellTrail.handler, MessageCaveHellTrail.class, 2, Side.CLIENT);
        NetworkManager.instance.registerMessage(MessageRavineTrail.handler, MessageRavineTrail.class, 3, Side.CLIENT);
        NetworkManager.instance.registerMessage(MessageRenderControl.handler, MessageRenderControl.class, 4, Side.CLIENT);
    }

    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandStructure());
        event.registerServerCommand(new CommandCave());
        event.registerServerCommand(new CommandCaveHell());
        event.registerServerCommand(new CommandRavine());
        event.registerServerCommand(new CommandPopulate());
    }

}
