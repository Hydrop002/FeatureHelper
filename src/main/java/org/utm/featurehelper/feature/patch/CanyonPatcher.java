package org.utm.featurehelper.feature.patch;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.carver.CanyonWorldCarver;
import org.utm.featurehelper.command.CommandCarve;
import org.utm.featurehelper.feature.ICarverPatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CanyonPatcher extends CanyonWorldCarver implements ICarverPatcher {

    protected final float[] field_202536_i = new float[1024];

    protected IWorld world;
    protected Random rand;
    protected double x;
    protected double y;
    protected double z;
    protected float radius;
    protected float yaw;
    protected float pitch;
    protected int depth;
    protected int maxDepth;
    protected double heightFactor;
    protected boolean debug;

    protected float yawDiff;
    protected float pitchDiff;
    protected double horRadius;
    protected double verRadius;

    public void carveTunnel(IWorld world, Random rand, double x, double y, double z, float radius, float yaw, float pitch, int depth, int maxDepth, double heightFactor, boolean debug) {
        this.world = world;
        this.rand = rand;
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.yaw = yaw;
        this.pitch = pitch;
        this.depth = depth;
        this.maxDepth = maxDepth;
        this.heightFactor = heightFactor;
        this.debug = debug;

        this.yawDiff = 0;
        this.pitchDiff = 0;
        float r = 1.0F;
        for (int i = 0; i < 256; ++i) {
            if (i == 0 || this.rand.nextInt(3) == 0) {
                r = 1.0F + this.rand.nextFloat() * this.rand.nextFloat();
            }
            this.field_202536_i[i] = r * r;
        }

        if (this.debug) {
            CommandCarve.addCarver(this);
            List<Vec3d> subList = new ArrayList<>();
            subList.add(this.getPos());
            CommandCarve.addPosList(subList);
            this.updateAndCarve();
            return;
        }
        while (this.depth < this.maxDepth) {
            if (!this.updateAndCarve())
                break;
        }
    }

    public boolean updateAndCarve() {
        this.horRadius = 1.5 + MathHelper.sin(this.depth * (float) Math.PI / this.maxDepth) * this.radius;
        this.verRadius = this.horRadius * this.heightFactor;
        this.horRadius *= this.rand.nextFloat() * 0.25 + 0.75;
        this.verRadius *= this.rand.nextFloat() * 0.25 + 0.75;
        float cosPitch = MathHelper.cos(this.pitch);
        float sinPitch = MathHelper.sin(this.pitch);
        this.x += MathHelper.cos(this.yaw) * cosPitch;
        this.y += sinPitch;
        this.z += MathHelper.sin(this.yaw) * cosPitch;
        this.pitch *= 0.7F;
        this.pitch += this.pitchDiff * 0.05F;
        this.yaw += this.yawDiff * 0.05F;
        this.pitchDiff *= 0.8F;
        this.yawDiff *= 0.5F;
        this.pitchDiff += (this.rand.nextFloat() - this.rand.nextFloat()) * this.rand.nextFloat() * 2;
        this.yawDiff += (this.rand.nextFloat() - this.rand.nextFloat()) * this.rand.nextFloat() * 4;
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
        if (!this.doesAreaHaveFluids(world, 0, 0, minX, maxX, minY, maxY, minZ, maxZ)) {
            BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos blockPosUp = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos blockPosDown = new BlockPos.MutableBlockPos();
            for(int i = minX; i < maxX; ++i) {
                double offsetRatioX = (i + 0.5 - this.x) / this.horRadius;
                for (int k = minZ; k < maxZ; ++k) {
                    double offsetRatioZ = (k + 0.5 - this.z) / this.horRadius;
                    if (offsetRatioX * offsetRatioX + offsetRatioZ * offsetRatioZ < 1) {
                        boolean foundTop = false;
                        for (int j = maxY; j > minY; --j) {
                            double offsetRatioY = (j - 0.5 - this.y) / this.verRadius;
                            if ((offsetRatioX * offsetRatioX + offsetRatioZ * offsetRatioZ) * this.field_202536_i[j - 1] + offsetRatioY * offsetRatioY / 6 < 1) {
                                blockPos.setPos(i, j, k);
                                blockPosUp.setPos(blockPos).move(EnumFacing.UP);
                                blockPosDown.setPos(blockPos).move(EnumFacing.DOWN);
                                IBlockState blockState = this.world.getBlockState(blockPos);
                                IBlockState blockStateUp = this.world.getBlockState(blockPosUp);
                                if (blockState.getBlock() == Blocks.GRASS_BLOCK || blockState.getBlock() == Blocks.MYCELIUM) {
                                    foundTop = true;
                                }
                                if (this.isTargetSafeFromFalling(blockState, blockStateUp)) {
                                    if (j < 11) {
                                        this.world.setBlockState(blockPos, LAVA_FLUID.getBlockState(), 2);
                                    } else {
                                        this.world.setBlockState(blockPos, DEFAULT_CAVE_AIR, 2);
                                        if (foundTop && this.world.getBlockState(blockPosDown).getBlock() == Blocks.DIRT) {
                                            IBlockState top = this.world.getBiome(blockPos).getSurfaceBuilderConfig().getTop();
                                            this.world.setBlockState(blockPosDown, top, 2);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public Vec3d getPos() {
        return new Vec3d(this.x, this.y, this.z);
    }

}
