package org.utm.featurehelper;

import org.utm.featurehelper.command.CommandCave;
import org.utm.featurehelper.command.CommandStructure;

import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandStructure());
        event.registerServerCommand(new CommandCave());
    }

}
