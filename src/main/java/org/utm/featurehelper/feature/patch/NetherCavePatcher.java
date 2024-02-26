package org.utm.featurehelper.feature.patch;

import com.google.common.collect.ImmutableSet;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.utm.featurehelper.command.CommandCarve;

public class NetherCavePatcher extends CavePatcher {

    public NetherCavePatcher() {
        this.terrainBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.NETHERRACK);
        this.terrainFluids = ImmutableSet.of(Fluids.LAVA, Fluids.WATER);
    }

    public boolean updateAndCarve() {
        this.horRadius = 1.5 + MathHelper.sin(this.depth * (float) Math.PI / this.maxDepth) * this.radius;
        this.verRadius = this.horRadius * this.heightFactor;
        float cosPitch = MathHelper.cos(this.pitch);
        float sinPitch = MathHelper.sin(this.pitch);
        this.x += MathHelper.cos(this.yaw) * cosPitch;
        this.y += sinPitch;
        this.z += MathHelper.sin(this.yaw) * cosPitch;
        this.pitch *= this.steep ? 0.92F : 0.7F;
        this.pitch += this.pitchDiff * 0.1F;
        this.yaw += this.yawDiff * 0.1F;
        this.pitchDiff *= 0.9F;
        this.yawDiff *= 0.75F;
        this.pitchDiff += (this.rand.nextFloat() - this.rand.nextFloat()) * this.rand.nextFloat() * 2;
        this.yawDiff += (this.rand.nextFloat() - this.rand.nextFloat()) * this.rand.nextFloat() * 4;
        if (this.depth == this.forkDepth && this.radius > 1) {
            if (this.debug) {
                CommandCarve.removeCarver();
                CommandCarve.addPos(null);
            }
            new NetherCavePatcher().carveTunnel(this.world, this.rand, this.x, this.y, this.z, this.rand.nextFloat() * 0.5F + 0.5F, this.yaw - (float) Math.PI / 2F, this.pitch / 3F, this.depth, this.maxDepth, 1, this.debug);
            new NetherCavePatcher().carveTunnel(this.world, this.rand, this.x, this.y, this.z, this.rand.nextFloat() * 0.5F + 0.5F, this.yaw + (float) Math.PI / 2F, this.pitch / 3F, this.depth, this.maxDepth, 1, this.debug);
            return false;
        }
        if (this.rand.nextInt(4) != 0) {
            this.carve();
        }
        ++this.depth;
        if (this.debug) {
            CommandCarve.addPos(this.getPos());
            if (this.depth >= this.maxDepth) {
                CommandCarve.removeCarver();
                CommandCarve.addPos(null);
            }
            CommandCarve.sendMessage();
        }
        return true;
    }

    protected void carve() {
        int minX = MathHelper.floor(this.x - this.horRadius) - 1;
        int maxX = MathHelper.floor(this.x + this.horRadius) + 1;
        int minY = MathHelper.floor(this.y - this.verRadius) - 1;
        int maxY = MathHelper.floor(this.y + this.verRadius) + 1;
        int minZ = MathHelper.floor(this.z - this.horRadius) - 1;
        int maxZ = MathHelper.floor(this.z + this.horRadius) + 1;
        if (!this.doesAreaHaveFluids(this.world, 0, 0, minX, maxX, minY, maxY, minZ, maxZ)) {
            for(int i = minX; i < maxX; ++i) {
                double offsetRatioX = (i + 0.5 - this.x) / this.horRadius;
                for (int k = minZ; k < maxZ; ++k) {
                    double offsetRatioZ = (k + 0.5 - this.z) / this.horRadius;
                    for (int j = maxY; j > minY; --j) {
                        double offsetRatioY = (j - 0.5 - this.y) / this.verRadius;
                        if (offsetRatioY > -0.7 && offsetRatioX * offsetRatioX + offsetRatioY * offsetRatioY + offsetRatioZ * offsetRatioZ < 1) {
                            BlockPos blockPos = new BlockPos(i, j, k);
                            if (this.isTargetAllowed(this.world.getBlockState(blockPos))) {
                                if (j <= 31) {
                                    this.world.setBlockState(blockPos, LAVA_FLUID.getBlockState(), 2);
                                } else {
                                    this.world.setBlockState(blockPos, DEFAULT_CAVE_AIR, 2);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
