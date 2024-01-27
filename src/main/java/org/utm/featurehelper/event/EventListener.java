package org.utm.featurehelper.event;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventListener {
    
    public static EventListener instance = new EventListener();

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        EntityLivingBase entity = Minecraft.getInstance().player;
        // RenderBoundingBox.instance.render(entity, event.getPartialTicks());
        // RenderBoundingBox.instance.renderList(entity, event.getPartialTicks());
        // RenderTrail.caveRenderer.renderList(entity, event.getPartialTicks());
        // RenderTrail.caveHellRenderer.renderList(entity, event.getPartialTicks());
        // RenderTrail.ravineRenderer.renderList(entity, event.getPartialTicks());
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.getWorld().isRemote && event.getEntity() instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
            // CommandStructure.sendMessageToPlayer(player, event.getWorld());
            // CavesPatcher.sendMessageToPlayer(player, event.getWorld());
            // CavesHellPatcher.sendMessageToPlayer(player, event.getWorld());
            // RavinePatcher.sendMessageToPlayer(player, event.getWorld());
        }
    }

}
