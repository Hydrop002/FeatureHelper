package org.utm.featurehelper.feature.patch;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;

import java.util.Random;

public class BlockBlobPatcher extends WorldGenBlockBlob {

    private final Block block;
    private final int startRadius;

    public BlockBlobPatcher(Block blockIn, int startRadiusIn) {
        super(blockIn, startRadiusIn);
        this.block = blockIn;
        this.startRadius = startRadiusIn;
    }

    public boolean generate(World world, Random rand, BlockPos pos) {
        while (true) {
            label50: {
                if (pos.getY() > 3) {
                    if (world.isAirBlock(pos.down())) {
                        break label50;
                    }
                    Block block = world.getBlockState(pos.down()).getBlock();
                    if (block != Blocks.GRASS && block != Blocks.DIRT && block != Blocks.STONE) {
                        break label50;
                    }
                }
                if (pos.getY() <= 3) {
                    return false;
                }

                int size = this.startRadius;
                for (int count = 0; size >= 0 && count < 3; ++count) {
                    int sizeX = size + rand.nextInt(2);
                    int sizeY = size + rand.nextInt(2);
                    int sizeZ = size + rand.nextInt(2);
                    float radius = (float) (sizeX + sizeY + sizeZ) * 0.333F + 0.5F;
                    for (BlockPos near : BlockPos.getAllInBox(pos.add(-sizeX, -sizeY, -sizeZ), pos.add(sizeX, sizeY, sizeZ))) {
                        if (near.distanceSq(pos) <= (double)(radius * radius)) {
                            world.setBlockState(near, this.block.getDefaultState(), 2);
                        }
                    }
                    pos = pos.add(-(size + 1) + rand.nextInt(2 + size * 2), -rand.nextInt(2), -(size + 1) + rand.nextInt(2 + size * 2));
                }
                return true;
            }
            pos = pos.down();
        }
    }

}
