package org.utm.featurehelper.feature;

import java.lang.reflect.Field;
import java.util.*;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeMesa;
import net.minecraft.world.gen.structure.*;
import net.minecraft.world.gen.structure.template.TemplateManager;

public class ComponentFactory {
    
    private static Map<String, IComponent> factory = new HashMap<>();
    private static List empty = new ArrayList();

    static {
        factory.put("MSRoom", (world, x, y, z, rand) -> {
            Biome biome = world.getBiome(new BlockPos(x, y, z));
            MapGenMineshaft.Type type = biome instanceof BiomeMesa ? MapGenMineshaft.Type.MESA : MapGenMineshaft.Type.NORMAL;
            return new StructureMineshaftPieces.Room(0, rand, x, z, type);
        });
        factory.put("MSCorridor", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb = StructureMineshaftPieces.Corridor.findCorridorSize(empty, rand, x, y, z, face);
            Biome biome = world.getBiome(new BlockPos(x, y, z));
            MapGenMineshaft.Type type = biome instanceof BiomeMesa ? MapGenMineshaft.Type.MESA : MapGenMineshaft.Type.NORMAL;
            return new StructureMineshaftPieces.Corridor(0, rand, bb, face, type);
        });
        factory.put("MSCrossing", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb = StructureMineshaftPieces.Cross.findCrossing(empty, rand, x, y, z, face);
            Biome biome = world.getBiome(new BlockPos(x, y, z));
            MapGenMineshaft.Type type = biome instanceof BiomeMesa ? MapGenMineshaft.Type.MESA : MapGenMineshaft.Type.NORMAL;
            return new StructureMineshaftPieces.Cross(0, rand, bb, face, type);
        });
        factory.put("MSStairs", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb = StructureMineshaftPieces.Stairs.findStairs(empty, rand, x, y, z, face);
            Biome biome = world.getBiome(new BlockPos(x, y, z));
            MapGenMineshaft.Type type = biome instanceof BiomeMesa ? MapGenMineshaft.Type.MESA : MapGenMineshaft.Type.NORMAL;
            return new StructureMineshaftPieces.Stairs(0, rand, bb, face, type);
        });
        factory.put("ViStart", (world, x, y, z, rand) -> null);
        factory.put("ViW", (world, x, y, z, rand) -> new StructureVillagePieces.Well(null, 0, rand, x, z));
        factory.put("ViBH", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureVillagePieces.Start start = new StructureVillagePieces.Start(world.getBiomeProvider(), 0, rand, x, z, empty, 0);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 9, 9, 6, face);
            return new StructureVillagePieces.House1(start, 0, rand, bb, face);
        });
        factory.put("ViS", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureVillagePieces.Start start = new StructureVillagePieces.Start(world.getBiomeProvider(), 0, rand, x, z, empty, 0);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 10, 6, 7, face);
            return new StructureVillagePieces.House2(start, 0, rand, bb, face);
        });
        factory.put("ViTRH", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureVillagePieces.Start start = new StructureVillagePieces.Start(world.getBiomeProvider(), 0, rand, x, z, empty, 0);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 9, 7, 12, face);
            return new StructureVillagePieces.House3(start, 0, rand, bb, face);
        });
        factory.put("ViSH", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureVillagePieces.Start start = new StructureVillagePieces.Start(world.getBiomeProvider(), 0, rand, x, z, empty, 0);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 5, 6, 5, face);
            return new StructureVillagePieces.House4Garden(start, 0, rand, bb, face);
        });
        factory.put("ViST", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureVillagePieces.Start start = new StructureVillagePieces.Start(world.getBiomeProvider(), 0, rand, x, z, empty, 0);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 5, 12, 9, face);
            return new StructureVillagePieces.Church(start, 0, rand, bb, face);
        });
        factory.put("ViDF", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureVillagePieces.Start start = new StructureVillagePieces.Start(world.getBiomeProvider(), 0, rand, x, z, empty, 0);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 13, 4, 9, face);
            return new StructureVillagePieces.Field1(start, 0, rand, bb, face);
        });
        factory.put("ViF", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureVillagePieces.Start start = new StructureVillagePieces.Start(world.getBiomeProvider(), 0, rand, x, z, empty, 0);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 7, 4, 9, face);
            return new StructureVillagePieces.Field2(start, 0, rand, bb, face);
        });
        factory.put("ViL", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureVillagePieces.Start start = new StructureVillagePieces.Start(world.getBiomeProvider(), 0, rand, x, z, empty, 0);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 3, 4, 2, face);
            return new StructureVillagePieces.Torch(start, 0, rand, bb, face);
        });
        factory.put("ViPH", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureVillagePieces.Start start = new StructureVillagePieces.Start(world.getBiomeProvider(), 0, rand, x, z, empty, 0);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 9, 7, 11, face);
            return new StructureVillagePieces.Hall(start, 0, rand, bb, face);
        });
        factory.put("ViSmH", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureVillagePieces.Start start = new StructureVillagePieces.Start(world.getBiomeProvider(), 0, rand, x, z, empty, 0);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 4, 6, 5, face);
            return new StructureVillagePieces.WoodHut(start, 0, rand, bb, face);
        });
        factory.put("ViSR", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureVillagePieces.Start start = new StructureVillagePieces.Start(world.getBiomeProvider(), 0, rand, x, z, empty, 0);
            StructureBoundingBox bb = StructureVillagePieces.Path.findPieceBox(start, empty, rand, x, y, z, face);
            return new StructureVillagePieces.Path(start, 0, rand, bb, face);
        });
        factory.put("TeDP", (world, x, y, z, rand) -> new ComponentScatteredFeaturePieces.DesertPyramid(rand, x, z));
        factory.put("TeJP", (world, x, y, z, rand) -> new ComponentScatteredFeaturePieces.JunglePyramid(rand, x, z));
        factory.put("TeSH", (world, x, y, z, rand) -> new ComponentScatteredFeaturePieces.SwampHut(rand, x, z));
        factory.put("Iglu", (world, x, y, z, rand) -> new ComponentScatteredFeaturePieces.Igloo(rand, x, z));
        factory.put("SHStart", (world, x, y, z, rand) -> new StructureStrongholdPieces.Stairs2(0, rand, x, z));
        factory.put("SHSD", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -7, 0, 5, 11, 5, face);
            return new StructureStrongholdPieces.Stairs(0, rand, bb, face);
        });
        factory.put("SHCC", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -1, 0, 5, 5, 7, face);
            return new StructureStrongholdPieces.ChestCorridor(0, rand, bb, face);
        });
        factory.put("SHFC", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -1, 0, 5, 5, rand.nextInt(3) + 1, face);
            return new StructureStrongholdPieces.Corridor(0, rand, bb, face);
        });
        factory.put("SH5C", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -4, -3, 0, 10, 9, 11, face);
            return new StructureStrongholdPieces.Crossing(0, rand, bb, face);
        });
        factory.put("SHLT", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -1, 0, 5, 5, 5, face);
            return new StructureStrongholdPieces.LeftTurn(0, rand, bb, face);
        });
        // same as SHLT
        factory.put("SHRT", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -1, 0, 5, 5, 5, face);
            return new StructureStrongholdPieces.LeftTurn(0, rand, bb, face);
        });
        factory.put("SHLi", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb;
            if (rand.nextInt(2) == 0)
                bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -4, -1, 0, 14, 11, 15, face);
            else
                bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -4, -1, 0, 14, 6, 15, face);
            return new StructureStrongholdPieces.Library(0, rand, bb, face);
        });
        factory.put("SHPR", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -4, -1, 0, 11, 8, 16, face);
            return new StructureStrongholdPieces.PortalRoom(0, rand, bb, face);
        });
        factory.put("SHPH", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -1, 0, 9, 5, 11, face);
            return new StructureStrongholdPieces.Prison(0, rand, bb, face);
        });
        factory.put("SHRC", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -4, -1, 0, 11, 7, 11, face);
            return new StructureStrongholdPieces.RoomCrossing(0, rand, bb, face);
        });
        factory.put("SHS", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -1, 0, 5, 5, 7, face);
            return new StructureStrongholdPieces.Straight(0, rand, bb, face);
        });
        factory.put("SHSSD", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -7, 0, 5, 11, 8, face);
            return new StructureStrongholdPieces.StairsStraight(0, rand, bb, face);
        });
        factory.put("NeStart", (world, x, y, z, rand) -> new StructureNetherBridgePieces.Start(rand, x, z));
        factory.put("NeBCr", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -8, -3, 0, 19, 10, 19, face);
            return new StructureNetherBridgePieces.Crossing3(0, rand, bb, face);
        });
        factory.put("NeSCSC", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, 0, 0, 5, 7, 5, face);
            return new StructureNetherBridgePieces.Crossing2(0, rand, bb, face);
        });
        factory.put("NeRC", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -2, 0, 0, 7, 9, 7, face);
            return new StructureNetherBridgePieces.Crossing(0, rand, bb, face);
        });
        factory.put("NeBS", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -3, 0, 5, 10, 19, face);
            return new StructureNetherBridgePieces.Straight(0, rand, bb, face);
        });
        factory.put("NeSCLT", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, 0, 0, 5, 7, 5, face);
            return new StructureNetherBridgePieces.Corridor(0, rand, bb, face);
        });
        factory.put("NeSCRT", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, 0, 0, 5, 7, 5, face);
            return new StructureNetherBridgePieces.Corridor2(0, rand, bb, face);
        });
        factory.put("NeCCS", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -7, 0, 5, 14, 10, face);
            return new StructureNetherBridgePieces.Corridor3(0, rand, bb, face);
        });
        factory.put("NeCTB", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -3, 0, 0, 9, 7, 9, face);
            return new StructureNetherBridgePieces.Corridor4(0, rand, bb, face);
        });
        factory.put("NeSC", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, 0, 0, 5, 7, 5, face);
            return new StructureNetherBridgePieces.Corridor5(0, rand, bb, face);
        });
        factory.put("NeCE", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -5, -3, 0, 13, 14, 13, face);
            return new StructureNetherBridgePieces.Entrance(0, rand, bb, face);
        });
        factory.put("NeCSR", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -5, -3, 0, 13, 14, 13, face);
            return new StructureNetherBridgePieces.NetherStalkRoom(0, rand, bb, face);
        });
        factory.put("NeMT", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -2, 0, 0, 7, 8, 9, face);
            return new StructureNetherBridgePieces.Throne(0, rand, bb, face);
        });
        factory.put("NeSR", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -2, 0, 0, 7, 11, 7, face);
            return new StructureNetherBridgePieces.Stairs(0, rand, bb, face);
        });
        factory.put("NeBEF", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -3, 0, 5, 10, 8, face);
            return new StructureNetherBridgePieces.End(0, rand, bb, face);
        });
        factory.put("OMB", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureOceanMonumentPieces.MonumentBuilding building = new StructureOceanMonumentPieces.MonumentBuilding(rand, x, z, face);
            building.childPieces.clear();
            return building;
        });
        factory.put("OMCR", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            // int roomIndex = 10 + rand.nextInt(4);
            StructureOceanMonumentPieces.RoomDefinition room = new StructureOceanMonumentPieces.RoomDefinition(0);
            StructureOceanMonumentPieces.MonumentCoreRoom piece = new StructureOceanMonumentPieces.MonumentCoreRoom(face, room, rand);
            piece.getBoundingBox().offset(x, y, z);
            return piece;
        });
        factory.put("OMDXR", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureOceanMonumentPieces.RoomDefinition room = new StructureOceanMonumentPieces.RoomDefinition(0);
            StructureOceanMonumentPieces.RoomDefinition east = new StructureOceanMonumentPieces.RoomDefinition(0);
            room.setConnection(EnumFacing.EAST, east);
            StructureOceanMonumentPieces.DoubleXRoom piece = new StructureOceanMonumentPieces.DoubleXRoom(face, room, rand);
            piece.getBoundingBox().offset(x, y, z);
            return piece;
        });
        factory.put("OMDXYR", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureOceanMonumentPieces.RoomDefinition room = new StructureOceanMonumentPieces.RoomDefinition(0);
            StructureOceanMonumentPieces.RoomDefinition east = new StructureOceanMonumentPieces.RoomDefinition(0);
            StructureOceanMonumentPieces.RoomDefinition up = new StructureOceanMonumentPieces.RoomDefinition(0);
            StructureOceanMonumentPieces.RoomDefinition eastUp = new StructureOceanMonumentPieces.RoomDefinition(0);
            room.setConnection(EnumFacing.EAST, east);
            room.setConnection(EnumFacing.UP, up);
            east.setConnection(EnumFacing.UP, eastUp);
            StructureOceanMonumentPieces.DoubleXYRoom piece = new StructureOceanMonumentPieces.DoubleXYRoom(face, room, rand);
            piece.getBoundingBox().offset(x, y, z);
            return piece;
        });
        factory.put("OMDYR", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureOceanMonumentPieces.RoomDefinition room = new StructureOceanMonumentPieces.RoomDefinition(0);
            StructureOceanMonumentPieces.RoomDefinition up = new StructureOceanMonumentPieces.RoomDefinition(0);
            room.setConnection(EnumFacing.UP, up);
            StructureOceanMonumentPieces.DoubleYRoom piece = new StructureOceanMonumentPieces.DoubleYRoom(face, room, rand);
            piece.getBoundingBox().offset(x, y, z);
            return piece;
        });
        factory.put("OMDYZR", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureOceanMonumentPieces.RoomDefinition room = new StructureOceanMonumentPieces.RoomDefinition(0);
            StructureOceanMonumentPieces.RoomDefinition north = new StructureOceanMonumentPieces.RoomDefinition(0);
            StructureOceanMonumentPieces.RoomDefinition up = new StructureOceanMonumentPieces.RoomDefinition(0);
            StructureOceanMonumentPieces.RoomDefinition northUp = new StructureOceanMonumentPieces.RoomDefinition(0);
            room.setConnection(EnumFacing.NORTH, north);
            room.setConnection(EnumFacing.UP, up);
            north.setConnection(EnumFacing.UP, northUp);
            StructureOceanMonumentPieces.DoubleYZRoom piece = new StructureOceanMonumentPieces.DoubleYZRoom(face, room, rand);
            piece.getBoundingBox().offset(x, y, z);
            return piece;
        });
        factory.put("OMDZR", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureOceanMonumentPieces.RoomDefinition room = new StructureOceanMonumentPieces.RoomDefinition(0);
            StructureOceanMonumentPieces.RoomDefinition north = new StructureOceanMonumentPieces.RoomDefinition(0);
            room.setConnection(EnumFacing.NORTH, north);
            StructureOceanMonumentPieces.DoubleZRoom piece = new StructureOceanMonumentPieces.DoubleZRoom(face, room, rand);
            piece.getBoundingBox().offset(x, y, z);
            return piece;
        });
        factory.put("OMEntry", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureOceanMonumentPieces.RoomDefinition room = new StructureOceanMonumentPieces.RoomDefinition(0);
            StructureOceanMonumentPieces.EntryRoom piece = new StructureOceanMonumentPieces.EntryRoom(face, room);
            piece.getBoundingBox().offset(x, y, z);
            return piece;
        });
        factory.put("OMPenthouse", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureBoundingBox bb = StructureBoundingBox.createProper(x, y, z, x + 13, y + 4, z + 13);
            return new StructureOceanMonumentPieces.Penthouse(face, bb);
        });
        factory.put("OMSimple", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureOceanMonumentPieces.RoomDefinition room = new StructureOceanMonumentPieces.RoomDefinition(0);
            StructureOceanMonumentPieces.SimpleRoom piece = new StructureOceanMonumentPieces.SimpleRoom(face, room, rand);
            piece.getBoundingBox().offset(x, y, z);
            return piece;
        });
        factory.put("OMSimpleT", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            StructureOceanMonumentPieces.RoomDefinition room = new StructureOceanMonumentPieces.RoomDefinition(0);
            StructureOceanMonumentPieces.SimpleTopRoom piece = new StructureOceanMonumentPieces.SimpleTopRoom(face, room, rand);
            piece.getBoundingBox().offset(x, y, z);
            return piece;
        });  // StructureOceanMonumentPieces.WingRoom?
        factory.put("ECP", (world, x, y, z, rand) -> {
            TemplateManager manager = world.getSaveHandler().getStructureTemplateManager();
            String template = (new String[] {
                    "base_floor",
                    "base_roof",
                    "bridge_end",
                    "bridge_gentle_stairs",
                    "bridge_piece",
                    "bridge_steep_stairs",
                    "fat_tower_base",
                    "fat_tower_middle",
                    "fat_tower_top",
                    "second_floor",
                    "second_floor_2",
                    "second_roof",
                    "ship",
                    "third_floor",
                    "third_floor_b",
                    "third_floor_c",
                    "third_roof",
                    "tower_base",
                    "tower_floor",
                    "tower_piece",
                    "tower_top"
            })[rand.nextInt(21)];
            Rotation rotation = Rotation.values()[rand.nextInt(Rotation.values().length)];
            return new StructureEndCityPieces.CityTemplate(manager, template, new BlockPos(x, y, z), rotation, true);
        });
        factory.put("WMP", (world, x, y, z, rand) -> {
            TemplateManager manager = world.getSaveHandler().getStructureTemplateManager();
            String template = (new String[] {
                    "1x1_a1",
                    "1x1_a2",
                    "1x1_a3",
                    "1x1_a4",
                    "1x1_a5",
                    "1x1_as1",
                    "1x1_as2",
                    "1x1_as3",
                    "1x1_as4",
                    "1x1_b1",
                    "1x1_b2",
                    "1x1_b3",
                    "1x1_b4",
                    "1x1_b5",
                    "1x2_a1",
                    "1x2_a2",
                    "1x2_a3",
                    "1x2_a4",
                    "1x2_a5",
                    "1x2_a6",
                    "1x2_a7",
                    "1x2_a8",
                    "1x2_a9",
                    "1x2_b1",
                    "1x2_b2",
                    "1x2_b3",
                    "1x2_b4",
                    "1x2_b5",
                    "1x2_c1",
                    "1x2_c2",
                    "1x2_c3",
                    "1x2_c4",
                    "1x2_c_stairs",
                    "1x2_d1",
                    "1x2_d2",
                    "1x2_d3",
                    "1x2_d4",
                    "1x2_d5",
                    "1x2_d_stairs",
                    "1x2_s1",
                    "1x2_s2",
                    "1x2_se1",
                    "2x2_a1",
                    "2x2_a2",
                    "2x2_a3",
                    "2x2_a4",
                    "2x2_b1",
                    "2x2_b2",
                    "2x2_b3",
                    "2x2_b4",
                    "2x2_b5",
                    "2x2_s1",
                    "carpet_east",
                    "carpet_north",
                    "carpet_south",
                    "carpet_south_2",
                    "carpet_west",
                    "carpet_west_2",
                    "corridor_floor",
                    "entrance",
                    "indoors_door",
                    "indoors_door_2",
                    "indoors_wall",
                    "indoors_wall_2",
                    "roof",
                    "roof_corner",
                    "roof_front",
                    "roof_inner_corner",
                    "small_wall",
                    "small_wall_corner",
                    "wall_corner",
                    "wall_flat",
                    "wall_window"
            })[rand.nextInt(73)];
            Rotation rotation = Rotation.values()[rand.nextInt(Rotation.values().length)];
            return new WoodlandMansionPieces.MansionTemplate(manager, template, new BlockPos(x, y, z), rotation);
        });
    }

    public static Set<String> getNameSet() {
        return factory.keySet();
    }
    
    public static StructureComponent getComponent(String id, World world, int x, int y, int z, Random rand) {
        return factory.get(id).getComponent(world, x, y, z, rand);
    }

}
