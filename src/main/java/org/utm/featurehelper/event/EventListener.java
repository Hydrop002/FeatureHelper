package org.utm.featurehelper.event;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
        EntityLivingBase entity = Minecraft.getMinecraft().player;
        RenderBoundingBox.instance.render(entity, event.getPartialTicks());
        RenderBoundingBox.instance.renderList(entity, event.getPartialTicks());
        RenderTrail.caveRenderer.renderList(entity, event.getPartialTicks());
        RenderTrail.caveHellRenderer.renderList(entity, event.getPartialTicks());
        RenderTrail.ravineRenderer.renderList(entity, event.getPartialTicks());
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.getWorld().isRemote && event.getEntity() instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
            CommandStructure command = (CommandStructure) player.getServer().getCommandManager().getCommands().get("structure");
            command.sendMessageToPlayer(player, event.getWorld());
            CavesPatcher.sendMessageToPlayer(player, event.getWorld());
            CavesHellPatcher.sendMessageToPlayer(player, event.getWorld());
            RavinePatcher.sendMessageToPlayer(player, event.getWorld());
        }
    }

}
