package org.utm.featurehelper.feature.patch;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
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

    public boolean generate(World world, Random rand, int x, int y, int z) {
        while (true) {
            if (y > 3) {
                label63: {
                    if (!world.isAirBlock(x, y - 1, z)) {
                        Block block = world.getBlock(x, y - 1, z);
                        if (block == Blocks.grass || block == Blocks.dirt || block == Blocks.stone) {
                            break label63;
                        }
                    }
                    --y;
                    continue;
                }
            }
            if (y <= 3)
                return false;

            int size = this.field_150544_b;
            for (int count = 0; size >= 0 && count < 3; ++count) {
                int sizeX = size + rand.nextInt(2);
                int sizeY = size + rand.nextInt(2);
                int sizeZ = size + rand.nextInt(2);
                float radius = (sizeX + sizeY + sizeZ) * 0.333F + 0.5F;
                for (int i = x - sizeX; i <= x + sizeX; ++i) {
                    for (int k = z - sizeZ; k <= z + sizeZ; ++k) {
                        for (int j = y - sizeY; j <= y + sizeY; ++j) {
                            float offsetX = i - x;
                            float offsetZ = k - z;
                            float offsetY = j - y;
                            if (offsetX * offsetX + offsetZ * offsetZ + offsetY * offsetY <= radius * radius) {
                                world.setBlock(i, j, k, this.field_150545_a, 0, 2);
                            }
                        }
                    }
                }
                x += -(size + 1) + rand.nextInt(2 + size * 2);
                z += -(size + 1) + rand.nextInt(2 + size * 2);
                y -= rand.nextInt(2);
            }
            return true;
        }
    }

}
