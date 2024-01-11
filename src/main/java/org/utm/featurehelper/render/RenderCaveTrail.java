package org.utm.featurehelper.render;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RenderCaveTrail {

    public static RenderCaveTrail instance = new RenderCaveTrail();

    public Frustrum frustrum = new Frustrum();

    public List<List<double[]>> posList = new ArrayList<List<double[]>>();

    public void renderList(EntityLivingBase entity, float partialTicks) {
        if (this.posList.isEmpty())
            return;
        double cameraX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        double cameraY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        double cameraZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
        float viewX = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
        float viewY = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
        this.frustrum.setPosition(cameraX, cameraY, cameraZ);
        Iterator<List<double[]>> it = this.posList.iterator();
        boolean start = true;
        double[] prevPos = null;
        double[] forkPos = null;
        while (it.hasNext()) {
            Iterator<double[]> tunnel = it.next().iterator();
            while (tunnel.hasNext()) {
                double[] pos = tunnel.next();
                if (pos == null) {
                    if (forkPos == null) forkPos = prevPos;
                    break;
                }
                if (tunnel.hasNext() || entity.ticksExisted % 20 < 10) {
                    AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(pos[0], pos[1], pos[2], pos[0], pos[1], pos[2]);
                    if (this.frustrum.isBoundingBoxInFrustum(aabb)) {
                        GL11.glPushMatrix();
                        GL11.glTranslated(pos[0] - cameraX, pos[1] - cameraY, pos[2] - cameraZ);
                        GL11.glRotatef(180.0F - viewY, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-viewX, 1.0F, 0.0F, 0.0F);
                        GL11.glDepthMask(false);
                        GL11.glDisable(GL11.GL_DEPTH_TEST);
                        GL11.glDisable(GL11.GL_TEXTURE_2D);
                        GL11.glDisable(GL11.GL_LIGHTING);
                        GL11.glDisable(GL11.GL_CULL_FACE);
                        Tessellator.instance.startDrawingQuads();
                        if (start)
                            Tessellator.instance.setColorRGBA(255, 0, 0, 255);
                        else
                            Tessellator.instance.setColorRGBA(0, 255, 0, 255);
                        Tessellator.instance.addVertex(-0.1, 0.0, 0.0);
                        Tessellator.instance.addVertex(0.0, -0.1, 0.0);
                        Tessellator.instance.addVertex(0.1, 0.0, 0.0);
                        Tessellator.instance.addVertex(0.0, 0.1, 0.0);
                        Tessellator.instance.draw();
                        GL11.glEnable(GL11.GL_DEPTH_TEST);
                        GL11.glEnable(GL11.GL_TEXTURE_2D);
                        GL11.glEnable(GL11.GL_LIGHTING);
                        GL11.glEnable(GL11.GL_CULL_FACE);
                        GL11.glDepthMask(true);
                        GL11.glPopMatrix();
                    }
                    if (prevPos != null) {
                        AxisAlignedBB lineAABB = AxisAlignedBB.getBoundingBox(prevPos[0], prevPos[1], prevPos[2], pos[0], pos[1], pos[2]);
                        if (this.frustrum.isBoundingBoxInFrustum(lineAABB)) {
                            GL11.glPushMatrix();
                            GL11.glTranslated(-cameraX, -cameraY, -cameraZ);
                            GL11.glDepthMask(false);
                            GL11.glDisable(GL11.GL_TEXTURE_2D);
                            GL11.glDisable(GL11.GL_LIGHTING);
                            GL11.glDisable(GL11.GL_CULL_FACE);
                            Tessellator.instance.startDrawing(3);
                            Tessellator.instance.setColorRGBA(0, 255, 0, 255);
                            Tessellator.instance.addVertex(prevPos[0], prevPos[1], prevPos[2]);
                            Tessellator.instance.addVertex(pos[0], pos[1], pos[2]);
                            Tessellator.instance.draw();
                            GL11.glEnable(GL11.GL_TEXTURE_2D);
                            GL11.glEnable(GL11.GL_LIGHTING);
                            GL11.glEnable(GL11.GL_CULL_FACE);
                            GL11.glDepthMask(true);
                            GL11.glPopMatrix();
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
