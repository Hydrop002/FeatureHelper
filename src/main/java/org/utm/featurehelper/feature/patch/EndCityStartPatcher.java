package org.utm.featurehelper.feature.patch;

import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.structure.MapGenEndCity;
import net.minecraft.world.gen.structure.StructureEndCityPieces;

import java.util.Random;

public class EndCityStartPatcher extends MapGenEndCity.Start {

    private boolean isSizeable;

    public EndCityStartPatcher(World worldIn, Random random, int chunkX, int chunkZ)
    {
        this.create(worldIn, random, chunkX, chunkZ);
    }

    private void create(World worldIn, Random rnd, int chunkX, int chunkZ)
    {
        Random random = new Random(chunkX + chunkZ * 10387313L);
        Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
        int i = getYPosForStructure(chunkX, chunkZ, worldIn, rotation);

        if (i < 60)
        {
            this.isSizeable = false;
        }
        else
        {
            BlockPos blockpos = new BlockPos(chunkX * 16 + 8, i, chunkZ * 16 + 8);
            StructureEndCityPieces.startHouseTower(worldIn.getSaveHandler().getStructureTemplateManager(), blockpos, rotation, this.components, rnd);
            this.updateBoundingBox();
            this.isSizeable = true;
        }
    }

    public boolean isSizeableStructure()
    {
        return this.isSizeable;
    }

    private static int getYPosForStructure(int chunkX, int chunkZ, World world, Rotation rotation)
    {
        int i = 5;
        int j = 5;

        if (rotation == Rotation.CLOCKWISE_90)
        {
            i = -5;
        }
        else if (rotation == Rotation.CLOCKWISE_180)
        {
            i = -5;
            j = -5;
        }
        else if (rotation == Rotation.COUNTERCLOCKWISE_90)
        {
            j = -5;
        }

        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
        int k = chunk.getHeightValue(7, 7);
        int l = chunk.getHeightValue(7, 7 + j);
        int i1 = chunk.getHeightValue(7 + i, 7);
        int j1 = chunk.getHeightValue(7 + i, 7 + j);
        return Math.min(Math.min(k, l), Math.min(i1, j1));
    }

}
