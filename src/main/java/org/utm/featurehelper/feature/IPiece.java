package org.utm.featurehelper.feature;

import net.minecraft.util.SharedSeedRandom;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.server.ServerWorld;

public interface IPiece {

    StructurePiece getPiece(ServerWorld world, int x, int y, int z, SharedSeedRandom rand);

}
