package org.utm.featurehelper.event;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.utm.featurehelper.command.CommandCarve;
import org.utm.featurehelper.command.CommandStructure;
import org.utm.featurehelper.render.RenderBoundingBox;
import org.utm.featurehelper.render.RenderTrail;

public class EventListener {
    
    public static EventListener instance = new EventListener();

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        EntityLivingBase entity = Minecraft.getInstance().player;
        RenderBoundingBox.instance.render(entity, event.getPartialTicks());
        RenderBoundingBox.instance.renderList(entity, event.getPartialTicks());
        RenderTrail.instance.renderList(entity, event.getPartialTicks());
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.getWorld().isRemote && event.getEntity() instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
            CommandStructure.sendMessageToPlayer(player, event.getWorld());
            CommandCarve.sendMessageToPlayer(player, event.getWorld());
        }
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        if (!((World) event.getWorld()).isRemote) {
            Chunk chunk = (Chunk) event.getChunk();
            Heightmap heightMap = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE);
            chunk.heightMap.put(Heightmap.Type.WORLD_SURFACE_WG, heightMap);
            heightMap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR);
            chunk.heightMap.put(Heightmap.Type.OCEAN_FLOOR_WG, heightMap);
        }
    }

}
