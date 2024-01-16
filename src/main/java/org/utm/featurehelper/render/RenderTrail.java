package org.utm.featurehelper.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RenderTrail {

    public static RenderTrail caveRenderer = new RenderTrail();
    public static RenderTrail caveHellRenderer = new RenderTrail();
    public static RenderTrail ravineRenderer = new RenderTrail();

    public ICamera camera;

    public List<List<Vec3>> posList = new ArrayList<List<Vec3>>();
    public boolean isRender = true;

    public void renderList(EntityLivingBase entity, float partialTicks) {
        if (!this.isRender || this.posList.isEmpty())
            return;
        if (this.camera == null)
            this.camera = new Frustum();
        double cameraX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        double cameraY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        double cameraZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
        float viewX = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
        float viewY = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
        this.camera.setPosition(cameraX, cameraY, cameraZ);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        Iterator<List<Vec3>> it = this.posList.iterator();
        boolean start = true;
        Vec3 prevPos = null;
        Vec3 forkPos = null;
        while (it.hasNext()) {
            Iterator<Vec3> tunnel = it.next().iterator();
            while (tunnel.hasNext()) {
                Vec3 pos = tunnel.next();
                if (pos == null) {
                    if (forkPos == null) forkPos = prevPos;
                    break;
                }
                if (tunnel.hasNext() || entity.ticksExisted % 20 < 10) {
                    AxisAlignedBB aabb = new AxisAlignedBB(pos.xCoord, pos.yCoord, pos.zCoord, pos.xCoord, pos.yCoord, pos.zCoord);
                    if (this.camera.isBoundingBoxInFrustum(aabb)) {
                        GlStateManager.pushMatrix();
                        GlStateManager.translate(pos.xCoord - cameraX, pos.yCoord - cameraY, pos.zCoord - cameraZ);
                        GlStateManager.rotate(180.0F - viewY, 0.0F, 1.0F, 0.0F);
                        GlStateManager.rotate(-viewX, 1.0F, 0.0F, 0.0F);
                        GlStateManager.disableDepth();
                        GlStateManager.depthMask(false);
                        GlStateManager.disableTexture2D();
                        GlStateManager.disableLighting();
                        GlStateManager.disableCull();
                        GlStateManager.disableBlend();
                        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
                        if (start) {
                            worldRenderer.pos(-0.1, 0.0, 0.0).color(255, 0, 0, 255).endVertex();
                            worldRenderer.pos(0.0, -0.1, 0.0).color(255, 0, 0, 255).endVertex();
                            worldRenderer.pos(0.1, 0.0, 0.0).color(255, 0, 0, 255).endVertex();
                            worldRenderer.pos(0.0, 0.1, 0.0).color(255, 0, 0, 255).endVertex();
                        } else {
                            worldRenderer.pos(-0.1, 0.0, 0.0).color(0, 255, 0, 255).endVertex();
                            worldRenderer.pos(0.0, -0.1, 0.0).color(0, 255, 0, 255).endVertex();
                            worldRenderer.pos(0.1, 0.0, 0.0).color(0, 255, 0, 255).endVertex();
                            worldRenderer.pos(0.0, 0.1, 0.0).color(0, 255, 0, 255).endVertex();
                        }
                        tessellator.draw();
                        GlStateManager.enableTexture2D();
                        GlStateManager.enableCull();
                        GlStateManager.disableBlend();
                        GlStateManager.depthMask(true);
                        GlStateManager.enableDepth();
                        GlStateManager.popMatrix();
                    }
                    if (prevPos != null) {
                        AxisAlignedBB lineAABB = new AxisAlignedBB(prevPos.xCoord, prevPos.yCoord, prevPos.zCoord, pos.xCoord, pos.yCoord, pos.zCoord);
                        if (this.camera.isBoundingBoxInFrustum(lineAABB)) {
                            GlStateManager.pushMatrix();
                            GlStateManager.translate(-cameraX, -cameraY, -cameraZ);
                            GlStateManager.depthMask(false);
                            GlStateManager.disableTexture2D();
                            GlStateManager.disableLighting();
                            GlStateManager.disableCull();
                            GlStateManager.disableBlend();
                            worldRenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
                            worldRenderer.pos(prevPos.xCoord, prevPos.yCoord, prevPos.zCoord).color(0, 255, 0, 255).endVertex();
                            worldRenderer.pos(pos.xCoord, pos.yCoord, pos.zCoord).color(0, 255, 0, 255).endVertex();
                            tessellator.draw();
                            GlStateManager.enableTexture2D();
                            GlStateManager.enableCull();
                            GlStateManager.disableBlend();
                            GlStateManager.depthMask(true);
                            GlStateManager.popMatrix();
                        }
                    }
                }
                start = false;
                prevPos = pos;
            }
            prevPos = forkPos;
        }
    }

}
