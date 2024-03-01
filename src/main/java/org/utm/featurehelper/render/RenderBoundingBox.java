package org.utm.featurehelper.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3d;

public class RenderBoundingBox {

    public static RenderBoundingBox instance = new RenderBoundingBox();

    public MutableBoundingBox bb;
    public List<MutableBoundingBox> bbList = new ArrayList<>();
    public boolean isRender = true;

    public void render(ActiveRenderInfo renderInfo, MatrixStack stack, ClippingHelper clippingHelper) {  // 基于RenderType
        if (!this.isRender || this.bb == null)
            return;
        Vector3d cameraPos = renderInfo.getPosition();
        clippingHelper.prepare(cameraPos.x, cameraPos.y, cameraPos.z);
        AxisAlignedBB aabb = new AxisAlignedBB(
                this.bb.x0 - 0.01, this.bb.y0 - 0.01, this.bb.z0 - 0.01,
                this.bb.x1 + 1.01, this.bb.y1 + 1.01, this.bb.z1 + 1.01
        );
        if (clippingHelper.isVisible(aabb)) {
            IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();
            IVertexBuilder builder = buffer.getBuffer(RenderType.lines());
            stack.pushPose();
            stack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            WorldRenderer.renderLineBox(stack, builder, aabb, 1.0F, 1.0F, 1.0F, 1.0F);
            buffer.endBatch(RenderType.lines());
            stack.popPose();
        }
    }

    public void renderList(ActiveRenderInfo renderInfo, MatrixStack stack, ClippingHelper clippingHelper) {
        if (!this.isRender || this.bbList.isEmpty())
            return;
        Vector3d cameraPos = renderInfo.getPosition();
        clippingHelper.prepare(cameraPos.x, cameraPos.y, cameraPos.z);
        Iterator<MutableBoundingBox> it = this.bbList.iterator();
        boolean start = true;
        while (it.hasNext()) {
            MutableBoundingBox bb = it.next();
            if (bb == null)
                return;
            boolean last = !it.hasNext();
            if (last && renderInfo.getEntity().tickCount % 20 < 10)
                break;
            AxisAlignedBB aabb = new AxisAlignedBB(
                    bb.x0 - 0.01, bb.y0 - 0.01, bb.z0 - 0.01,
                    bb.x1 + 1.01, bb.y1 + 1.01, bb.z1 + 1.01
            );
            if (clippingHelper.isVisible(aabb)) {
                IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();
                IVertexBuilder builder = buffer.getBuffer(RenderType.lines());
                stack.pushPose();
                stack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
                if (last)
                    WorldRenderer.renderLineBox(stack, builder, aabb, 1.0F, 1.0F, 0.0F, 1.0F);
                else if (start)
                    WorldRenderer.renderLineBox(stack, builder, aabb, 1.0F, 0.0F, 0.0F, 1.0F);
                else
                    WorldRenderer.renderLineBox(stack, builder, aabb, 0.0F, 1.0F, 0.0F, 1.0F);
                buffer.endBatch(RenderType.lines());
                stack.popPose();
            }
            start = false;
        }
    }

}
