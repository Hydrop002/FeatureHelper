package org.utm.featurehelper.feature.patch;

import com.google.common.collect.Lists;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.WoodlandMansionPieces;
import net.minecraft.world.gen.feature.structure.WoodlandMansionStructure;

import java.util.List;

public class WoodlandMansionStartPatcher extends WoodlandMansionStructure.Start {

    private boolean isValid;

    public WoodlandMansionStartPatcher(IWorld world, SharedSeedRandom random, int chunkX, int chunkZ, Biome biome) {
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

        Chunk chunk = ((World) world).getChunk(chunkX, chunkZ);
        int k = chunk.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, 7, 7);
        int l = chunk.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, 7, 7 + j);
        int i1 = chunk.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, 7 + i, 7);
        int j1 = chunk.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, 7 + i, 7 + j);
        int k1 = Math.min(Math.min(k, l), Math.min(i1, j1));
        if (k1 < 60) {
            this.isValid = false;
        } else {
            BlockPos blockpos = new BlockPos(chunkX * 16 + 8, k1 + 1, chunkZ * 16 + 8);
            List<WoodlandMansionPieces.MansionTemplate> list = Lists.newLinkedList();
            WoodlandMansionPieces.generateMansion(world.getSaveHandler().getStructureTemplateManager(), blockpos, rotation, list, random);
            this.components.addAll(list);
            this.recalculateStructureSize(world);
            this.isValid = true;
        }
    }

    public boolean isSizeableStructure() {
        return this.isValid;
    }

    public void writeAdditional(net.minecraft.nbt.NBTTagCompound tag) {
        super.writeAdditional(tag);
        tag.setBoolean("Valid", this.isValid);
    }
    public void readAdditional(net.minecraft.nbt.NBTTagCompound tag) {
        super.readAdditional(tag);
        this.isValid = tag.hasKey("Valid") && tag.getBoolean("Valid");
    }

}
