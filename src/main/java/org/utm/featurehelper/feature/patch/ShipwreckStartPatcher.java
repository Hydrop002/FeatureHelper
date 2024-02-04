package org.utm.featurehelper.feature.patch;

import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.ShipwreckConfig;
import net.minecraft.world.gen.feature.structure.ShipwreckPieces;
import net.minecraft.world.gen.feature.structure.ShipwreckStructure;

public class ShipwreckStartPatcher extends ShipwreckStructure.Start {

    public ShipwreckStartPatcher(IWorld world, IChunkGenerator<?> generator, SharedSeedRandom random, int chunkX, int chunkZ, Biome biome) {
        ShipwreckConfig config = (ShipwreckConfig) generator.getStructureConfig(biome, Feature.SHIPWRECK);
        if (config == null) config = new ShipwreckConfig(false);
        Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
        BlockPos blockpos = new BlockPos(chunkX * 16, 90, chunkZ * 16);
        ShipwreckPieces.func_204760_a(world.getSaveHandler().getStructureTemplateManager(), blockpos, rotation, this.components, random, config);
        this.recalculateStructureSize(world);
    }

}
