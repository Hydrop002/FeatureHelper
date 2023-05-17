package org.utm.featurehelper.event;

import org.utm.featurehelper.command.CommandStructure;
import org.utm.featurehelper.render.RenderBoundingBox;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class EventListener {
    
    public static EventListener instance = new EventListener();

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        EntityLivingBase entity = Minecraft.getMinecraft().renderViewEntity;
        CommandStructure command = (CommandStructure) MinecraftServer.getServer().getCommandManager().getCommands().get("structure");
        RenderBoundingBox.instance.render(entity, command.getLastBoundingBox(), event.partialTicks);
        RenderBoundingBox.instance.renderList(entity, command.getBoundingBoxList(), event.partialTicks);
    }

}
