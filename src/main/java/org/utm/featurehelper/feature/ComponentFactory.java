package org.utm.featurehelper.feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureMineshaftPieces;
import net.minecraft.world.gen.structure.StructureNetherBridgePieces;
import net.minecraft.world.gen.structure.StructureStrongholdPieces;
import net.minecraft.world.gen.structure.StructureVillagePieces;

public class ComponentFactory {
    
    private static Map<String, IComponent> factory = new HashMap<String, IComponent>();
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
        factory.put("TeDP", new IComponent() {  // 沙漠神殿
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                return new ComponentScatteredFeaturePieces.DesertPyramid(rand, x, z);
            }
        });
        factory.put("TeJP", new IComponent() {  // 丛林神庙
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                return new ComponentScatteredFeaturePieces.JunglePyramid(rand, x, z);
            }
        });
        factory.put("TeSH", new IComponent() {  // 女巫小屋
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                return new ComponentScatteredFeaturePieces.SwampHut(rand, x, z);
            }
        });
        factory.put("SHStart", new IComponent() {
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                return new StructureStrongholdPieces.Stairs2(0, rand, x, z);
            }
        });
        factory.put("SHSD", new IComponent() {
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                int face = rand.nextInt(4);
                StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -7, 0, 5, 11, 5, face);
                return new StructureStrongholdPieces.Stairs(0, rand, bb, face);
            }
        });
        factory.put("SHCC", new IComponent() {
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                int face = rand.nextInt(4);
                StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -1, 0, 5, 5, 7, face);
                return new StructureStrongholdPieces.ChestCorridor(0, rand, bb, face);
            }
        });
        factory.put("SHFC", new IComponent() {
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                int face = rand.nextInt(4);
                StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -1, 0, 5, 5, rand.nextInt(3) + 1, face);
                return new StructureStrongholdPieces.Corridor(0, rand, bb, face);
            }
        });
        factory.put("SH5C", new IComponent() {
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                int face = rand.nextInt(4);
                StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -4, -3, 0, 10, 9, 11, face);
                return new StructureStrongholdPieces.Crossing(0, rand, bb, face);
            }
        });
        factory.put("SHLT", new IComponent() {
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                int face = rand.nextInt(4);
                StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -1, 0, 5, 5, 5, face);
                return new StructureStrongholdPieces.LeftTurn(0, rand, bb, face);
            }
        });
        factory.put("SHRT", new IComponent() {  // same as SHLT
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                int face = rand.nextInt(4);
                StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -1, 0, 5, 5, 5, face);
                return new StructureStrongholdPieces.LeftTurn(0, rand, bb, face);
            }
        });
        factory.put("SHLi", new IComponent() {
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                int face = rand.nextInt(4);
                StructureBoundingBox bb;
                if (rand.nextInt(2) == 0)
                    bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -4, -1, 0, 14, 11, 15, face);
                else
                    bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -4, -1, 0, 14, 6, 15, face);
                return new StructureStrongholdPieces.Library(0, rand, bb, face);
            }
        });
        factory.put("SHPR", new IComponent() {
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                int face = rand.nextInt(4);
                StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -4, -1, 0, 11, 8, 16, face);
                return new StructureStrongholdPieces.PortalRoom(0, rand, bb, face);
            }
        });
        factory.put("SHPH", new IComponent() {
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                int face = rand.nextInt(4);
                StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -1, 0, 9, 5, 11, face);
                return new StructureStrongholdPieces.Prison(0, rand, bb, face);
            }
        });
        factory.put("SHRC", new IComponent() {
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                int face = rand.nextInt(4);
                StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -4, -1, 0, 11, 7, 11, face);
                return new StructureStrongholdPieces.RoomCrossing(0, rand, bb, face);
            }
        });
        factory.put("SHS", new IComponent() {
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                int face = rand.nextInt(4);
                StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -1, 0, 5, 5, 7, face);
                return new StructureStrongholdPieces.Straight(0, rand, bb, face);
            }
        });
        factory.put("SHSSD", new IComponent() {
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                int face = rand.nextInt(4);
                StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -7, 0, 5, 11, 8, face);
                return new StructureStrongholdPieces.StairsStraight(0, rand, bb, face);
            }
        });
        factory.put("NeStart", new IComponent() {
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                return new StructureNetherBridgePieces.Start(rand, x, z);
            }
        });
        factory.put("NeBCr", new IComponent() {
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                int face = rand.nextInt(4);
                StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -8, -3, 0, 19, 10, 19, face);
                return new StructureNetherBridgePieces.Crossing3(0, rand, bb, face);
            }
        });
        factory.put("NeSCSC", new IComponent() {
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                int face = rand.nextInt(4);
                StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, 0, 0, 5, 7, 5, face);
                return new StructureNetherBridgePieces.Crossing2(0, rand, bb, face);
            }
        });
        factory.put("NeRC", new IComponent() {
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                int face = rand.nextInt(4);
                StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -2, 0, 0, 7, 9, 7, face);
                return new StructureNetherBridgePieces.Crossing(0, rand, bb, face);
            }
        });
        factory.put("NeBS", new IComponent() {
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                int face = rand.nextInt(4);
                StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -3, 0, 5, 10, 19, face);
                return new StructureNetherBridgePieces.Straight(0, rand, bb, face);
            }
        });
        factory.put("NeSCLT", new IComponent() {
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                int face = rand.nextInt(4);
                StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, 0, 0, 5, 7, 5, face);
                return new StructureNetherBridgePieces.Corridor(0, rand, bb, face);
            }
        });
        factory.put("NeSCRT", new IComponent() {
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                int face = rand.nextInt(4);
                StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, 0, 0, 5, 7, 5, face);
                return new StructureNetherBridgePieces.Corridor2(0, rand, bb, face);
            }
        });
        factory.put("NeCCS", new IComponent() {
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                int face = rand.nextInt(4);
                StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -7, 0, 5, 14, 10, face);
                return new StructureNetherBridgePieces.Corridor3(0, rand, bb, face);
            }
        });
        factory.put("NeCTB", new IComponent() {
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                int face = rand.nextInt(4);
                StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -3, 0, 0, 9, 7, 9, face);
                return new StructureNetherBridgePieces.Corridor4(0, rand, bb, face);
            }
        });
        factory.put("NeSC", new IComponent() {
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                int face = rand.nextInt(4);
                StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, 0, 0, 5, 7, 5, face);
                return new StructureNetherBridgePieces.Corridor5(0, rand, bb, face);
            }
        });
        factory.put("NeCE", new IComponent() {
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                int face = rand.nextInt(4);
                StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -5, -3, 0, 13, 14, 13, face);
                return new StructureNetherBridgePieces.Entrance(0, rand, bb, face);
            }
        });
        factory.put("NeCSR", new IComponent() {
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                int face = rand.nextInt(4);
                StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -5, -3, 0, 13, 14, 13, face);
                return new StructureNetherBridgePieces.NetherStalkRoom(0, rand, bb, face);
            }
        });
        factory.put("NeMT", new IComponent() {
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                int face = rand.nextInt(4);
                StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -2, 0, 0, 7, 8, 9, face);
                return new StructureNetherBridgePieces.Throne(0, rand, bb, face);
            }
        });
        factory.put("NeSR", new IComponent() {
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                int face = rand.nextInt(4);
                StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -2, 0, 0, 7, 11, 7, face);
                return new StructureNetherBridgePieces.Stairs(0, rand, bb, face);
            }
        });
        factory.put("NeBEF", new IComponent() {
            @Override
            public StructureComponent getComponent(World world, int x, int y, int z, Random rand) {
                int face = rand.nextInt(4);
                StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -3, 0, 5, 10, 8, face);
                return new StructureNetherBridgePieces.End(0, rand, bb, face);
            }
        });
    }
    
    public static StructureComponent getComponent(String id, World world, int x, int y, int z, Random rand) {
        return factory.get(id).getComponent(world, x, y, z, rand);
    }

}
