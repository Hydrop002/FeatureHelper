package org.utm.featurehelper;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import org.utm.featurehelper.command.CommandCave;
import org.utm.featurehelper.command.CommandCaveHell;
import org.utm.featurehelper.command.CommandPopulate;
import org.utm.featurehelper.command.CommandStructure;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import org.utm.featurehelper.network.MessageBoundingBox;
import org.utm.featurehelper.network.MessageCaveHellTrail;
import org.utm.featurehelper.network.MessageCaveTrail;
import org.utm.featurehelper.network.NetworkManager;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        NetworkManager.instance.registerMessage(MessageBoundingBox.handler, MessageBoundingBox.class, 0, Side.CLIENT);
        NetworkManager.instance.registerMessage(MessageCaveTrail.handler, MessageCaveTrail.class, 1, Side.CLIENT);
        NetworkManager.instance.registerMessage(MessageCaveHellTrail.handler, MessageCaveHellTrail.class, 2, Side.CLIENT);
    }

    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandStructure());
        event.registerServerCommand(new CommandCave());
        event.registerServerCommand(new CommandCaveHell());
        event.registerServerCommand(new CommandPopulate());
    }

}
