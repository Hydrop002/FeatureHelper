package org.utm.featurehelper.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class RenderTrail {

    public static RenderTrail instance = new RenderTrail();

    public List<List<Vector3d>> posList = new ArrayList<>();
    public boolean isRender = true;

    public void renderList(ActiveRenderInfo renderInfo, MatrixStack stack, ClippingHelper clippingHelper) {  // 基于Tessellator
        if (!this.isRender || this.posList.isEmpty())
            return;
        Vector3d cameraPos = renderInfo.getPosition();
        clippingHelper.prepare(cameraPos.x, cameraPos.y, cameraPos.z);
        float viewX = renderInfo.getXRot();
        float viewY = renderInfo.getYRot();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuilder();
        Iterator<List<Vector3d>> it = this.posList.iterator();
        boolean start = true;
        Vector3d prevPos = null;
        Vector3d forkPos = null;
        while (it.hasNext()) {
            Iterator<Vector3d> tunnel = it.next().iterator();
            while (tunnel.hasNext()) {
                Vector3d pos = tunnel.next();
                if (pos == null) {
                    if (forkPos == null) forkPos = prevPos;
                    break;
                }
                if (tunnel.hasNext() || renderInfo.getEntity().tickCount % 20 < 10) {
                    AxisAlignedBB aabb = new AxisAlignedBB(pos.x, pos.y, pos.z, pos.x, pos.y, pos.z);
                    if (clippingHelper.isVisible(aabb)) {
                        RenderSystem.pushMatrix();
                        RenderSystem.multMatrix(stack.last().pose());
                        RenderSystem.translated(pos.x - cameraPos.x, pos.y - cameraPos.y, pos.z - cameraPos.z);
                        RenderSystem.rotatef(180.0F - viewY, 0.0F, 1.0F, 0.0F);
                        RenderSystem.rotatef(-viewX, 1.0F, 0.0F, 0.0F);
                        RenderSystem.disableDepthTest();
                        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
                        if (start) {
                            builder.vertex(-0.1, 0.0, 0.0).color(255, 0, 0, 255).endVertex();
                            builder.vertex(0.0, -0.1, 0.0).color(255, 0, 0, 255).endVertex();
                            builder.vertex(0.1, 0.0, 0.0).color(255, 0, 0, 255).endVertex();
                            builder.vertex(0.0, 0.1, 0.0).color(255, 0, 0, 255).endVertex();
                        } else {
                            builder.vertex(-0.1, 0.0, 0.0).color(0, 255, 0, 255).endVertex();
                            builder.vertex(0.0, -0.1, 0.0).color(0, 255, 0, 255).endVertex();
                            builder.vertex(0.1, 0.0, 0.0).color(0, 255, 0, 255).endVertex();
                            builder.vertex(0.0, 0.1, 0.0).color(0, 255, 0, 255).endVertex();
                        }
                        tessellator.end();
                        RenderSystem.enableDepthTest();
                        RenderSystem.popMatrix();
                    }
                    if (prevPos != null) {
                        AxisAlignedBB lineAABB = new AxisAlignedBB(prevPos.x, prevPos.y, prevPos.z, pos.x, pos.y, pos.z);
                        if (clippingHelper.isVisible(lineAABB)) {
                            RenderSystem.pushMatrix();
                            RenderSystem.multMatrix(stack.last().pose());
                            RenderSystem.translated(-cameraPos.x, -cameraPos.y, -cameraPos.z);
                            builder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
                            builder.vertex(prevPos.x, prevPos.y, prevPos.z).color(0, 255, 0, 255).endVertex();
                            builder.vertex(pos.x, pos.y, pos.z).color(0, 255, 0, 255).endVertex();
                            tessellator.end();
                            RenderSystem.popMatrix();
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
