package org.utm.featurehelper.feature.patch;

import com.google.common.collect.Lists;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.structure.WoodlandMansion;
import net.minecraft.world.gen.structure.WoodlandMansionPieces;

import java.util.List;
import java.util.Random;

public class MansionStartPatcher extends WoodlandMansion.Start {

    private boolean isValid;

    public MansionStartPatcher(World world, Random random, int chunkX, int chunkZ)
    {
        this.create(world, random, chunkX, chunkZ);
    }

    private void create(World world, Random random, int chunkX, int chunkZ)
    {
        Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
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
        int k1 = Math.min(Math.min(k, l), Math.min(i1, j1));

        if (k1 < 60)
        {
            this.isValid = false;
        }
        else
        {
            BlockPos blockpos = new BlockPos(chunkX * 16 + 8, k1 + 1, chunkZ * 16 + 8);
            List<WoodlandMansionPieces.MansionTemplate> list = Lists.newLinkedList();
            WoodlandMansionPieces.generateMansion(world.getSaveHandler().getStructureTemplateManager(), blockpos, rotation, list, random);
            this.components.addAll(list);
            this.updateBoundingBox();
            this.isValid = true;
        }
    }

    public boolean isSizeableStructure()
    {
        return this.isValid;
    }

}
