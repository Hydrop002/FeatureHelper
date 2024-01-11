package org.utm.featurehelper.event;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import org.utm.featurehelper.command.CommandStructure;
import org.utm.featurehelper.feature.patch.CavesHellPatcher;
import org.utm.featurehelper.feature.patch.CavesPatcher;
import org.utm.featurehelper.feature.patch.RavinePatcher;
import org.utm.featurehelper.render.RenderBoundingBox;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.utm.featurehelper.render.RenderCaveHellTrail;
import org.utm.featurehelper.render.RenderCaveTrail;
import org.utm.featurehelper.render.RenderRavineTrail;

public class EventListener {
    
    public static EventListener instance = new EventListener();

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        EntityLivingBase entity = Minecraft.getMinecraft().renderViewEntity;
        RenderBoundingBox.instance.render(entity, event.partialTicks);
        RenderBoundingBox.instance.renderList(entity, event.partialTicks);
        RenderCaveTrail.instance.renderList(entity, event.partialTicks);
        RenderCaveHellTrail.instance.renderList(entity, event.partialTicks);
        RenderRavineTrail.instance.renderList(entity, event.partialTicks);
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.world.isRemote && event.entity instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.entity;
            CommandStructure command = (CommandStructure) MinecraftServer.getServer().getCommandManager().getCommands().get("structure");
            command.sendMessageToPlayer(player);
            CavesPatcher.sendMessageToPlayer(player);
            CavesHellPatcher.sendMessageToPlayer(player);
            RavinePatcher.sendMessageToPlayer(player);
        }
    }

}
