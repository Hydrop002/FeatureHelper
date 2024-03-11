package org.utm.featurehelper.command;

import java.util.*;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.NBTCompoundTagArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;
import org.utm.featurehelper.command.arguments.ConfiguredStructureArgument;
import org.utm.featurehelper.command.arguments.PieceArgument;
import org.utm.featurehelper.command.arguments.StructureArgument;
import org.utm.featurehelper.network.MessageBoundingBox;
import org.utm.featurehelper.network.MessageRenderControl;
import org.utm.featurehelper.network.NetworkManager;
import org.utm.featurehelper.feature.PieceFactory;

import javax.annotation.Nullable;

public class CommandStructure {  // todo Mixin

    private static final SimpleCommandExceptionType ERROR_START_FAILED = new SimpleCommandExceptionType(
            new TranslationTextComponent("commands.structure.start.failed")
    );
    private static final SimpleCommandExceptionType ERROR_PIECE_FAILED = new SimpleCommandExceptionType(
            new TranslationTextComponent("commands.structure.piece.failed")
    );
    private static final SimpleCommandExceptionType ERROR_COMPLETED = new SimpleCommandExceptionType(
            new TranslationTextComponent("commands.structure.continue.complete")
    );
    private static final SimpleCommandExceptionType ERROR_DIMENSION = new SimpleCommandExceptionType(
            new TranslationTextComponent("commands.structure.continue.failed")
    );

    private static final List<MutableBoundingBox> bbList = new ArrayList<>();
    private static MutableBoundingBox lastBB;
    private static ServerWorld startWorld;
    private static ServerWorld lastWorld;

