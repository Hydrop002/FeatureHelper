package org.utm.featurehelper.feature.patch;

import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.EndCityPieces;
import net.minecraft.world.gen.feature.structure.EndCityStructure;

import java.util.Random;

public class EndCityStartPatcher extends EndCityStructure.Start {

    private boolean isSizeable;

    public EndCityStartPatcher(World world, SharedSeedRandom random, int chunkX, int chunkZ, Biome biome) {
        Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
        int i = getYPosForStructure(chunkX, chunkZ, world);
        if (i < 60) {
            this.isSizeable = false;
        } else {
            BlockPos blockpos = new BlockPos(chunkX * 16 + 8, i, chunkZ * 16 + 8);
            EndCityPieces.startHouseTower(world.getSaveHandler().getStructureTemplateManager(), blockpos, rotation, this.components, random);
            this.recalculateStructureSize(world);
            this.isSizeable = true;
        }
    }

    public boolean isSizeableStructure() {
        return this.isSizeable;
    }

    public void writeAdditional(net.minecraft.nbt.NBTTagCompound tag) {
        super.writeAdditional(tag);
        tag.setBoolean("Valid", this.isSizeable);
    }
    public void readAdditional(net.minecraft.nbt.NBTTagCompound tag) {
        super.readAdditional(tag);
        this.isSizeable = tag.hasKey("Valid") && tag.getBoolean("Valid");
    }

    private static int getYPosForStructure(int chunkX, int chunkZ, World world) {
        Random random = new Random(chunkX + chunkZ * 10387313L);
        Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];

        int i = 5;
        int j = 5;
        if (rotation == Rotation.CLOCKWISE_90) {
            i = -5;
        } else if (rotation == Rotation.CLOCKWISE_180) {
            i = -5;
            j = -5;
        } else if (rotation == Rotation.COUNTERCLOCKWISE_90) {
            j = -5;
        }

        Chunk chunk = world.getChunk(chunkX, chunkZ);
        int k = chunk.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, 7, 7);
        int l = chunk.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, 7, 7 + j);
        int i1 = chunk.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, 7 + i, 7);
        int j1 = chunk.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, 7 + i, 7 + j);
        return Math.min(Math.min(k, l), Math.min(i1, j1));
    }

}
