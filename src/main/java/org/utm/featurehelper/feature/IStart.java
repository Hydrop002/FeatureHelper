package org.utm.featurehelper.feature;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureStart;

public interface IStart {

    StructureStart getStart(World world, int chunkX, int chunkZ, Random rand);

}
