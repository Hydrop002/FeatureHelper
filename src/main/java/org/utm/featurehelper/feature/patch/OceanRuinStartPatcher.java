package org.utm.featurehelper.feature.patch;

import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.OceanRuinConfig;
import net.minecraft.world.gen.feature.structure.OceanRuinPieces;
import net.minecraft.world.gen.feature.structure.OceanRuinStructure;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class OceanRuinStartPatcher extends OceanRuinStructure.Start {

    public OceanRuinStartPatcher(IWorld world, IChunkGenerator<?> generator, SharedSeedRandom random, int chunkX, int chunkZ, Biome biome) {
        OceanRuinConfig config = (OceanRuinConfig) generator.getStructureConfig(biome, Feature.OCEAN_RUIN);
        if (config == null) config = new OceanRuinConfig(OceanRuinStructure.Type.COLD, 0.3F, 0.9F);
        int i = chunkX * 16;
        int j = chunkZ * 16;
        BlockPos blockpos = new BlockPos(i, 90, j);
        Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
        TemplateManager templatemanager = world.getSaveHandler().getStructureTemplateManager();
        OceanRuinPieces.func_204041_a(templatemanager, blockpos, rotation, this.components, random, config);
        this.recalculateStructureSize(world);
    }

}
