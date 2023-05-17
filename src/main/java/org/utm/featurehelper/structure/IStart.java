package org.utm.featurehelper.structure;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureStart;

public interface IStart {

    public StructureStart getStart(World world, int chunkX, int chunkZ, Random rand);

}
