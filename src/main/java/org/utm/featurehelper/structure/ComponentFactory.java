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
		factory.put("ViW", new IComponent() {  // 水井
			@Override
			public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
				return new StructureVillagePieces.Well(null, 0, rand, x, z);
			}
		});
		factory.put("ViBH", new IComponent() {  // 图书馆
			@Override
			public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
				int face = rand.nextInt(4);
				StructureVillagePieces.Start start = new StructureVillagePieces.Start(world.getWorldChunkManager(), 0, rand, x, z, empty, 0);
				StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 9, 9, 6, face);
				return new StructureVillagePieces.House1(start, 0, rand, bb, face);
			}
		});
		factory.put("ViS", new IComponent() {  // 铁匠铺
			@Override
			public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
				int face = rand.nextInt(4);
				StructureVillagePieces.Start start = new StructureVillagePieces.Start(world.getWorldChunkManager(), 0, rand, x, z, empty, 0);
				StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 10, 6, 7, face);
				return new StructureVillagePieces.House2(start, 0, rand, bb, face);
			}
		});
		factory.put("ViTRH", new IComponent() {  // 大房子
			@Override
			public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
				int face = rand.nextInt(4);
				StructureVillagePieces.Start start = new StructureVillagePieces.Start(world.getWorldChunkManager(), 0, rand, x, z, empty, 0);
				StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 9, 7, 12, face);
				return new StructureVillagePieces.House3(start, 0, rand, bb, face);
			}
		});
		factory.put("ViSH", new IComponent() {  // 小房子
			@Override
			public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
				int face = rand.nextInt(4);
				StructureVillagePieces.Start start = new StructureVillagePieces.Start(world.getWorldChunkManager(), 0, rand, x, z, empty, 0);
				StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 5, 6, 5, face);
				return new StructureVillagePieces.House4Garden(start, 0, rand, bb, face);
			}
		});
		factory.put("ViST", new IComponent() {  // 教堂
			@Override
			public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
				int face = rand.nextInt(4);
				StructureVillagePieces.Start start = new StructureVillagePieces.Start(world.getWorldChunkManager(), 0, rand, x, z, empty, 0);
				StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 5, 12, 9, face);
				return new StructureVillagePieces.Church(start, 0, rand, bb, face);
			}
		});
		factory.put("ViDF", new IComponent() {  // 大农田
			@Override
			public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
				int face = rand.nextInt(4);
				StructureVillagePieces.Start start = new StructureVillagePieces.Start(world.getWorldChunkManager(), 0, rand, x, z, empty, 0);
				StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 13, 4, 9, face);
				return new StructureVillagePieces.Field1(start, 0, rand, bb, face);
			}
		});
		factory.put("ViF", new IComponent() {  // 农田
			@Override
			public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
				int face = rand.nextInt(4);
				StructureVillagePieces.Start start = new StructureVillagePieces.Start(world.getWorldChunkManager(), 0, rand, x, z, empty, 0);
				StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 7, 4, 9, face);
				return new StructureVillagePieces.Field2(start, 0, rand, bb, face);
			}
		});
		factory.put("ViL", new IComponent() {  // 路灯
			@Override
			public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
				int face = rand.nextInt(4);
				StructureVillagePieces.Start start = new StructureVillagePieces.Start(world.getWorldChunkManager(), 0, rand, x, z, empty, 0);
				StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 3, 4, 2, face);
				return new StructureVillagePieces.Torch(start, 0, rand, bb, face);
			}
		});
		factory.put("ViPH", new IComponent() {  // 牧场
			@Override
			public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
				int face = rand.nextInt(4);
				StructureVillagePieces.Start start = new StructureVillagePieces.Start(world.getWorldChunkManager(), 0, rand, x, z, empty, 0);
				StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 9, 7, 11, face);
				return new StructureVillagePieces.Hall(start, 0, rand, bb, face);
			}
		});
		factory.put("ViSmH", new IComponent() {  // 小木屋
			@Override
			public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
				int face = rand.nextInt(4);
				StructureVillagePieces.Start start = new StructureVillagePieces.Start(world.getWorldChunkManager(), 0, rand, x, z, empty, 0);
				StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 4, 6, 5, face);
				return new StructureVillagePieces.WoodHut(start, 0, rand, bb, face);
			}
		});
		factory.put("ViSR", new IComponent() {  // 路
			@Override
			public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
				int face = rand.nextInt(4);
				StructureVillagePieces.Start start = new StructureVillagePieces.Start(world.getWorldChunkManager(), 0, rand, x, z, empty, 0);
				StructureBoundingBox bb = StructureVillagePieces.Path.func_74933_a(start, empty, rand, x, y, z, face);
				return new StructureVillagePieces.Path(start, 0, rand, bb, face);
			}
		});
	}
	
	public static StructureComponent getComponent(String id, World world, int x, int y, int z, Random rand) {
		return factory.get(id).getComponent(world, x, y, z, rand);
	}

}
