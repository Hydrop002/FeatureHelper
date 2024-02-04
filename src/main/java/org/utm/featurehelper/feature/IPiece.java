package org.utm.featurehelper.feature;

import net.minecraft.util.SharedSeedRandom;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.StructurePiece;

public interface IPiece {

    StructurePiece getPiece(World world, int x, int y, int z, SharedSeedRandom rand);

}
