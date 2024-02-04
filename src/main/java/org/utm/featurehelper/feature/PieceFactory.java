package org.utm.featurehelper.feature;

import java.util.*;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class PieceFactory {
    
    private static final Map<String, IPiece> factory = new HashMap<>();

    static {
        factory.put("MSRoom", (world, x, y, z, rand) -> {
            Biome biome = world.getBiome(new BlockPos(x, y, z));
            MineshaftConfig config = (MineshaftConfig) world.getChunkProvider().getChunkGenerator().getStructureConfig(biome, Feature.MINESHAFT);
            if (config == null) config = new MineshaftConfig(0.004, MineshaftStructure.Type.NORMAL);
            return new MineshaftPieces.Room(0, rand, x, z, config.type);
        });
        factory.put("MSCorridor", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb = MineshaftPieces.Corridor.findCorridorSize(Collections.emptyList(), rand, x, y, z, face);
            Biome biome = world.getBiome(new BlockPos(x, y, z));
            MineshaftConfig config = (MineshaftConfig) world.getChunkProvider().getChunkGenerator().getStructureConfig(biome, Feature.MINESHAFT);
            if (config == null) config = new MineshaftConfig(0.004, MineshaftStructure.Type.NORMAL);
            return new MineshaftPieces.Corridor(0, rand, bb, face, config.type);
        });
        factory.put("MSCrossing", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb = MineshaftPieces.Cross.findCrossing(Collections.emptyList(), rand, x, y, z, face);
            Biome biome = world.getBiome(new BlockPos(x, y, z));
            MineshaftConfig config = (MineshaftConfig) world.getChunkProvider().getChunkGenerator().getStructureConfig(biome, Feature.MINESHAFT);
            if (config == null) config = new MineshaftConfig(0.004, MineshaftStructure.Type.NORMAL);
            return new MineshaftPieces.Cross(0, rand, bb, face, config.type);
        });
        factory.put("MSStairs", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb = MineshaftPieces.Stairs.findStairs(Collections.emptyList(), rand, x, y, z, face);
            Biome biome = world.getBiome(new BlockPos(x, y, z));
            MineshaftConfig config = (MineshaftConfig) world.getChunkProvider().getChunkGenerator().getStructureConfig(biome, Feature.MINESHAFT);
            if (config == null) config = new MineshaftConfig(0.004, MineshaftStructure.Type.NORMAL);
            return new MineshaftPieces.Stairs(0, rand, bb, face, config.type);
        });
        factory.put("ViStart", (world, x, y, z, rand) -> {
            Biome biome = world.getBiome(new BlockPos(x, y, z));
            VillageConfig config = (VillageConfig) world.getChunkProvider().getChunkGenerator().getStructureConfig(biome, Feature.VILLAGE);
            if (config == null) config = new VillageConfig(0, VillagePieces.Type.OAK);
            return new VillagePieces.Start(0, rand, x, z, Collections.emptyList(), config, biome);
        });
        factory.put("ViW", (world, x, y, z, rand) -> new VillagePieces.Well(null, 0, rand, x, z));
        factory.put("ViBH", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            Biome biome = world.getBiome(new BlockPos(x, y, z));
            VillageConfig config = (VillageConfig) world.getChunkProvider().getChunkGenerator().getStructureConfig(biome, Feature.VILLAGE);
            if (config == null) config = new VillageConfig(0, VillagePieces.Type.OAK);
            VillagePieces.Start start = new VillagePieces.Start(0, rand, x, z, Collections.emptyList(), config, biome);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 9, 9, 6, face);
            return new VillagePieces.House1(start, 0, rand, bb, face);
        });
        factory.put("ViS", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            Biome biome = world.getBiome(new BlockPos(x, y, z));
            VillageConfig config = (VillageConfig) world.getChunkProvider().getChunkGenerator().getStructureConfig(biome, Feature.VILLAGE);
            if (config == null) config = new VillageConfig(0, VillagePieces.Type.OAK);
            VillagePieces.Start start = new VillagePieces.Start(0, rand, x, z, Collections.emptyList(), config, biome);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 10, 6, 7, face);
            return new VillagePieces.House2(start, 0, rand, bb, face);
        });
        factory.put("ViTRH", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            Biome biome = world.getBiome(new BlockPos(x, y, z));
            VillageConfig config = (VillageConfig) world.getChunkProvider().getChunkGenerator().getStructureConfig(biome, Feature.VILLAGE);
            if (config == null) config = new VillageConfig(0, VillagePieces.Type.OAK);
            VillagePieces.Start start = new VillagePieces.Start(0, rand, x, z, Collections.emptyList(), config, biome);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 9, 7, 12, face);
            return new VillagePieces.House3(start, 0, rand, bb, face);
        });
        factory.put("ViSH", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            Biome biome = world.getBiome(new BlockPos(x, y, z));
            VillageConfig config = (VillageConfig) world.getChunkProvider().getChunkGenerator().getStructureConfig(biome, Feature.VILLAGE);
            if (config == null) config = new VillageConfig(0, VillagePieces.Type.OAK);
            VillagePieces.Start start = new VillagePieces.Start(0, rand, x, z, Collections.emptyList(), config, biome);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 5, 6, 5, face);
            return new VillagePieces.House4Garden(start, 0, rand, bb, face);
        });
        factory.put("ViST", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            Biome biome = world.getBiome(new BlockPos(x, y, z));
            VillageConfig config = (VillageConfig) world.getChunkProvider().getChunkGenerator().getStructureConfig(biome, Feature.VILLAGE);
            if (config == null) config = new VillageConfig(0, VillagePieces.Type.OAK);
            VillagePieces.Start start = new VillagePieces.Start(0, rand, x, z, Collections.emptyList(), config, biome);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 5, 12, 9, face);
            return new VillagePieces.Church(start, 0, rand, bb, face);
        });
        factory.put("ViDF", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            Biome biome = world.getBiome(new BlockPos(x, y, z));
            VillageConfig config = (VillageConfig) world.getChunkProvider().getChunkGenerator().getStructureConfig(biome, Feature.VILLAGE);
            if (config == null) config = new VillageConfig(0, VillagePieces.Type.OAK);
            VillagePieces.Start start = new VillagePieces.Start(0, rand, x, z, Collections.emptyList(), config, biome);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 13, 4, 9, face);
            return new VillagePieces.Field1(start, 0, rand, bb, face);
        });
        factory.put("ViF", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            Biome biome = world.getBiome(new BlockPos(x, y, z));
            VillageConfig config = (VillageConfig) world.getChunkProvider().getChunkGenerator().getStructureConfig(biome, Feature.VILLAGE);
            if (config == null) config = new VillageConfig(0, VillagePieces.Type.OAK);
            VillagePieces.Start start = new VillagePieces.Start(0, rand, x, z, Collections.emptyList(), config, biome);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 7, 4, 9, face);
            return new VillagePieces.Field2(start, 0, rand, bb, face);
        });
        factory.put("ViL", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            Biome biome = world.getBiome(new BlockPos(x, y, z));
            VillageConfig config = (VillageConfig) world.getChunkProvider().getChunkGenerator().getStructureConfig(biome, Feature.VILLAGE);
            if (config == null) config = new VillageConfig(0, VillagePieces.Type.OAK);
            VillagePieces.Start start = new VillagePieces.Start(0, rand, x, z, Collections.emptyList(), config, biome);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 3, 4, 2, face);
            return new VillagePieces.Torch(start, 0, rand, bb, face);
        });
        factory.put("ViPH", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            Biome biome = world.getBiome(new BlockPos(x, y, z));
            VillageConfig config = (VillageConfig) world.getChunkProvider().getChunkGenerator().getStructureConfig(biome, Feature.VILLAGE);
            if (config == null) config = new VillageConfig(0, VillagePieces.Type.OAK);
            VillagePieces.Start start = new VillagePieces.Start(0, rand, x, z, Collections.emptyList(), config, biome);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 9, 7, 11, face);
            return new VillagePieces.Hall(start, 0, rand, bb, face);
        });
        factory.put("ViSmH", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            Biome biome = world.getBiome(new BlockPos(x, y, z));
            VillageConfig config = (VillageConfig) world.getChunkProvider().getChunkGenerator().getStructureConfig(biome, Feature.VILLAGE);
            if (config == null) config = new VillageConfig(0, VillagePieces.Type.OAK);
            VillagePieces.Start start = new VillagePieces.Start(0, rand, x, z, Collections.emptyList(), config, biome);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 4, 6, 5, face);
            return new VillagePieces.WoodHut(start, 0, rand, bb, face);
        });
        factory.put("ViSR", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            Biome biome = world.getBiome(new BlockPos(x, y, z));
            VillageConfig config = (VillageConfig) world.getChunkProvider().getChunkGenerator().getStructureConfig(biome, Feature.VILLAGE);
            if (config == null) config = new VillageConfig(0, VillagePieces.Type.OAK);
            VillagePieces.Start start = new VillagePieces.Start(0, rand, x, z, Collections.emptyList(), config, biome);
            MutableBoundingBox bb = VillagePieces.Path.findPieceBox(start, Collections.emptyList(), rand, x, y, z, face);
            return new VillagePieces.Path(start, 0, rand, bb, face);
        });
        factory.put("TeDP", (world, x, y, z, rand) -> new DesertPyramidPiece(rand, x, z));
        factory.put("TeJP", (world, x, y, z, rand) -> new JunglePyramidPiece(rand, x, z));
        factory.put("TeSH", (world, x, y, z, rand) -> new SwampHutPiece(rand, x, z));
        factory.put("SHStart", (world, x, y, z, rand) -> new StrongholdPieces.Stairs2(0, rand, x, z));
        factory.put("SHSD", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -7, 0, 5, 11, 5, face);
            return new StrongholdPieces.Stairs(0, rand, bb, face);
        });
        factory.put("SHCC", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -1, 0, 5, 5, 7, face);
            return new StrongholdPieces.ChestCorridor(0, rand, bb, face);
        });
        factory.put("SHFC", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -1, 0, 5, 5, rand.nextInt(3) + 1, face);
            return new StrongholdPieces.Corridor(0, rand, bb, face);
        });
        factory.put("SH5C", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, -4, -3, 0, 10, 9, 11, face);
            return new StrongholdPieces.Crossing(0, rand, bb, face);
        });
        factory.put("SHLT", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -1, 0, 5, 5, 5, face);
            return new StrongholdPieces.LeftTurn(0, rand, bb, face);
        });
        factory.put("SHRT", (world, x, y, z, rand) -> {  // same as SHLT
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -1, 0, 5, 5, 5, face);
            return new StrongholdPieces.LeftTurn(0, rand, bb, face);
        });
        factory.put("SHLi", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb;
            if (rand.nextInt(2) == 0)
                bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, -4, -1, 0, 14, 11, 15, face);
            else
                bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, -4, -1, 0, 14, 6, 15, face);
            return new StrongholdPieces.Library(0, rand, bb, face);
        });
        factory.put("SHPR", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, -4, -1, 0, 11, 8, 16, face);
            return new StrongholdPieces.PortalRoom(0, rand, bb, face);
        });
        factory.put("SHPH", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -1, 0, 9, 5, 11, face);
            return new StrongholdPieces.Prison(0, rand, bb, face);
        });
        factory.put("SHRC", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, -4, -1, 0, 11, 7, 11, face);
            return new StrongholdPieces.RoomCrossing(0, rand, bb, face);
        });
        factory.put("SHS", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -1, 0, 5, 5, 7, face);
            return new StrongholdPieces.Straight(0, rand, bb, face);
        });
        factory.put("SHSSD", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -7, 0, 5, 11, 8, face);
            return new StrongholdPieces.StairsStraight(0, rand, bb, face);
        });
        factory.put("NeStart", (world, x, y, z, rand) -> new FortressPieces.Start(rand, x, z));
        factory.put("NeBCr", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, -8, -3, 0, 19, 10, 19, face);
            return new FortressPieces.Crossing3(0, rand, bb, face);
        });
        factory.put("NeSCSC", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, 0, 0, 5, 7, 5, face);
            return new FortressPieces.Crossing2(0, rand, bb, face);
        });
        factory.put("NeRC", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, -2, 0, 0, 7, 9, 7, face);
            return new FortressPieces.Crossing(0, rand, bb, face);
        });
        factory.put("NeBS", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -3, 0, 5, 10, 19, face);
            return new FortressPieces.Straight(0, rand, bb, face);
        });
        factory.put("NeSCLT", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, 0, 0, 5, 7, 5, face);
            return new FortressPieces.Corridor(0, rand, bb, face);
        });
        factory.put("NeSCRT", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, 0, 0, 5, 7, 5, face);
            return new FortressPieces.Corridor2(0, rand, bb, face);
        });
        factory.put("NeCCS", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -7, 0, 5, 14, 10, face);
            return new FortressPieces.Corridor3(0, rand, bb, face);
        });
        factory.put("NeCTB", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, -3, 0, 0, 9, 7, 9, face);
            return new FortressPieces.Corridor4(0, rand, bb, face);
        });
        factory.put("NeSC", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, 0, 0, 5, 7, 5, face);
            return new FortressPieces.Corridor5(0, rand, bb, face);
        });
        factory.put("NeCE", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, -5, -3, 0, 13, 14, 13, face);
            return new FortressPieces.Entrance(0, rand, bb, face);
        });
        factory.put("NeCSR", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, -5, -3, 0, 13, 14, 13, face);
            return new FortressPieces.NetherStalkRoom(0, rand, bb, face);
        });
        factory.put("NeMT", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, -2, 0, 0, 7, 8, 9, face);
            return new FortressPieces.Throne(0, rand, bb, face);
        });
        factory.put("NeSR", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, -2, 0, 0, 7, 11, 7, face);
            return new FortressPieces.Stairs(0, rand, bb, face);
        });
        factory.put("NeBEF", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb = MutableBoundingBox.getComponentToAddBoundingBox(x, y, z, -1, -3, 0, 5, 10, 8, face);
            return new FortressPieces.End(0, rand, bb, face);
        });
        factory.put("OMB", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            OceanMonumentPieces.MonumentBuilding building = new OceanMonumentPieces.MonumentBuilding(rand, x, z, face);
            building.childPieces.clear();
            return building;
        });
        factory.put("OMCR", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            // int roomIndex = 10 + rand.nextInt(4);
            OceanMonumentPieces.RoomDefinition room = new OceanMonumentPieces.RoomDefinition(0);
            OceanMonumentPieces.MonumentCoreRoom piece = new OceanMonumentPieces.MonumentCoreRoom(face, room, rand);
            piece.getBoundingBox().offset(x, y, z);
            return piece;
        });
        factory.put("OMDXR", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            OceanMonumentPieces.RoomDefinition room = new OceanMonumentPieces.RoomDefinition(0);
            OceanMonumentPieces.RoomDefinition east = new OceanMonumentPieces.RoomDefinition(0);
            room.setConnection(EnumFacing.EAST, east);
            OceanMonumentPieces.DoubleXRoom piece = new OceanMonumentPieces.DoubleXRoom(face, room, rand);
            piece.getBoundingBox().offset(x, y, z);
            return piece;
        });
        factory.put("OMDXYR", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            OceanMonumentPieces.RoomDefinition room = new OceanMonumentPieces.RoomDefinition(0);
            OceanMonumentPieces.RoomDefinition east = new OceanMonumentPieces.RoomDefinition(0);
            OceanMonumentPieces.RoomDefinition up = new OceanMonumentPieces.RoomDefinition(0);
            OceanMonumentPieces.RoomDefinition eastUp = new OceanMonumentPieces.RoomDefinition(0);
            room.setConnection(EnumFacing.EAST, east);
            room.setConnection(EnumFacing.UP, up);
            east.setConnection(EnumFacing.UP, eastUp);
            OceanMonumentPieces.DoubleXYRoom piece = new OceanMonumentPieces.DoubleXYRoom(face, room, rand);
            piece.getBoundingBox().offset(x, y, z);
            return piece;
        });
        factory.put("OMDYR", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            OceanMonumentPieces.RoomDefinition room = new OceanMonumentPieces.RoomDefinition(0);
            OceanMonumentPieces.RoomDefinition up = new OceanMonumentPieces.RoomDefinition(0);
            room.setConnection(EnumFacing.UP, up);
            OceanMonumentPieces.DoubleYRoom piece = new OceanMonumentPieces.DoubleYRoom(face, room, rand);
            piece.getBoundingBox().offset(x, y, z);
            return piece;
        });
        factory.put("OMDYZR", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            OceanMonumentPieces.RoomDefinition room = new OceanMonumentPieces.RoomDefinition(0);
            OceanMonumentPieces.RoomDefinition north = new OceanMonumentPieces.RoomDefinition(0);
            OceanMonumentPieces.RoomDefinition up = new OceanMonumentPieces.RoomDefinition(0);
            OceanMonumentPieces.RoomDefinition northUp = new OceanMonumentPieces.RoomDefinition(0);
            room.setConnection(EnumFacing.NORTH, north);
            room.setConnection(EnumFacing.UP, up);
            north.setConnection(EnumFacing.UP, northUp);
            OceanMonumentPieces.DoubleYZRoom piece = new OceanMonumentPieces.DoubleYZRoom(face, room, rand);
            piece.getBoundingBox().offset(x, y, z);
            return piece;
        });
        factory.put("OMDZR", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            OceanMonumentPieces.RoomDefinition room = new OceanMonumentPieces.RoomDefinition(0);
            OceanMonumentPieces.RoomDefinition north = new OceanMonumentPieces.RoomDefinition(0);
            room.setConnection(EnumFacing.NORTH, north);
            OceanMonumentPieces.DoubleZRoom piece = new OceanMonumentPieces.DoubleZRoom(face, room, rand);
            piece.getBoundingBox().offset(x, y, z);
            return piece;
        });
        factory.put("OMEntry", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            OceanMonumentPieces.RoomDefinition room = new OceanMonumentPieces.RoomDefinition(0);
            OceanMonumentPieces.EntryRoom piece = new OceanMonumentPieces.EntryRoom(face, room);
            piece.getBoundingBox().offset(x, y, z);
            return piece;
        });
        factory.put("OMPenthouse", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            MutableBoundingBox bb = MutableBoundingBox.createProper(x, y, z, x + 13, y + 4, z + 13);
            return new OceanMonumentPieces.Penthouse(face, bb);
        });
        factory.put("OMSimple", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            OceanMonumentPieces.RoomDefinition room = new OceanMonumentPieces.RoomDefinition(0);
            OceanMonumentPieces.SimpleRoom piece = new OceanMonumentPieces.SimpleRoom(face, room, rand);
            piece.getBoundingBox().offset(x, y, z);
            return piece;
        });
        factory.put("OMSimpleT", (world, x, y, z, rand) -> {
            EnumFacing face = EnumFacing.Plane.HORIZONTAL.random(rand);
            OceanMonumentPieces.RoomDefinition room = new OceanMonumentPieces.RoomDefinition(0);
            OceanMonumentPieces.SimpleTopRoom piece = new OceanMonumentPieces.SimpleTopRoom(face, room, rand);
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
            return new EndCityPieces.CityTemplate(manager, template, new BlockPos(x, y, z), rotation, true);
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
        factory.put("Iglu", (world, x, y, z, rand) -> {
            TemplateManager manager = world.getSaveHandler().getStructureTemplateManager();
            BlockPos blockPos = new BlockPos(x, 90, z);
            Rotation rotation = Rotation.values()[rand.nextInt(Rotation.values().length)];
            String template = (new String[] {
                    "igloo/top",
                    "igloo/middle",
                    "igloo/bottom"
            })[rand.nextInt(3)];
            return new IglooPieces.Piece(manager, new ResourceLocation(template), blockPos, rotation, 0);
        });
        factory.put("Shipwreck", (world, x, y, z, rand) -> {
            TemplateManager manager = world.getSaveHandler().getStructureTemplateManager();
            BlockPos blockPos = new BlockPos(x, 90, z);
            Rotation rotation = Rotation.values()[rand.nextInt(Rotation.values().length)];
            Biome biome = world.getBiome(new BlockPos(x, y, z));
            ShipwreckConfig config = (ShipwreckConfig) world.getChunkProvider().getChunkGenerator().getStructureConfig(biome, Feature.SHIPWRECK);
            if (config == null) config = new ShipwreckConfig(false);
            String template = (new String[] {
                    "shipwreck/with_mast",
                    "shipwreck/upsidedown_full",
                    "shipwreck/upsidedown_fronthalf",
                    "shipwreck/upsidedown_backhalf",
                    "shipwreck/sideways_full",
                    "shipwreck/sideways_fronthalf",
                    "shipwreck/sideways_backhalf",
                    "shipwreck/rightsideup_full",
                    "shipwreck/rightsideup_fronthalf",
                    "shipwreck/rightsideup_backhalf",
                    "shipwreck/with_mast_degraded",
                    "shipwreck/upsidedown_full_degraded",
                    "shipwreck/upsidedown_fronthalf_degraded",
                    "shipwreck/upsidedown_backhalf_degraded",
                    "shipwreck/sideways_full_degraded",
                    "shipwreck/sideways_fronthalf_degraded",
                    "shipwreck/sideways_backhalf_degraded",
                    "shipwreck/rightsideup_full_degraded",
                    "shipwreck/rightsideup_fronthalf_degraded",
                    "shipwreck/rightsideup_backhalf_degraded"
            })[rand.nextInt(20)];
            return new ShipwreckPieces.Piece(manager, new ResourceLocation(template), blockPos, rotation, config.field_204753_a);
        });
        factory.put("ORP", (world, x, y, z, rand) -> {
            TemplateManager manager = world.getSaveHandler().getStructureTemplateManager();
            BlockPos blockPos = new BlockPos(x, 90, z);
            Rotation rotation = Rotation.values()[rand.nextInt(Rotation.values().length)];
            Biome biome = world.getBiome(new BlockPos(x, y, z));
            OceanRuinConfig config = (OceanRuinConfig) world.getChunkProvider().getChunkGenerator().getStructureConfig(biome, Feature.OCEAN_RUIN);
            if (config == null) config = new OceanRuinConfig(OceanRuinStructure.Type.COLD, 0.3F, 0.9F);
            String template = (new String[] {
                    "underwater_ruin/big_brick_1",
                    "underwater_ruin/big_brick_2",
                    "underwater_ruin/big_brick_3",
                    "underwater_ruin/big_brick_8",
                    "underwater_ruin/big_cracked_1",
                    "underwater_ruin/big_cracked_2",
                    "underwater_ruin/big_cracked_3",
                    "underwater_ruin/big_cracked_8",
                    "underwater_ruin/big_mossy_1",
                    "underwater_ruin/big_mossy_2",
                    "underwater_ruin/big_mossy_3",
                    "underwater_ruin/big_mossy_8",
                    "underwater_ruin/big_warm_4",
                    "underwater_ruin/big_warm_5",
                    "underwater_ruin/big_warm_6",
                    "underwater_ruin/big_warm_7",
                    "underwater_ruin/brick_1",
                    "underwater_ruin/brick_2",
                    "underwater_ruin/brick_3",
                    "underwater_ruin/brick_4",
                    "underwater_ruin/brick_5",
                    "underwater_ruin/brick_6",
                    "underwater_ruin/brick_7",
                    "underwater_ruin/brick_8",
                    "underwater_ruin/cracked_1",
                    "underwater_ruin/cracked_2",
                    "underwater_ruin/cracked_3",
                    "underwater_ruin/cracked_4",
                    "underwater_ruin/cracked_5",
                    "underwater_ruin/cracked_6",
                    "underwater_ruin/cracked_7",
                    "underwater_ruin/cracked_8",
                    "underwater_ruin/mossy_1",
                    "underwater_ruin/mossy_2",
                    "underwater_ruin/mossy_3",
                    "underwater_ruin/mossy_4",
                    "underwater_ruin/mossy_5",
                    "underwater_ruin/mossy_6",
                    "underwater_ruin/mossy_7",
                    "underwater_ruin/mossy_8",
                    "underwater_ruin/warm_1",
                    "underwater_ruin/warm_2",
                    "underwater_ruin/warm_3",
                    "underwater_ruin/warm_4",
                    "underwater_ruin/warm_5",
                    "underwater_ruin/warm_6",
                    "underwater_ruin/warm_7",
                    "underwater_ruin/warm_8"
            })[rand.nextInt(48)];
            return new OceanRuinPieces.Piece(manager, new ResourceLocation(template), blockPos, rotation, rand.nextFloat() * 0.5F + 0.5F, config.field_204031_a, rand.nextBoolean());
        });
        factory.put("BTP", (world, x, y, z, rand) -> new BuriedTreasurePieces.Piece(new BlockPos(x, y, z)));
    }

    public static Set<String> getNameSet() {
        return factory.keySet();
    }
    
    public static StructurePiece getPiece(String id, World world, int x, int y, int z, SharedSeedRandom rand) {
        return factory.get(id).getPiece(world, x, y, z, rand);
    }

}
