package org.utm.featurehelper.feature.patch;

import net.minecraft.util.MathHelper;
import net.minecraft.world.gen.MapGenCaves;

public class CavesPatcher extends MapGenCaves {

    private int chunkX;
    private int chunkZ;
    private double x;
    private double y;
    private double z;
    private float radius;
    private float yaw;
    private float pitch;
    private int index;
    private int length;
    private double heightFactor;

    private double centerX;
    private double centerZ;
    private float yawDiff;
    private float pitchDiff;
    private boolean isRoom;
    private int fork;
    private boolean flag;

    public void generate(int chunkX, int chunkZ, double x, double y, double z, float radius, float yaw, float pitch, int index, int length, double heightFactor) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.yaw = yaw;
        this.pitch = pitch;
        this.index = index;
        this.length = length;
        this.heightFactor = heightFactor;

        this.centerX = this.chunkX * 16 + 8;
        this.centerZ = this.chunkZ * 16 + 8;
        this.yawDiff = 0;
        this.pitchDiff = 0;
        if (this.length <= 0) {
            int maxLength = this.range * 16 - 16;
            this.length = maxLength - this.rand.nextInt(maxLength / 4);
        }
        this.isRoom = false;
        if (this.index == -1) {
            this.index = this.length / 2;
            this.isRoom = true;
        }
        this.fork = this.rand.nextInt(this.length / 2) + this.length / 4;
        this.flag = this.rand.nextInt(6) == 0;
        while (this.index < this.length) {
            this.generateStep();
        }
    }

    public void generateStep() {
        double horRadius = 1.5 + MathHelper.sin(this.index * (float) Math.PI / this.length) * this.radius;
        double verRadius = horRadius * this.heightFactor;
        float cosPitch = MathHelper.cos(this.pitch);
        float sinPitch = MathHelper.sin(this.pitch);
        this.x += MathHelper.cos(this.yaw) * cosPitch;
        this.y += sinPitch;
        this.z += MathHelper.sin(this.yaw) * cosPitch;
        if (this.flag) {
            this.pitch *= 0.92;
        } else {
            this.pitch *= 0.7;
        }
        this.pitch += this.pitchDiff * 0.1;
        this.yaw += this.yawDiff * 0.1;
        this.pitchDiff *= 0.9;
        this.yawDiff *= 0.75;
        this.pitchDiff += (this.rand.nextFloat() - this.rand.nextFloat()) * this.rand.nextFloat() * 2;
        this.yawDiff += (this.rand.nextFloat() - this.rand.nextFloat()) * this.rand.nextFloat() * 4;
        if (!this.isRoom && this.index == this.fork && this.radius > 1 && this.length > 0) {
            this.generate(this.chunkX, this.chunkZ, this.x, this.y, this.z, this.rand.nextFloat() * 0.5F + 0.5F, this.yaw - (float) Math.PI / 2F, this.pitch / 3F, this.index, this.length, 1);
            this.generate(this.chunkX, this.chunkZ, this.x, this.y, this.z, this.rand.nextFloat() * 0.5F + 0.5F, this.yaw + (float) Math.PI / 2F, this.pitch / 3F, this.index, this.length, 1);
            return;
        }
        ++this.index;
    }

}
