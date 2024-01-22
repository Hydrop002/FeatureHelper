package org.utm.featurehelper.feature;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeMesa;
import net.minecraft.world.gen.structure.*;

public class StartFactory {
    
    private static Map<String, IStart> factory = new HashMap<>();
    
    static {
        factory.put("Mineshaft", (world, chunkX, chunkZ, rand) -> {
            Biome biome = world.getBiome(new BlockPos((chunkX << 4) + 8, 64, (chunkZ << 4) + 8));
            MapGenMineshaft.Type type = biome instanceof BiomeMesa ? MapGenMineshaft.Type.MESA : MapGenMineshaft.Type.NORMAL;
            return new StructureMineshaftStart(world, rand, chunkX, chunkZ, type);
        });
        factory.put("Village", (world, chunkX, chunkZ, rand) -> new MapGenVillage.Start(world, rand, chunkX, chunkZ, 0));
        factory.put("Stronghold", (world, chunkX, chunkZ, rand) -> new MapGenStronghold.Start(world, rand, chunkX, chunkZ));
        factory.put("Temple", (world, chunkX, chunkZ, rand) -> new MapGenScatteredFeature.Start(world, rand, chunkX, chunkZ));
        factory.put("Fortress", (world, chunkX, chunkZ, rand) -> new MapGenNetherBridge.Start(world, rand, chunkX, chunkZ));
        factory.put("Monument", (world, chunkX, chunkZ, rand) -> new StructureOceanMonument.StartMonument(world, rand, chunkX, chunkZ));
        factory.put("EndCity", (world, chunkX, chunkZ, rand) -> {
            // todo EndCityStartPatcher -- world.getChunkFromChunkCoords(chunkX, chunkZ).getHeightValue(7, 7)
            return null;
        });
        factory.put("Mansion", (world, chunkX, chunkZ, rand) -> {
            // todo MansionStartPatcher
            return null;
        });
    }

    public static Set<String> getNameSet() {
        return factory.keySet();
    }
    
    public static StructureStart getStart(String id, World world, int chunkX, int chunkZ, Random rand) {
        return factory.get(id).getStart(world, chunkX, chunkZ, rand);
    }

}
