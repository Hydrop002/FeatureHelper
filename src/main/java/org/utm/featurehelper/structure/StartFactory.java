package org.utm.featurehelper.structure;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenNetherBridge;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureMineshaftStart;
import net.minecraft.world.gen.structure.StructureStart;

public class StartFactory {
	
	private static Map<String, IStart> factory = new HashMap();
	
	static {
		factory.put("Mineshaft", new IStart() {
			@Override
			public StructureStart getStart(World world, int chunkX, int chunkZ, Random rand) {
				return new StructureMineshaftStart(world, rand, chunkX, chunkZ);
			}
		});
		factory.put("Village", new IStart() {
			@Override
			public StructureStart getStart(World world, int chunkX, int chunkZ, Random rand) {
				return new MapGenVillage.Start(world, rand, chunkX, chunkZ, 0);
			}
		});
		factory.put("Stronghold", new IStart() {
			@Override
			public StructureStart getStart(World world, int chunkX, int chunkZ, Random rand) {
				return new MapGenStronghold.Start(world, rand, chunkX, chunkZ);
			}
		});
		factory.put("Temple", new IStart() {
			@Override
			public StructureStart getStart(World world, int chunkX, int chunkZ, Random rand) {
				return new MapGenScatteredFeature.Start(world, rand, chunkX, chunkZ);
			}
		});
		factory.put("Fortress", new IStart() {
			@Override
			public StructureStart getStart(World world, int chunkX, int chunkZ, Random rand) {
				return new MapGenNetherBridge.Start(world, rand, chunkX, chunkZ);
			}
		});
	}
	
	public static StructureStart getStart(String id, World world, int chunkX, int chunkZ, Random rand) {
		return factory.get(id).getStart(world, chunkX, chunkZ, rand);
	}

}
