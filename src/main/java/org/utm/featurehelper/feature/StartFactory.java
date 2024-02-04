package org.utm.featurehelper.feature;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.structure.*;
import org.utm.featurehelper.feature.patch.*;

public class StartFactory {
    
    private static final Map<String, IStart> factory = new HashMap<>();

    static {
        factory.put("Mineshaft", (world, chunkX, chunkZ, rand) -> {
            Biome biome = world.getBiome(new BlockPos((chunkX << 4) + 9, 0, (chunkZ << 4) + 9));
            return new MineshaftStartPatcher(world, world.getChunkProvider().getChunkGenerator(), rand, chunkX, chunkZ, biome);
        });
        factory.put("Village", (world, chunkX, chunkZ, rand) -> {
            Biome biome = world.getBiome(new BlockPos((chunkX << 4) + 9, 0, (chunkZ << 4) + 9));
            return new VillageStartPatcher(world, world.getChunkProvider().getChunkGenerator(), rand, chunkX, chunkZ, biome);
        });
        factory.put("Stronghold", (world, chunkX, chunkZ, rand) -> {
            Biome biome = world.getBiome(new BlockPos((chunkX << 4) + 9, 0, (chunkZ << 4) + 9));
            return new StrongholdStructure.Start(world, rand, chunkX, chunkZ, biome, 0);
        });
        factory.put("Desert_Pyramid", (world, chunkX, chunkZ, rand) -> {
            Biome biome = world.getBiome(new BlockPos((chunkX << 4) + 9, 0, (chunkZ << 4) + 9));
            return new DesertPyramidStructure.Start(world, rand, chunkX, chunkZ, biome);
        });
        factory.put("Jungle_Pyramid", (world, chunkX, chunkZ, rand) -> {
            Biome biome = world.getBiome(new BlockPos((chunkX << 4) + 9, 0, (chunkZ << 4) + 9));
            return new JunglePyramidStructure.Start(world, rand, chunkX, chunkZ, biome);
        });
        factory.put("Swamp_Hut", (world, chunkX, chunkZ, rand) -> {
            Biome biome = world.getBiome(new BlockPos((chunkX << 4) + 9, 0, (chunkZ << 4) + 9));
            return new SwampHutStructure.Start(world, rand, chunkX, chunkZ, biome);
        });
        factory.put("Igloo", (world, chunkX, chunkZ, rand) -> {
            Biome biome = world.getBiome(new BlockPos((chunkX << 4) + 9, 0, (chunkZ << 4) + 9));
            return new IglooStructure.Start(world, world.getChunkProvider().getChunkGenerator(), rand, chunkX, chunkZ, biome);
        });
        factory.put("Shipwreck", (world, chunkX, chunkZ, rand) -> {
            Biome biome = world.getBiome(new BlockPos((chunkX << 4) + 9, 0, (chunkZ << 4) + 9));
            return new ShipwreckStartPatcher(world, world.getChunkProvider().getChunkGenerator(), rand, chunkX, chunkZ, biome);
        });
        factory.put("Ocean_Ruin", (world, chunkX, chunkZ, rand) -> {
            Biome biome = world.getBiome(new BlockPos((chunkX << 4) + 9, 0, (chunkZ << 4) + 9));
            return new OceanRuinStartPatcher(world, world.getChunkProvider().getChunkGenerator(), rand, chunkX, chunkZ, biome);
        });
        factory.put("Fortress", (world, chunkX, chunkZ, rand) -> {
            Biome biome = world.getBiome(new BlockPos((chunkX << 4) + 9, 0, (chunkZ << 4) + 9));
            return new FortressStructure.Start(world, rand, chunkX, chunkZ, biome);
        });
        factory.put("Monument", (world, chunkX, chunkZ, rand) -> {
            Biome biome = world.getBiome(new BlockPos((chunkX << 4) + 9, 0, (chunkZ << 4) + 9));
            return new OceanMonumentStructure.Start(world, rand, chunkX, chunkZ, biome);
        });
        factory.put("EndCity", (world, chunkX, chunkZ, rand) -> {
            Biome biome = world.getBiome(new BlockPos((chunkX << 4) + 9, 0, (chunkZ << 4) + 9));
            return new EndCityStartPatcher(world, rand, chunkX, chunkZ, biome);
        });
        factory.put("Mansion", (world, chunkX, chunkZ, rand) -> {
            Biome biome = world.getBiome(new BlockPos((chunkX << 4) + 9, 0, (chunkZ << 4) + 9));
            return new WoodlandMansionStartPatcher(world, rand, chunkX, chunkZ, biome);
        });
        factory.put("Buried_Treasure", (world, chunkX, chunkZ, rand) -> {
            Biome biome = world.getBiome(new BlockPos((chunkX << 4) + 9, 0, (chunkZ << 4) + 9));
            return new BuriedTreasureStructure.Start(world, world.getChunkProvider().getChunkGenerator(), rand, chunkX, chunkZ, biome);
        });
    }

    public static Set<String> getNameSet() {
        return factory.keySet();
    }
    
    public static StructureStart getStart(String id, World world, int chunkX, int chunkZ, SharedSeedRandom rand) {
        return factory.get(id).getStart(world, chunkX, chunkZ, rand);
    }

}
