package org.utm.featurehelper.command;

import java.util.*;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.NBTArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.*;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.OceanMonumentPieces;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraftforge.fml.network.PacketDistributor;
import org.utm.featurehelper.command.arguments.PieceArgument;
import org.utm.featurehelper.command.arguments.StartArgument;
import org.utm.featurehelper.network.MessageBoundingBox;
import org.utm.featurehelper.network.MessageRenderControl;
import org.utm.featurehelper.network.NetworkManager;
import org.utm.featurehelper.feature.PieceFactory;
import org.utm.featurehelper.feature.StartFactory;

import javax.annotation.Nullable;

public class CommandStructure {

    private static final SimpleCommandExceptionType START_FAILED_EXCEPTION = new SimpleCommandExceptionType(
            new TextComponentTranslation("commands.structure.start.failed")
    );
    private static final SimpleCommandExceptionType PIECE_FAILED_EXCEPTION = new SimpleCommandExceptionType(
            new TextComponentTranslation("commands.structure.piece.failed")
    );
    private static final SimpleCommandExceptionType COMPLETE_EXCEPTION = new SimpleCommandExceptionType(
            new TextComponentTranslation("commands.structure.continue.complete")
    );
    private static final SimpleCommandExceptionType DIMENSION_EXCEPTION = new SimpleCommandExceptionType(
            new TextComponentTranslation("commands.structure.continue.failed")
    );

    private static final List<MutableBoundingBox> bbList = new ArrayList<>();
    private static MutableBoundingBox lastBB;
    private static World startWorld;
    private static World lastWorld;