    private static Iterator<StructurePiece> it;
    private static StructureStart<?> start;

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("structure")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("start")
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .then(Commands.literal("configured")
                                        .then(Commands.argument("configuredStructureName", ConfiguredStructureArgument.configuredStructure())
                                                .executes(context -> generateStructure(
                                                        context.getSource(),
                                                        BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                                        ConfiguredStructureArgument.getConfiguredStructure(context, "configuredStructureName"),
                                                        false,
                                                        null))
                                                .then(Commands.argument("debug", BoolArgumentType.bool())
                                                        .executes(context -> generateStructure(
                                                                context.getSource(),
                                                                BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                                                ConfiguredStructureArgument.getConfiguredStructure(context, "configuredStructureName"),
                                                                BoolArgumentType.getBool(context, "debug"),
                                                                null
                                                        ))
                                                        .then(Commands.argument("dataTag", NBTCompoundTagArgument.compoundTag())
                                                                .executes(context -> generateStructure(
                                                                        context.getSource(),
                                                                        BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                                                        ConfiguredStructureArgument.getConfiguredStructure(context, "configuredStructureName"),
                                                                        BoolArgumentType.getBool(context, "debug"),
                                                                        NBTCompoundTagArgument.getCompoundTag(context, "dataTag")
                                                                ))))))
                                .then(Commands.literal("unconfigured")
                                        .then(Commands.argument("structureName", StructureArgument.structure())
                                                .executes(context -> generateStructure(
                                                        context.getSource(),
                                                        BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                                        StructureArgument.getStructure(context, "structureName"),
                                                        false,
                                                        null))
                                                .then(Commands.argument("debug", BoolArgumentType.bool())
                                                        .executes(context -> generateStructure(
                                                                context.getSource(),
                                                                BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                                                StructureArgument.getStructure(context, "structureName"),
                                                                BoolArgumentType.getBool(context, "debug"),
                                                                null
                                                        ))
                                                        .then(Commands.argument("dataTag", NBTCompoundTagArgument.compoundTag())
                                                                .executes(context -> generateStructure(
                                                                        context.getSource(),
                                                                        BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                                                        StructureArgument.getStructure(context, "structureName"),
                                                                        BoolArgumentType.getBool(context, "debug"),
                                                                        NBTCompoundTagArgument.getCompoundTag(context, "dataTag")
                                                                ))))))))
                /*.then(Commands.literal("piece")
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .then(Commands.argument("pieceName", PieceArgument.piece())
                                        .executes(context -> generatePiece(
                                                context.getSource(),
                                                BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                                PieceArgument.getPiece(context, "pieceName"),
                                                null
                                        ))
                                        .then(Commands.argument("dataTag", NBTCompoundTagArgument.compoundTag())
                                                .executes(context -> generatePiece(
                                                        context.getSource(),
                                                        BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                                        PieceArgument.getPiece(context, "pieceName"),
                                                        NBTCompoundTagArgument.getCompoundTag(context, "dataTag")
                                                ))))))*/
                .then(Commands.literal("continue")
                        .executes(context -> continueGenerate(context.getSource())))
                .then(Commands.literal("bb")
                        .then(Commands.literal("hide")
                                .executes(context -> renderBoundingBox(context.getSource(), false)))
                        .then(Commands.literal("show")
                                .executes(context -> renderBoundingBox(context.getSource(), true)))));
    }

    private static <FC extends IFeatureConfig, F extends Structure<FC>> int generateStructure(CommandSource source, BlockPos blockPos, StructureFeature<FC, F> structureFeature, boolean debug, @Nullable CompoundNBT dataTag) throws CommandSyntaxException {
        ServerWorld world = source.getLevel();
        SharedSeedRandom rand = new SharedSeedRandom();
        ChunkPos chunkPos = new ChunkPos(blockPos);
        DynamicRegistries registries = world.registryAccess();
        ChunkGenerator generator = world.getChunkSource().getGenerator();
        TemplateManager templateManager = world.getStructureManager();
        StructureManager structureManager = world.structureFeatureManager();
        Biome biome = world.getBiome(blockPos);
        StructureStart<FC> structureStart = structureFeature.feature.getStartFactory().create(structureFeature.feature, chunkPos.x, chunkPos.z, MutableBoundingBox.getUnknownBox(), 0, rand.nextLong());
        structureStart.generatePieces(registries, generator, templateManager, chunkPos.x, chunkPos.z, biome, structureFeature.config);

        if (!structureStart.isValid())
            throw ERROR_START_FAILED.create();

        start = structureStart;
        startWorld = world;
        lastWorld = world;

        if (dataTag != null) {
            CompoundNBT compound = start.createTag(chunkPos.x, chunkPos.z);
            for (String key : dataTag.getAllKeys()) {
                INBT value = dataTag.get(key);
                compound.put(key, value);
            }
            start = Structure.loadStaticStart(templateManager, compound, rand.nextLong());
        }

        MutableBoundingBox bb = start.getBoundingBox();
        MutableBoundingBox newBB = new MutableBoundingBox(bb.x0 - 1, bb.z0 - 1, bb.x1 + 1, bb.z1 + 1);
        clearBoundingBox();
        if (debug) {
            it = start.getPieces().iterator();
            if (it.hasNext()) {
                StructurePiece piece = it.next();
                if (piece.getBoundingBox().intersects(newBB)) {
                    Vector3i center = piece.getBoundingBox().getCenter();
                    BlockPos pos = new BlockPos(center.getX(), piece.getBoundingBox().y0, center.getZ());
                    if (piece instanceof OceanMonumentPieces.MonumentBuilding) {
                        OceanMonumentPieces.MonumentBuilding monument = (OceanMonumentPieces.MonumentBuilding) piece;
                        ArrayList<OceanMonumentPieces.Piece> rooms = (ArrayList<OceanMonumentPieces.Piece>) monument.childPieces;
                        ArrayList<StructurePiece> roomsCopy = (ArrayList<StructurePiece>) rooms.clone();
                        rooms.clear();
                        piece.postProcess(world, structureManager, generator, rand, newBB, chunkPos, pos);
                        it = roomsCopy.iterator();
                    } else {
                        piece.postProcess(world, structureManager, generator, rand, newBB, chunkPos, pos);
                    }
                    addBoundingBox(piece.getBoundingBox());
                }
            }
        } else {
            it = null;
            start.placeInChunk(world, structureManager, generator, rand, newBB, chunkPos);
        }
        setLastBoundingBox(start.getBoundingBox());
        sendMessage(world);
        source.sendSuccess(new TranslationTextComponent("commands.structure.start.success", blockPos.getX(), blockPos.getY(), blockPos.getZ()), true);
        return 1;
    }

    private static int generatePiece(CommandSource source, BlockPos blockPos, String pieceName, @Nullable CompoundNBT dataTag) throws CommandSyntaxException {
        ServerWorld world = source.getLevel();
        SharedSeedRandom rand = new SharedSeedRandom();
        ChunkPos chunkPos = new ChunkPos(blockPos);
        TemplateManager templateManager = world.getStructureManager();
        StructureManager structureManager = world.structureFeatureManager();
        ChunkGenerator generator = world.getChunkSource().getGenerator();
        StructurePiece piece = PieceFactory.getPiece(pieceName, world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), rand);

        lastWorld = world;

        if (dataTag != null) {
            CompoundNBT compound = piece.createTag();
            for (String key : dataTag.getAllKeys()) {
                INBT value = dataTag.get(key);
                compound.put(key, value);
            }
            IStructurePieceType pieceType = Registry.STRUCTURE_PIECE.get(new ResourceLocation(pieceName.toLowerCase(Locale.ROOT)));
            piece = pieceType.load(templateManager, compound);
        }

        MutableBoundingBox bb = piece.getBoundingBox();
        MutableBoundingBox newBB = new MutableBoundingBox(bb.x0 - 1, bb.z0 - 1, bb.x1 + 1, bb.z1 + 1);
        Vector3i center = bb.getCenter();
        BlockPos pos = new BlockPos(center.getX(), bb.y0, center.getZ());
        if (!piece.postProcess(world, structureManager, generator, rand, newBB, chunkPos, pos))
            throw ERROR_PIECE_FAILED.create();
        setLastBoundingBox(piece.getBoundingBox());
        sendMessage(world);
        source.sendSuccess(new TranslationTextComponent("commands.structure.piece.success", bb.x0, bb.y0, bb.z0, bb.x1, bb.y1, bb.z1), true);
        return 1;
    }

    private static int continueGenerate(CommandSource source) throws CommandSyntaxException {
        if (start == null)
            throw ERROR_COMPLETED.create();

        ServerWorld world = source.getLevel();
        if (world != startWorld)
            throw ERROR_DIMENSION.create();

        SharedSeedRandom rand = new SharedSeedRandom();
        ChunkPos chunkPos = new ChunkPos(start.getChunkX(), start.getChunkZ());
        StructureManager structureManager = world.structureFeatureManager();
        ChunkGenerator generator = world.getChunkSource().getGenerator();

        lastWorld = world;
        MutableBoundingBox bb = start.getBoundingBox();
        MutableBoundingBox newBB = new MutableBoundingBox(bb.x0 - 1, bb.z0 - 1, bb.x1 + 1, bb.z1 + 1);
        setLastBoundingBox(bb);
        if (it != null && it.hasNext()) {
            StructurePiece piece = it.next();
            if (piece.getBoundingBox().intersects(newBB)) {
                StructurePiece startPiece = start.getPieces().get(0);
                Vector3i center = startPiece.getBoundingBox().getCenter();
                BlockPos pos = new BlockPos(center.getX(), startPiece.getBoundingBox().y0, center.getZ());
                piece.postProcess(world, structureManager, generator, rand, newBB, chunkPos, pos);
                addBoundingBox(piece.getBoundingBox());
                sendMessage(world);
            }
            source.sendSuccess(new TranslationTextComponent("commands.structure.continue.success"), false);
        } else {
            if (canAddBoundingBox()) {
                addBoundingBox(null);
                sendMessage(world);
            }
            throw ERROR_COMPLETED.create();
        }
        return 1;
    }

    private static int renderBoundingBox(CommandSource source, boolean isRender) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        if (isRender) {
            showBoundingBox(player);
            source.sendSuccess(new TranslationTextComponent("commands.structure.bb.show.success"), false);
        } else {
            hideBoundingBox(player);
            source.sendSuccess(new TranslationTextComponent("commands.structure.bb.hide.success"), false);
        }
        return 1;
    }

    public static boolean canAddBoundingBox() {
        return bbList.isEmpty() || bbList.get(bbList.size() - 1) != null;
    }

    public static void clearBoundingBox() {
        bbList.clear();
        lastBB = null;
    }

    public static void addBoundingBox(@Nullable MutableBoundingBox bb) {
        bbList.add(bb);
    }

    public static void setLastBoundingBox(MutableBoundingBox bb) {
        lastBB = bb;
    }

    public static void sendMessage(ServerWorld world) {
        ListNBT list = new ListNBT();
        for (MutableBoundingBox bb : bbList) {
            if (bb == null)
                list.add(new IntArrayNBT(new int[0]));
            else
                list.add(bb.createTag());
        }
        MessageBoundingBox message = new MessageBoundingBox();
        message.compound = new CompoundNBT();
        if (world == startWorld)
            message.compound.put("BBList", list);
        else
            message.compound.put("BBList", new ListNBT());
        if (world == lastWorld && lastBB != null)
            message.compound.put("lastBB", lastBB.createTag());
        NetworkManager.instance.send(PacketDistributor.DIMENSION.with(world::dimension), message);
    }

    public static void sendMessageToPlayer(ServerPlayerEntity player, ServerWorld world) {
        ListNBT list = new ListNBT();
        for (MutableBoundingBox bb : bbList) {
            if (bb == null)
                list.add(new IntArrayNBT(new int[0]));
            else
                list.add(bb.createTag());
        }
        MessageBoundingBox message = new MessageBoundingBox();
        message.compound = new CompoundNBT();
        if (world == startWorld)
            message.compound.put("BBList", list);
        else
            message.compound.put("BBList", new ListNBT());
        if (world == lastWorld && lastBB != null)
            message.compound.put("lastBB", lastBB.createTag());
        NetworkManager.instance.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void hideBoundingBox(ServerPlayerEntity player) {
        MessageRenderControl message = new MessageRenderControl();
        message.renderType = 0;
        message.isRender = false;
        NetworkManager.instance.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void showBoundingBox(ServerPlayerEntity player) {
        MessageRenderControl message = new MessageRenderControl();
        message.renderType = 0;
        message.isRender = true;
        NetworkManager.instance.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

}
