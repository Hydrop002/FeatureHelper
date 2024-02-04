package org.utm.featurehelper.feature.patch;

import net.minecraft.util.SharedSeedRandom;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftPieces;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.feature.structure.StructurePiece;

public class MineshaftStartPatcher extends MineshaftStructure.Start {

    private MineshaftStructure.Type field_202507_c;

    public MineshaftStartPatcher(IWorld world, IChunkGenerator<?> generator, SharedSeedRandom random, int chunkX, int chunkZ, Biome biome) {
        MineshaftConfig config = (MineshaftConfig) generator.getStructureConfig(biome, Feature.MINESHAFT);
        // world.getChunkProvider().getChunkGenerator()可能提供空结构配置
        if (config == null) config = new MineshaftConfig(0.004, MineshaftStructure.Type.NORMAL);
        this.field_202507_c = config.type;
        MineshaftPieces.Room room = new MineshaftPieces.Room(0, random, (chunkX << 4) + 2, (chunkZ << 4) + 2, this.field_202507_c);
        this.components.add(room);
        room.buildComponent(room, this.components, random);
        this.recalculateStructureSize(world);
        if (config.type == MineshaftStructure.Type.MESA) {
            int i = -5;
            int j = world.getSeaLevel() - this.boundingBox.maxY + this.boundingBox.getYSize() / 2 - -5;
            this.boundingBox.offset(0, j, 0);

            for(StructurePiece structurepiece : this.components) {
                structurepiece.offset(0, j, 0);
            }
        } else {
            this.markAvailableHeight(world, random, 10);
        }
    }

}
