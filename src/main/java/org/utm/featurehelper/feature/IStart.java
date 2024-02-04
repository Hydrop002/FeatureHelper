package org.utm.featurehelper.feature;

import net.minecraft.util.SharedSeedRandom;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.StructureStart;

public interface IStart {

    StructureStart getStart(World world, int chunkX, int chunkZ, SharedSeedRandom rand);

}
