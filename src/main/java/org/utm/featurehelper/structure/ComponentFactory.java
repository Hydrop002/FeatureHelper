package org.utm.featurehelper.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureMineshaftPieces;
import net.minecraft.world.gen.structure.StructureVillagePieces;

public class ComponentFactory {
	
	private static Map<String, IComponent> factory = new HashMap();
	private static List empty = new ArrayList();

	static {
		factory.put("MSRoom", new IComponent() {
			@Override
			public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
				return new StructureMineshaftPieces.Room(0, rand, x, z);
			}
		});
		factory.put("MSCorridor", new IComponent() {
			@Override
			public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
				int face = rand.nextInt(4);
				StructureBoundingBox bb = StructureMineshaftPieces.Corridor.findValidPlacement(empty, rand, x, y, z, face);
				return new StructureMineshaftPieces.Corridor(0, rand, bb, face);
			}
		});
		factory.put("MSCrossing", new IComponent() {
			@Override
			public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
				int face = rand.nextInt(4);
				StructureBoundingBox bb = StructureMineshaftPieces.Cross.findValidPlacement(empty, rand, x, y, z, face);
				return new StructureMineshaftPieces.Cross(0, rand, bb, face);
			}
		});
		factory.put("MSStairs", new IComponent() {
			@Override
			public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
				int face = rand.nextInt(4);
				StructureBoundingBox bb = StructureMineshaftPieces.Stairs.findValidPlacement(empty, rand, x, y, z, face);
				return new StructureMineshaftPieces.Stairs(0, rand, bb, face);
			}
		});
		factory.put("ViStart", new IComponent() {
			@Override
			public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
				return null;
			}
		});
		factory.put("ViW", new IComponent() {
			@Override
			public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
				return new StructureVillagePieces.Well(null, 0, rand, x, z);
			}
		});
	}
	
	public static StructureComponent getComponent(String id, World world, int x, int y, int z, Random rand) {
		return factory.get(id).getComponent(world, x, y, z, rand);
	}

}
