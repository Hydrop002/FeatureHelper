package org.utm.featurehelper;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import org.utm.featurehelper.command.CommandCave;
import org.utm.featurehelper.command.CommandStructure;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import org.utm.featurehelper.network.MessageBoundingBox;
import org.utm.featurehelper.network.NetworkManager;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        NetworkManager.instance.registerMessage(MessageBoundingBox.handler, MessageBoundingBox.class, 0, Side.CLIENT);
    }

    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandStructure());
        event.registerServerCommand(new CommandCave());
    }

}