    private static Iterator<StructurePiece> it;
    private static StructureStart start;

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("structure")
                .requires(source -> source.hasPermissionLevel(2))
                .then(Commands.literal("start")
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .then(Commands.argument("startName", StartArgument.start())
                                        .executes(context -> generateStart(
                                                context.getSource(),
                                                BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                                StartArgument.getStart(context, "startName"),
                                                false,
                                                null))
                                        .then(Commands.argument("debug", BoolArgumentType.bool())
                                                .executes(context -> generateStart(
                                                        context.getSource(),
                                                        BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                                        StartArgument.getStart(context, "startName"),
                                                        BoolArgumentType.getBool(context, "debug"),
                                                        null
                                                ))
                                                .then(Commands.argument("dataTag", NBTArgument.nbt())
                                                        .executes(context -> generateStart(
                                                                context.getSource(),
                                                                BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                                                StartArgument.getStart(context, "startName"),
                                                                BoolArgumentType.getBool(context, "debug"),
                                                                NBTArgument.func_197130_a(context, "dataTag")
                                                        )))))))
                .then(Commands.literal("piece")
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .then(Commands.argument("pieceName", PieceArgument.piece())
                                        .executes(context -> generatePiece(
                                                context.getSource(),
                                                BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                                PieceArgument.getPiece(context, "pieceName"),
                                                null
                                        ))
                                        .then(Commands.argument("dataTag", NBTArgument.nbt())
                                                .executes(context -> generatePiece(
                                                        context.getSource(),
                                                        BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                                        PieceArgument.getPiece(context, "pieceName"),
                                                        NBTArgument.func_197130_a(context, "dataTag")
                                                ))))))
                .then(Commands.literal("continue")
                        .executes(context -> continueGenerate(context.getSource())))
                .then(Commands.literal("bb")
                        .then(Commands.literal("hide")
                                .executes(context -> renderBoundingBox(context.getSource(), false)))
                        .then(Commands.literal("show")
                                .executes(context -> renderBoundingBox(context.getSource(), true)))));
    }

    private static int generateStart(CommandSource source, BlockPos blockPos, String startName, boolean debug, @Nullable NBTTagCompound dataTag) throws CommandSyntaxException {
        World world = source.getWorld();
        SharedSeedRandom rand = new SharedSeedRandom();

        start = StartFactory.getStart(startName, world, blockPos.getX() >> 4, blockPos.getZ() >> 4, rand);
        startWorld = world;
        lastWorld = world;

        if (!start.isSizeableStructure())
            throw START_FAILED_EXCEPTION.create();

        if (dataTag != null) {
            NBTTagCompound compound = start.write(blockPos.getX() >> 4, blockPos.getZ() >> 4);
            for (String key : dataTag.keySet()) {
                INBTBase value = dataTag.getTag(key);
                compound.setTag(key, value);
            }
            start.getComponents().clear();
            start.read(world, compound);
        }

        MutableBoundingBox bb = start.getBoundingBox();
        MutableBoundingBox newBB = new MutableBoundingBox(bb.minX - 1, bb.minZ - 1, bb.maxX + 1, bb.maxZ + 1);
        clearBoundingBox();
        if (debug) {
            it = start.getComponents().iterator();
            if (it.hasNext()) {
                StructurePiece piece = it.next();
                if (piece.getBoundingBox().intersectsWith(newBB)) {
                    if (piece instanceof OceanMonumentPieces.MonumentBuilding) {
                        OceanMonumentPieces.MonumentBuilding monument = (OceanMonumentPieces.MonumentBuilding) piece;
                        ArrayList<OceanMonumentPieces.Piece> rooms = (ArrayList<OceanMonumentPieces.Piece>) monument.childPieces;
                        ArrayList<StructurePiece> roomsCopy = (ArrayList<StructurePiece>) rooms.clone();
                        rooms.clear();
                        piece.addComponentParts(world, rand, newBB, new ChunkPos(start.getChunkPosX(), start.getChunkPosZ()));
                        it = roomsCopy.iterator();
                    } else {
                        piece.addComponentParts(world, rand, newBB, new ChunkPos(start.getChunkPosX(), start.getChunkPosZ()));
                    }
                    addBoundingBox(piece.getBoundingBox());
                }
            }
        } else {
            it = null;
            start.generateStructure(world, rand, newBB, new ChunkPos(start.getChunkPosX(), start.getChunkPosZ()));
        }
        setLastBoundingBox(start.getBoundingBox());
        sendMessage(world);
        source.sendFeedback(new TextComponentTranslation("commands.structure.start.success", blockPos.getX(), blockPos.getY(), blockPos.getZ()), true);
        return 1;
    }

    private static int generatePiece(CommandSource source, BlockPos blockPos, String pieceName, @Nullable NBTTagCompound dataTag) throws CommandSyntaxException {
        World world = source.getWorld();
        SharedSeedRandom rand = new SharedSeedRandom();

        StructurePiece piece = PieceFactory.getPiece(pieceName, world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), rand);
        lastWorld = world;

        if (dataTag != null) {
            NBTTagCompound compound = piece.createStructureBaseNBT();
            for (String key : dataTag.keySet()) {
                INBTBase value = dataTag.getTag(key);
                compound.setTag(key, value);
            }
            piece.readStructureBaseNBT(world, compound);
        }

        MutableBoundingBox bb = piece.getBoundingBox();
        MutableBoundingBox newBB = new MutableBoundingBox(bb.minX - 1, bb.minZ - 1, bb.maxX + 1, bb.maxZ + 1);
        if (!piece.addComponentParts(world, rand, newBB, new ChunkPos(blockPos)))
            throw PIECE_FAILED_EXCEPTION.create();
        setLastBoundingBox(piece.getBoundingBox());
        sendMessage(world);
        source.sendFeedback(new TextComponentTranslation("commands.structure.piece.success", bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ), true);
        return 1;
    }

    private static int continueGenerate(CommandSource source) throws CommandSyntaxException {
        if (start == null)
            throw COMPLETE_EXCEPTION.create();

        World world = source.getWorld();
        if (world != startWorld)
            throw DIMENSION_EXCEPTION.create();

        lastWorld = world;
        MutableBoundingBox bb = start.getBoundingBox();
        MutableBoundingBox newBB = new MutableBoundingBox(bb.minX - 1, bb.minZ - 1, bb.maxX + 1, bb.maxZ + 1);
        setLastBoundingBox(bb);
        if (it != null && it.hasNext()) {
            StructurePiece piece = it.next();
            if (piece.getBoundingBox().intersectsWith(newBB)) {
                piece.addComponentParts(world, new Random(), newBB, new ChunkPos(start.getChunkPosX(), start.getChunkPosZ()));
                addBoundingBox(piece.getBoundingBox());
                sendMessage(world);
            }
            source.sendFeedback(new TextComponentTranslation("commands.structure.continue.success"), false);
        } else {
            if (canAddBoundingBox()) {
                addBoundingBox(null);
                sendMessage(world);
            }
            throw COMPLETE_EXCEPTION.create();
        }
        return 1;
    }

    private static int renderBoundingBox(CommandSource source, boolean isRender) throws CommandSyntaxException {
        EntityPlayerMP player = source.asPlayer();
        if (isRender) {
            showBoundingBox(player);
            source.sendFeedback(new TextComponentTranslation("commands.structure.bb.show.success"), false);
        } else {
            hideBoundingBox(player);
            source.sendFeedback(new TextComponentTranslation("commands.structure.bb.hide.success"), false);
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

    public static void sendMessage(World world) {
        NBTTagList list = new NBTTagList();
        for (MutableBoundingBox bb : bbList) {
            if (bb == null)
                list.add(new NBTTagIntArray(new int[0]));
            else
                list.add(bb.toNBTTagIntArray());
        }
        MessageBoundingBox message = new MessageBoundingBox();
        message.compound = new NBTTagCompound();
        if (world == startWorld)
            message.compound.setTag("BBList", list);
        else
            message.compound.setTag("BBList", new NBTTagList());
        if (world == lastWorld && lastBB != null)
            message.compound.setTag("lastBB", lastBB.toNBTTagIntArray());
        NetworkManager.instance.send(PacketDistributor.DIMENSION.with(() -> world.getDimension().getType()), message);
    }

    public static void sendMessageToPlayer(EntityPlayerMP player, World world) {
        NBTTagList list = new NBTTagList();
        for (MutableBoundingBox bb : bbList) {
            if (bb == null)
                list.add(new NBTTagIntArray(new int[0]));
            else
                list.add(bb.toNBTTagIntArray());
        }
        MessageBoundingBox message = new MessageBoundingBox();
        message.compound = new NBTTagCompound();
        if (world == startWorld)
            message.compound.setTag("BBList", list);
        else
            message.compound.setTag("BBList", new NBTTagList());
        if (world == lastWorld && lastBB != null)
            message.compound.setTag("lastBB", lastBB.toNBTTagIntArray());
        NetworkManager.instance.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void hideBoundingBox(EntityPlayerMP player) {
        MessageRenderControl message = new MessageRenderControl();
        message.renderType = 0;
        message.isRender = false;
        NetworkManager.instance.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void showBoundingBox(EntityPlayerMP player) {
        MessageRenderControl message = new MessageRenderControl();
        message.renderType = 0;
        message.isRender = true;
        NetworkManager.instance.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

}
