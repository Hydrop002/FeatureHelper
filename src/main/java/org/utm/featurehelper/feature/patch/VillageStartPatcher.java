package org.utm.featurehelper.feature.patch;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.structure.VillagePieces;
import net.minecraft.world.gen.feature.structure.VillageStructure;

import java.util.List;

public class VillageStartPatcher extends VillageStructure.Start {

    private boolean hasMoreThanTwoComponents;

    public VillageStartPatcher(IWorld world, IChunkGenerator<?> generator, SharedSeedRandom random, int chunkX, int chunkZ, Biome biome) {
        VillageConfig config = (VillageConfig) generator.getStructureConfig(biome, Feature.VILLAGE);
        if (config == null) config = new VillageConfig(0, VillagePieces.Type.OAK);
        List<VillagePieces.PieceWeight> list = VillagePieces.getStructureVillageWeightedPieceList(random, config.field_202461_a);
        VillagePieces.Start start = new VillagePieces.Start(0, random, (chunkX << 4) + 2, (chunkZ << 4) + 2, list, config, biome);
        this.components.add(start);
        start.buildComponent(start, this.components, random);
        List<StructurePiece> list1 = start.pendingRoads;
        List<StructurePiece> list2 = start.pendingHouses;

        while(!list1.isEmpty() || !list2.isEmpty()) {
            if (list1.isEmpty()) {
                int i = random.nextInt(list2.size());
                StructurePiece structurepiece = list2.remove(i);
                structurepiece.buildComponent(start, this.components, random);
            } else {
                int j = random.nextInt(list1.size());
                StructurePiece structurepiece2 = list1.remove(j);
                structurepiece2.buildComponent(start, this.components, random);
            }
        }

        this.recalculateStructureSize(world);
        int k = 0;

        for(StructurePiece structurepiece1 : this.components) {
            if (!(structurepiece1 instanceof VillagePieces.Road)) {
                ++k;
            }
        }

        this.hasMoreThanTwoComponents = k > 2;
    }

    public boolean isSizeableStructure() {
        return this.hasMoreThanTwoComponents;
    }

    public void writeAdditional(NBTTagCompound tagCompound) {
        super.writeAdditional(tagCompound);
        tagCompound.setBoolean("Valid", this.hasMoreThanTwoComponents);
    }

    public void readAdditional(NBTTagCompound tagCompound) {
        super.readAdditional(tagCompound);
        this.hasMoreThanTwoComponents = tagCompound.getBoolean("Valid");
    }

}
