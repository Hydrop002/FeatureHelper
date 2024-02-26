package org.utm.featurehelper.feature.patch;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class UnderwaterCanyonPatcher extends CanyonPatcher {

    public UnderwaterCanyonPatcher() {
        this.terrainBlocks = ImmutableSet.of(
                Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE,
                Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK,
                Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA,
                Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA,
                Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA,
                Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA,
                Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.SAND, Blocks.GRAVEL,
                Blocks.WATER, Blocks.LAVA, Blocks.OBSIDIAN, Blocks.AIR, Blocks.CAVE_AIR
        );
    }

    protected void carve() {
        int minX = MathHelper.floor(this.x - this.horRadius) - 1;
        int maxX = MathHelper.floor(this.x + this.horRadius) + 1;
        int minY = MathHelper.floor(this.y - this.verRadius) - 1;
        int maxY = MathHelper.floor(this.y + this.verRadius) + 1;
        int minZ = MathHelper.floor(this.z - this.horRadius) - 1;
        int maxZ = MathHelper.floor(this.z + this.horRadius) + 1;
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        for(int i = minX; i < maxX; ++i) {
            double offsetRatioX = (i + 0.5 - this.x) / this.horRadius;
            for (int k = minZ; k < maxZ; ++k) {
                double offsetRatioZ = (k + 0.5 - this.z) / this.horRadius;
                if (offsetRatioX * offsetRatioX + offsetRatioZ * offsetRatioZ < 1) {
                    for (int j = maxY; j > minY; --j) {
                        double offsetRatioY = (j - 0.5 - this.y) / this.verRadius;
                        if ((offsetRatioX * offsetRatioX + offsetRatioZ * offsetRatioZ) * this.field_202536_i[j - 1] + offsetRatioY * offsetRatioY / 6 < 1 && j < this.world.getSeaLevel()) {
                            blockPos.setPos(i, j, k);
                            IBlockState blockState = this.world.getBlockState(blockPos);
                            if (this.isTargetAllowed(blockState)) {
                                if (j == 10) {
                                    if (this.rand.nextFloat() < 0.25) {
                                        this.world.setBlockState(blockPos, Blocks.MAGMA_BLOCK.getDefaultState(), 2);
                                        this.world.getPendingBlockTicks().scheduleTick(blockPos, Blocks.MAGMA_BLOCK, 0);
                                    } else {
                                        this.world.setBlockState(blockPos, Blocks.OBSIDIAN.getDefaultState(), 2);
                                    }
                                } else if (j < 10) {
                                    this.world.setBlockState(blockPos, Blocks.LAVA.getDefaultState(), 2);
                                } else {
                                    boolean foundAir = false;
                                    for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL) {
                                        IBlockState blockStateNear = this.world.getBlockState(blockPos.setPos(i + facing.getXOffset(), j, k + facing.getZOffset()));
                                        if (blockStateNear.isAir()) {
                                            this.world.setBlockState(blockPos, WATER_FLUID.getBlockState(), 2);
                                            this.world.getPendingFluidTicks().scheduleTick(blockPos, WATER_FLUID.getFluid(), 0);
                                            foundAir = true;
                                            break;
                                        }
                                    }
                                    blockPos.setPos(i, j, k);
                                    if (!foundAir) {
                                        this.world.setBlockState(blockPos, WATER_FLUID.getBlockState(), 2);
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
