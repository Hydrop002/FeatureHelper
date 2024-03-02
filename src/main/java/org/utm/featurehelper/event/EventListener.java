package org.utm.featurehelper.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.utm.featurehelper.command.CommandStructure;
import org.utm.featurehelper.render.RenderBoundingBox;
import org.utm.featurehelper.render.RenderTrail;

public class EventListener {
    
    public static EventListener instance = new EventListener();

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        ActiveRenderInfo renderInfo = Minecraft.getInstance().gameRenderer.getMainCamera();
        MatrixStack stack = event.getMatrixStack();
        ClippingHelper clippingHelper = new ClippingHelper(stack.last().pose(), event.getProjectionMatrix());
        RenderBoundingBox.instance.render(renderInfo, stack, clippingHelper);
        RenderBoundingBox.instance.renderList(renderInfo, stack, clippingHelper);
        RenderTrail.instance.renderList(renderInfo, stack, clippingHelper);
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.getWorld().isClientSide() && event.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
            CommandStructure.sendMessageToPlayer(player, (ServerWorld) event.getWorld());
            //CommandCarve.sendMessageToPlayer(player, event.getWorld());
        }
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        if (!event.getWorld().isClientSide()) {
            Chunk chunk = (Chunk) event.getChunk();
            Heightmap heightMap = chunk.heightmaps.get(Heightmap.Type.WORLD_SURFACE);
            chunk.heightmaps.put(Heightmap.Type.WORLD_SURFACE_WG, heightMap);
            heightMap = chunk.heightmaps.get(Heightmap.Type.OCEAN_FLOOR);
            chunk.heightmaps.put(Heightmap.Type.OCEAN_FLOOR_WG, heightMap);
        }
    }

}
