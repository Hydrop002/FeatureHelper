package org.utm.featurehelper.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class RenderBoundingBox {

    public static RenderBoundingBox instance = new RenderBoundingBox();

    public ICamera camera;

    public StructureBoundingBox bb;
    public List<StructureBoundingBox> bbList = new ArrayList<StructureBoundingBox>();
    public boolean isRender = true;

    public void render(EntityLivingBase entity, float partialTicks) {
        if (!this.isRender || this.bb == null)
            return;
        if (this.camera == null)
            this.camera = new Frustum();
        double cameraX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        double cameraY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        double cameraZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
        this.camera.setPosition(cameraX, cameraY, cameraZ);
        AxisAlignedBB aabb = new AxisAlignedBB(this.bb.minX, this.bb.minY, this.bb.minZ, this.bb.maxX + 1, this.bb.maxY + 1, this.bb.maxZ + 1);
        if (this.camera.isBoundingBoxInFrustum(aabb)) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-cameraX, -cameraY, -cameraZ);
            GlStateManager.depthMask(false);
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.disableBlend();
            RenderGlobal.drawOutlinedBoundingBox(aabb, 255, 255, 255, 255);
            GlStateManager.enableTexture2D();
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            GlStateManager.popMatrix();
        }
    }

    public void renderList(EntityLivingBase entity, float partialTicks) {
        if (!this.isRender || this.bbList.isEmpty())
            return;
        double cameraX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        double cameraY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        double cameraZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
        this.camera.setPosition(cameraX, cameraY, cameraZ);
        Iterator<StructureBoundingBox> it = this.bbList.iterator();
        boolean start = true;
        while (it.hasNext()) {
            StructureBoundingBox bb;
            bb = it.next();
            if (bb == null)
                return;
            if (it.hasNext() || entity.ticksExisted % 20 < 10) {
                AxisAlignedBB aabb = new AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.maxX + 1, bb.maxY + 1, bb.maxZ + 1);
                if (this.camera.isBoundingBoxInFrustum(aabb)) {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(-cameraX, -cameraY, -cameraZ);
                    GlStateManager.depthMask(false);
                    GlStateManager.disableTexture2D();
                    GlStateManager.disableLighting();
                    GlStateManager.disableCull();
                    GlStateManager.disableBlend();
                    if (start)
                        RenderGlobal.drawOutlinedBoundingBox(aabb, 255, 0, 0, 255);
                    else
                        RenderGlobal.drawOutlinedBoundingBox(aabb, 0, 255, 0, 255);
                    GlStateManager.enableTexture2D();
                    GlStateManager.enableCull();
                    GlStateManager.disableBlend();
                    GlStateManager.depthMask(true);
                    GlStateManager.popMatrix();
                }
            }
            start = false;
        }
    }

}
