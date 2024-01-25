package org.utm.featurehelper.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class RenderBoundingBox {

    public static RenderBoundingBox instance = new RenderBoundingBox();

    public ICamera camera;

    public StructureBoundingBox bb;
    public List<StructureBoundingBox> bbList = new ArrayList<>();
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
        AxisAlignedBB aabb = new AxisAlignedBB(
                this.bb.minX - 0.01, this.bb.minY - 0.01, this.bb.minZ - 0.01,
                this.bb.maxX + 1.01, this.bb.maxY + 1.01, this.bb.maxZ + 1.01
        );
        if (this.camera.isBoundingBoxInFrustum(aabb)) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-cameraX, -cameraY, -cameraZ);
            GlStateManager.depthMask(false);
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.disableBlend();
            RenderGlobal.drawBoundingBox(
                    aabb.minX, aabb.minY, aabb.minZ,
                    aabb.maxX, aabb.maxY, aabb.maxZ,
                    1.0F, 1.0F, 1.0F, 1.0F
            );
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
            StructureBoundingBox bb = it.next();
            if (bb == null)
                return;
            boolean last = !it.hasNext();
            if (last && entity.ticksExisted % 20 < 10)
                break;
            AxisAlignedBB aabb = new AxisAlignedBB(
                    bb.minX - 0.01, bb.minY - 0.01, bb.minZ - 0.01,
                    bb.maxX + 1.01, bb.maxY + 1.01, bb.maxZ + 1.01
            );
            if (this.camera.isBoundingBoxInFrustum(aabb)) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(-cameraX, -cameraY, -cameraZ);
                GlStateManager.depthMask(false);
                GlStateManager.disableTexture2D();
                GlStateManager.disableLighting();
                GlStateManager.disableCull();
                GlStateManager.disableBlend();
                if (last)
                    RenderGlobal.drawBoundingBox(
                            aabb.minX, aabb.minY, aabb.minZ,
                            aabb.maxX, aabb.maxY, aabb.maxZ,
                            1.0F, 1.0F, 0.0F, 1.0F
                    );
                else if (start)
                    RenderGlobal.drawBoundingBox(
                            aabb.minX, aabb.minY, aabb.minZ,
                            aabb.maxX, aabb.maxY, aabb.maxZ,
                            1.0F, 0.0F, 0.0F, 1.0F
                    );
                else
                    RenderGlobal.drawBoundingBox(
                            aabb.minX, aabb.minY, aabb.minZ,
                            aabb.maxX, aabb.maxY, aabb.maxZ,
                            0.0F, 1.0F, 0.0F, 1.0F
                    );
                GlStateManager.enableTexture2D();
                GlStateManager.enableCull();
                GlStateManager.disableBlend();
                GlStateManager.depthMask(true);
                GlStateManager.popMatrix();
            }
            start = false;
        }
    }

}
