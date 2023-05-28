package org.utm.featurehelper.feature.patch;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.gen.MapGenCaves;

import java.util.ArrayList;
import java.util.List;

public class CavesPatcher extends MapGenCaves {

    private double x;
    private double y;
    private double z;
    private float radius;
    private float yaw;
    private float pitch;
    private int index;
    private int length;
    private double heightFactor;
    private boolean debug;

    private float yawDiff;
    private float pitchDiff;
    private boolean isRoom;
    private int fork;
    private boolean steep;

    private List<CavesPatcher> list = new ArrayList<CavesPatcher>();

    public void generate(double x, double y, double z, float radius, float yaw, float pitch, int index, int length, double heightFactor, boolean debug) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.yaw = yaw;
        this.pitch = pitch;
        this.index = index;
        this.length = length;
        this.heightFactor = heightFactor;
        this.debug = debug;

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
        this.steep = this.rand.nextInt(6) == 0;

        if (this.debug) {
            this.list.add(this);
            return;
        }
        while (this.index < this.length) {
            this.addRoom();
        }
    }

    public void addRoom() {
        double horRadius = 1.5 + MathHelper.sin(this.index * (float) Math.PI / this.length) * this.radius;
        double verRadius = horRadius * this.heightFactor;
        float cosPitch = MathHelper.cos(this.pitch);
        float sinPitch = MathHelper.sin(this.pitch);
        this.x += MathHelper.cos(this.yaw) * cosPitch;
        this.y += sinPitch;
        this.z += MathHelper.sin(this.yaw) * cosPitch;
        if (this.steep) {
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
            this.list.remove(list.size() - 1);
            new CavesPatcher().generate(this.x, this.y, this.z, this.rand.nextFloat() * 0.5F + 0.5F, this.yaw - (float) Math.PI / 2F, this.pitch / 3F, this.index, this.length, 1, this.debug);
            new CavesPatcher().generate(this.x, this.y, this.z, this.rand.nextFloat() * 0.5F + 0.5F, this.yaw + (float) Math.PI / 2F, this.pitch / 3F, this.index, this.length, 1, this.debug);
            return;
        }
        if (this.isRoom || this.rand.nextInt(4) != 0) {
            int minX = MathHelper.floor_double(this.x - horRadius) - 1;
            int maxX = MathHelper.floor_double(this.x + horRadius) + 1;
            int minY = MathHelper.floor_double(this.y - verRadius) - 1;
            int maxY = MathHelper.floor_double(this.y + verRadius) + 1;
            int minZ = MathHelper.floor_double(this.z - horRadius) - 1;
            int maxZ = MathHelper.floor_double(this.z + horRadius) + 1;
            boolean foundWater = false;
            for (int i = minX; !foundWater && i < maxX; ++i) {
                for (int k = minZ; !foundWater && k < maxZ; ++k) {
                    for (int j = maxY + 1; !foundWater && j >= minY - 1; --j) {
                        if (j >= 0 && j < 256) {
                            Block block = this.worldObj.getBlock(i, j, k);
                            if (block == Blocks.flowing_water || block == Blocks.water)
                                foundWater = true;
                            if (j != minY - 1 && i != minX && i != maxX - 1 && k != minZ && k != maxZ - 1)
                                j = minY;
                        }
                    }
                }
            }
            if (!foundWater) {}
        }
        ++this.index;
    }

    public CavesPatcher getCurrent() {
        return list.get(list.size() - 1);
    }

    public boolean hasNext() {
        return this.index < this.length;
    }

}
