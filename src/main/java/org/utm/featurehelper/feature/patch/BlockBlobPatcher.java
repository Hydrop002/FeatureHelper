package org.utm.featurehelper.feature.patch;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;

import java.util.Random;

public class BlockBlobPatcher extends WorldGenBlockBlob {

    private Block field_150545_a;
    private int field_150544_b;

    public BlockBlobPatcher(Block block, int blobSize) {
        super(block, blobSize);
        this.field_150545_a = block;
        this.field_150544_b = blobSize;
    }

    public boolean generate(World world, Random rand, BlockPos pos) {
        while (true) {
            label0: {
                if (pos.getY() > 3) {
                    if (world.isAirBlock(pos.down())) {
                        break label0;
                    }
                    Block block = world.getBlockState(pos.down()).getBlock();
                    if (block != Blocks.grass && block != Blocks.dirt && block != Blocks.stone) {
                        break label0;
                    }
                }
                if (pos.getY() <= 3) {
                    return false;
                }

                int size = this.field_150544_b;
                for (int count = 0; size >= 0 && count < 3; ++count) {
                    int sizeX = size + rand.nextInt(2);
                    int sizeY = size + rand.nextInt(2);
                    int sizeZ = size + rand.nextInt(2);
                    float radius = (float)(sizeX + sizeY + sizeZ) * 0.333F + 0.5F;
                    for (BlockPos near : BlockPos.getAllInBox(pos.add(-sizeX, -sizeY, -sizeZ), pos.add(sizeX, sizeY, sizeZ))) {
                        if (near.distanceSq(pos) <= (double)(radius * radius)) {
                            world.setBlockState(near, this.field_150545_a.getDefaultState(), 2);
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
