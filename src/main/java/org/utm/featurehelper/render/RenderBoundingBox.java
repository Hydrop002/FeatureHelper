package org.utm.featurehelper.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class RenderBoundingBox {

	public static RenderBoundingBox instance = new RenderBoundingBox();

	public Frustrum frustrum = new Frustrum();
	
	public void render(EntityLivingBase entity, StructureBoundingBox bb, float partialTicks) {
		if (bb == null)
			return;
		double cameraX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        double cameraY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        double cameraZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
        this.frustrum.setPosition(cameraX, cameraY, cameraZ);
        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX + 1, bb.maxY + 1, bb.maxZ + 1);
        if (this.frustrum.isBoundingBoxInFrustum(aabb)) {
			GL11.glPushMatrix();
	        GL11.glTranslated(-cameraX, -cameraY, -cameraZ);
			GL11.glDepthMask(false);
	        GL11.glDisable(GL11.GL_TEXTURE_2D);
	        GL11.glDisable(GL11.GL_LIGHTING);
	        GL11.glDisable(GL11.GL_CULL_FACE);
	        RenderGlobal.drawOutlinedBoundingBox(aabb, 16777215);
	        GL11.glEnable(GL11.GL_TEXTURE_2D);
	        GL11.glEnable(GL11.GL_LIGHTING);
	        GL11.glEnable(GL11.GL_CULL_FACE);
	        GL11.glDepthMask(true);
	        GL11.glPopMatrix();
        }
	}

}
