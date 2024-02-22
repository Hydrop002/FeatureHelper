package org.utm.featurehelper.feature.patch;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.BlockBlobConfig;
import net.minecraft.world.gen.feature.BlockBlobFeature;

import java.util.Random;

public class BlockBlobPatcher extends BlockBlobFeature {

    public boolean func_212245_a(IWorld world, IChunkGenerator<? extends IChunkGenSettings> generator, Random rand, BlockPos pos, BlockBlobConfig config) {
        while (true) {
            label50: {
                if (pos.getY() > 3) {
                    if (world.isAirBlock(pos.down())) {
                        break label50;
                    }
                    Block block = world.getBlockState(pos.down()).getBlock();
                    if (block != Blocks.GRASS_BLOCK && !Block.isDirt(block) && !Block.isRock(block)) {
                        break label50;
                    }
                }
                if (pos.getY() <= 3) {
                    return false;
                }

                int size = config.field_202464_b;
                for (int count = 0; size >= 0 && count < 3; ++count) {
                    int sizeX = size + rand.nextInt(2);
                    int sizeY = size + rand.nextInt(2);
                    int sizeZ = size + rand.nextInt(2);
                    float radius = (float) (sizeX + sizeY + sizeZ) * 0.333F + 0.5F;
                    for (BlockPos near : BlockPos.getAllInBox(pos.add(-sizeX, -sizeY, -sizeZ), pos.add(sizeX, sizeY, sizeZ))) {
                        if (near.distanceSq(pos) <= (double)(radius * radius)) {
                            world.setBlockState(near, config.block.getDefaultState(), 2);
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
