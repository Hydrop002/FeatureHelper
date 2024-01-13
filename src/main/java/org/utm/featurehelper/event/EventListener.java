package org.utm.featurehelper.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import org.utm.featurehelper.command.CommandStructure;
import org.utm.featurehelper.feature.patch.CavesHellPatcher;
import org.utm.featurehelper.feature.patch.CavesPatcher;
import org.utm.featurehelper.feature.patch.RavinePatcher;
import org.utm.featurehelper.render.RenderBoundingBox;
import org.utm.featurehelper.render.RenderTrail;

public class EventListener {
    
    public static EventListener instance = new EventListener();

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        EntityLivingBase entity = Minecraft.getMinecraft().renderViewEntity;
        RenderBoundingBox.instance.render(entity, event.partialTicks);
        RenderBoundingBox.instance.renderList(entity, event.partialTicks);
        RenderTrail.caveRenderer.renderList(entity, event.partialTicks);
        RenderTrail.caveHellRenderer.renderList(entity, event.partialTicks);
        RenderTrail.ravineRenderer.renderList(entity, event.partialTicks);
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.world.isRemote && event.entity instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.entity;
            CommandStructure command = (CommandStructure) MinecraftServer.getServer().getCommandManager().getCommands().get("structure");
            command.sendMessageToPlayer(player, event.world);
            CavesPatcher.sendMessageToPlayer(player, event.world);
            CavesHellPatcher.sendMessageToPlayer(player, event.world);
            RavinePatcher.sendMessageToPlayer(player, event.world);
        }
    }

    @SubscribeEvent
    public void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {}

}
