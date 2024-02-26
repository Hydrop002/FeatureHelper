package org.utm.featurehelper.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.LocationPart;
import net.minecraft.command.arguments.RotationArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import org.utm.featurehelper.feature.ICarverPatcher;
import org.utm.featurehelper.feature.patch.*;
import org.utm.featurehelper.network.MessageCarveTrail;
import org.utm.featurehelper.network.MessageRenderControl;
import org.utm.featurehelper.network.NetworkManager;

import javax.annotation.Nullable;
import java.util.*;

public class CommandCarve {

    private static final SimpleCommandExceptionType COMPLETE_EXCEPTION = new SimpleCommandExceptionType(
            new TextComponentTranslation("commands.carve.continue.complete")
    );
    private static final SimpleCommandExceptionType DIMENSION_EXCEPTION = new SimpleCommandExceptionType(
            new TextComponentTranslation("commands.carve.continue.failed")
    );

    private static final SuggestionProvider<CommandSource> CAVE_ROT_PROVIDER = (context, builder) -> {
        SharedSeedRandom rand = new SharedSeedRandom();
        float yaw = rand.nextFloat() * 360;
        float pitch = (rand.nextFloat() - 0.5F) * 45F / (float) Math.PI;
        String remain = builder.getRemaining();
        if (remain == null || remain.isEmpty()) {
            builder.suggest("~");
            builder.suggest("~ ~");
            builder.suggest(String.valueOf(yaw));
            builder.suggest(yaw + " " + pitch);
        } else {
            String[] arr = remain.split(" ");
            try {
                LocationPart.func_197308_a(new StringReader(arr[0]), false);
                if (arr.length == 1) {
                    builder.suggest(arr[0] + " ~");
                    builder.suggest(arr[0] + " " + pitch);
                }
            } catch (CommandSyntaxException ignored) {}
        }
        return builder.buildFuture();
    };
    private static final SuggestionProvider<CommandSource> CAVE_RADIUS_PROVIDER = (context, builder) -> {
        String remain = builder.getRemaining();
        if (remain == null || remain.isEmpty()) {
            SharedSeedRandom rand = new SharedSeedRandom();
            float radius = rand.nextFloat() * 2F + rand.nextFloat();
            if (rand.nextInt(10) == 0)
                radius *= rand.nextFloat() * rand.nextFloat() * 3 + 1;
            builder.suggest(String.valueOf(radius));
        }
        return builder.buildFuture();
    };
    private static final SuggestionProvider<CommandSource> CAVE_DEPTH_PROVIDER = (context, builder) -> {
        String remain = builder.getRemaining();
        if (remain == null || remain.isEmpty()) {
            SharedSeedRandom rand = new SharedSeedRandom();
            int depth = 112 - rand.nextInt(28);
            builder.suggest(String.valueOf(depth));
        }
        return builder.buildFuture();
    };
    private static final SuggestionProvider<CommandSource> ROOM_RADIUS_PROVIDER = (context, builder) -> {
        String remain = builder.getRemaining();
        if (remain == null || remain.isEmpty()) {
            SharedSeedRandom rand = new SharedSeedRandom();
            float radius = 1 + rand.nextFloat() * 6;
            builder.suggest(String.valueOf(radius));
        }
        return builder.buildFuture();
    };
    private static final SuggestionProvider<CommandSource> CANYON_RADIUS_PROVIDER = (context, builder) -> {
        String remain = builder.getRemaining();
        if (remain == null || remain.isEmpty()) {
            SharedSeedRandom rand = new SharedSeedRandom();
            float radius = (rand.nextFloat() * 2F + rand.nextFloat()) * 2F;
            builder.suggest(String.valueOf(radius));
        }
        return builder.buildFuture();
    };

    private static World world;
    private static final List<ICarverPatcher> list = new ArrayList<>();
    private static final List<List<Vec3d>> posList = new ArrayList<>();

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("carve")
                .requires(source -> source.hasPermissionLevel(2))
                .then(Commands.literal("start")
                        .then(Commands.argument("pos", Vec3Argument.vec3())
                                .then(Commands.literal("cave")
                                        .then(Commands.literal("tunnel")
                                                .executes(context -> carveCaveTunnel(
                                                        context.getSource(),
                                                        Vec3Argument.getVec3(context, "pos"),
                                                        null, null, null, false
                                                ))
                                                .then(Commands.argument("rot", RotationArgument.rotation())
                                                        .suggests(CAVE_ROT_PROVIDER)
                                                        .executes(context -> carveCaveTunnel(
                                                                context.getSource(),
                                                                Vec3Argument.getVec3(context, "pos"),
                                                                RotationArgument.getRotation(context, "rot").getRotation(context.getSource()),
                                                                null, null, false
                                                        ))
                                                        .then(Commands.argument("radius", FloatArgumentType.floatArg(0.0F))
                                                                .suggests(CAVE_RADIUS_PROVIDER)
                                                                .executes(context -> carveCaveTunnel(
                                                                        context.getSource(),
                                                                        Vec3Argument.getVec3(context, "pos"),
                                                                        RotationArgument.getRotation(context, "rot").getRotation(context.getSource()),
                                                                        FloatArgumentType.getFloat(context, "radius"),
                                                                        null, false
                                                                ))
                                                                .then(Commands.argument("depth", IntegerArgumentType.integer(0))
                                                                        .suggests(CAVE_DEPTH_PROVIDER)
                                                                        .executes(context -> carveCaveTunnel(
                                                                                context.getSource(),
                                                                                Vec3Argument.getVec3(context, "pos"),
                                                                                RotationArgument.getRotation(context, "rot").getRotation(context.getSource()),
                                                                                FloatArgumentType.getFloat(context, "radius"),
                                                                                IntegerArgumentType.getInteger(context, "depth"),
                                                                                false
                                                                        ))
                                                                        .then(Commands.argument("debug", BoolArgumentType.bool())
                                                                                .executes(context -> carveCaveTunnel(
                                                                                        context.getSource(),
                                                                                        Vec3Argument.getVec3(context, "pos"),
                                                                                        RotationArgument.getRotation(context, "rot").getRotation(context.getSource()),
                                                                                        FloatArgumentType.getFloat(context, "radius"),
                                                                                        IntegerArgumentType.getInteger(context, "depth"),
                                                                                        BoolArgumentType.getBool(context, "debug")
                                                                                )))))))
                                        .then(Commands.literal("room")
                                                .executes(context -> carveCaveRoom(
                                                        context.getSource(),
                                                        Vec3Argument.getVec3(context, "pos"),
                                                        null
                                                ))
                                                .then(Commands.argument("radius", FloatArgumentType.floatArg(0.0F))
                                                        .suggests(ROOM_RADIUS_PROVIDER)
                                                        .executes(context -> carveCaveRoom(
                                                                context.getSource(),
                                                                Vec3Argument.getVec3(context, "pos"),
                                                                FloatArgumentType.getFloat(context, "radius")
                                                        )))))
                                .then(Commands.literal("canyon")
                                        .then(Commands.literal("tunnel")
                                                .executes(context -> carveCanyonTunnel(
                                                        context.getSource(),
                                                        Vec3Argument.getVec3(context, "pos"),
                                                        null, null, null, false
                                                ))
                                                .then(Commands.argument("rot", RotationArgument.rotation())
                                                        .suggests(CAVE_ROT_PROVIDER)
                                                        .executes(context -> carveCanyonTunnel(
                                                                context.getSource(),
                                                                Vec3Argument.getVec3(context, "pos"),
                                                                RotationArgument.getRotation(context, "rot").getRotation(context.getSource()),
                                                                null, null, false
                                                        ))
                                                        .then(Commands.argument("radius", FloatArgumentType.floatArg(0.0F))
                                                                .suggests(CANYON_RADIUS_PROVIDER)
                                                                .executes(context -> carveCanyonTunnel(
                                                                        context.getSource(),
                                                                        Vec3Argument.getVec3(context, "pos"),
                                                                        RotationArgument.getRotation(context, "rot").getRotation(context.getSource()),
                                                                        FloatArgumentType.getFloat(context, "radius"),
                                                                        null, false
                                                                ))
                                                                .then(Commands.argument("depth", IntegerArgumentType.integer(0))
                                                                        .suggests(CAVE_DEPTH_PROVIDER)
                                                                        .executes(context -> carveCanyonTunnel(
                                                                                context.getSource(),
                                                                                Vec3Argument.getVec3(context, "pos"),
                                                                                RotationArgument.getRotation(context, "rot").getRotation(context.getSource()),
                                                                                FloatArgumentType.getFloat(context, "radius"),
                                                                                IntegerArgumentType.getInteger(context, "depth"),
                                                                                false
                                                                        ))
                                                                        .then(Commands.argument("debug", BoolArgumentType.bool())
                                                                                .executes(context -> carveCanyonTunnel(
                                                                                        context.getSource(),
                                                                                        Vec3Argument.getVec3(context, "pos"),
                                                                                        RotationArgument.getRotation(context, "rot").getRotation(context.getSource()),
                                                                                        FloatArgumentType.getFloat(context, "radius"),
                                                                                        IntegerArgumentType.getInteger(context, "depth"),
                                                                                        BoolArgumentType.getBool(context, "debug")
                                                                                ))))))))
                                .then(Commands.literal("nether_cave")
                                        .then(Commands.literal("tunnel")
                                                .executes(context -> carveNetherCaveTunnel(
                                                        context.getSource(),
                                                        Vec3Argument.getVec3(context, "pos"),
                                                        null, null, null, false
                                                ))
                                                .then(Commands.argument("rot", RotationArgument.rotation())
                                                        .suggests(CAVE_ROT_PROVIDER)
                                                        .executes(context -> carveNetherCaveTunnel(
                                                                context.getSource(),
                                                                Vec3Argument.getVec3(context, "pos"),
                                                                RotationArgument.getRotation(context, "rot").getRotation(context.getSource()),
                                                                null, null, false
                                                        ))
                                                        .then(Commands.argument("radius", FloatArgumentType.floatArg(0.0F))
                                                                .suggests(CANYON_RADIUS_PROVIDER)
                                                                .executes(context -> carveNetherCaveTunnel(
                                                                        context.getSource(),
                                                                        Vec3Argument.getVec3(context, "pos"),
                                                                        RotationArgument.getRotation(context, "rot").getRotation(context.getSource()),
                                                                        FloatArgumentType.getFloat(context, "radius"),
                                                                        null, false
                                                                ))
                                                                .then(Commands.argument("depth", IntegerArgumentType.integer(0))
                                                                        .suggests(CAVE_DEPTH_PROVIDER)
                                                                        .executes(context -> carveNetherCaveTunnel(
                                                                                context.getSource(),
                                                                                Vec3Argument.getVec3(context, "pos"),
                                                                                RotationArgument.getRotation(context, "rot").getRotation(context.getSource()),
                                                                                FloatArgumentType.getFloat(context, "radius"),
                                                                                IntegerArgumentType.getInteger(context, "depth"),
                                                                                false
                                                                        ))
                                                                        .then(Commands.argument("debug", BoolArgumentType.bool())
                                                                                .executes(context -> carveNetherCaveTunnel(
                                                                                        context.getSource(),
                                                                                        Vec3Argument.getVec3(context, "pos"),
                                                                                        RotationArgument.getRotation(context, "rot").getRotation(context.getSource()),
                                                                                        FloatArgumentType.getFloat(context, "radius"),
                                                                                        IntegerArgumentType.getInteger(context, "depth"),
                                                                                        BoolArgumentType.getBool(context, "debug")
                                                                                )))))))
                                        .then(Commands.literal("room")
                                                .executes(context -> carveNetherCaveRoom(
                                                        context.getSource(),
                                                        Vec3Argument.getVec3(context, "pos"),
                                                        null
                                                ))
                                                .then(Commands.argument("radius", FloatArgumentType.floatArg(0.0F))
                                                        .suggests(ROOM_RADIUS_PROVIDER)
                                                        .executes(context -> carveNetherCaveRoom(
                                                                context.getSource(),
                                                                Vec3Argument.getVec3(context, "pos"),
                                                                FloatArgumentType.getFloat(context, "radius")
                                                        )))))
                                .then(Commands.literal("underwater_cave")
                                        .then(Commands.literal("tunnel")
                                                .executes(context -> carveUnderwaterCaveTunnel(
                                                        context.getSource(),
                                                        Vec3Argument.getVec3(context, "pos"),
                                                        null, null, null, false
                                                ))
                                                .then(Commands.argument("rot", RotationArgument.rotation())
                                                        .suggests(CAVE_ROT_PROVIDER)
                                                        .executes(context -> carveUnderwaterCaveTunnel(
                                                                context.getSource(),
                                                                Vec3Argument.getVec3(context, "pos"),
                                                                RotationArgument.getRotation(context, "rot").getRotation(context.getSource()),
                                                                null, null, false
                                                        ))
                                                        .then(Commands.argument("radius", FloatArgumentType.floatArg(0.0F))
                                                                .suggests(CAVE_RADIUS_PROVIDER)
                                                                .executes(context -> carveUnderwaterCaveTunnel(
                                                                        context.getSource(),
                                                                        Vec3Argument.getVec3(context, "pos"),
                                                                        RotationArgument.getRotation(context, "rot").getRotation(context.getSource()),
                                                                        FloatArgumentType.getFloat(context, "radius"),
                                                                        null, false
                                                                ))
                                                                .then(Commands.argument("depth", IntegerArgumentType.integer(0))
                                                                        .suggests(CAVE_DEPTH_PROVIDER)
                                                                        .executes(context -> carveUnderwaterCaveTunnel(
                                                                                context.getSource(),
                                                                                Vec3Argument.getVec3(context, "pos"),
                                                                                RotationArgument.getRotation(context, "rot").getRotation(context.getSource()),
                                                                                FloatArgumentType.getFloat(context, "radius"),
                                                                                IntegerArgumentType.getInteger(context, "depth"),
                                                                                false
                                                                        ))
                                                                        .then(Commands.argument("debug", BoolArgumentType.bool())
                                                                                .executes(context -> carveUnderwaterCaveTunnel(
                                                                                        context.getSource(),
                                                                                        Vec3Argument.getVec3(context, "pos"),
                                                                                        RotationArgument.getRotation(context, "rot").getRotation(context.getSource()),
                                                                                        FloatArgumentType.getFloat(context, "radius"),
                                                                                        IntegerArgumentType.getInteger(context, "depth"),
                                                                                        BoolArgumentType.getBool(context, "debug")
                                                                                )))))))
                                        .then(Commands.literal("room")
                                                .executes(context -> carveUnderwaterCaveRoom(
                                                        context.getSource(),
                                                        Vec3Argument.getVec3(context, "pos"),
                                                        null
                                                ))
                                                .then(Commands.argument("radius", FloatArgumentType.floatArg(0.0F))
                                                        .suggests(ROOM_RADIUS_PROVIDER)
                                                        .executes(context -> carveUnderwaterCaveRoom(
                                                                context.getSource(),
                                                                Vec3Argument.getVec3(context, "pos"),
                                                                FloatArgumentType.getFloat(context, "radius")
                                                        )))))
                                .then(Commands.literal("underwater_canyon")
                                        .then(Commands.literal("tunnel")
                                                .executes(context -> carveUnderwaterCanyonTunnel(
                                                        context.getSource(),
                                                        Vec3Argument.getVec3(context, "pos"),
                                                        null, null, null, false
                                                ))
                                                .then(Commands.argument("rot", RotationArgument.rotation())
                                                        .suggests(CAVE_ROT_PROVIDER)
                                                        .executes(context -> carveUnderwaterCanyonTunnel(
                                                                context.getSource(),
                                                                Vec3Argument.getVec3(context, "pos"),
                                                                RotationArgument.getRotation(context, "rot").getRotation(context.getSource()),
                                                                null, null, false
                                                        ))
                                                        .then(Commands.argument("radius", FloatArgumentType.floatArg(0.0F))
                                                                .suggests(CANYON_RADIUS_PROVIDER)
                                                                .executes(context -> carveUnderwaterCanyonTunnel(
                                                                        context.getSource(),
                                                                        Vec3Argument.getVec3(context, "pos"),
                                                                        RotationArgument.getRotation(context, "rot").getRotation(context.getSource()),
                                                                        FloatArgumentType.getFloat(context, "radius"),
                                                                        null, false
                                                                ))
                                                                .then(Commands.argument("depth", IntegerArgumentType.integer(0))
                                                                        .suggests(CAVE_DEPTH_PROVIDER)
                                                                        .executes(context -> carveUnderwaterCanyonTunnel(
                                                                                context.getSource(),
                                                                                Vec3Argument.getVec3(context, "pos"),
                                                                                RotationArgument.getRotation(context, "rot").getRotation(context.getSource()),
                                                                                FloatArgumentType.getFloat(context, "radius"),
                                                                                IntegerArgumentType.getInteger(context, "depth"),
                                                                                false
                                                                        ))
                                                                        .then(Commands.argument("debug", BoolArgumentType.bool())
                                                                                .executes(context -> carveUnderwaterCanyonTunnel(
                                                                                        context.getSource(),
                                                                                        Vec3Argument.getVec3(context, "pos"),
                                                                                        RotationArgument.getRotation(context, "rot").getRotation(context.getSource()),
                                                                                        FloatArgumentType.getFloat(context, "radius"),
                                                                                        IntegerArgumentType.getInteger(context, "depth"),
                                                                                        BoolArgumentType.getBool(context, "debug")
                                                                                ))))))))))
                .then(Commands.literal("continue")
                        .executes(context -> continueCarve(context.getSource())))
                .then(Commands.literal("trail")
                        .then(Commands.literal("hide")
                                .executes(context -> renderTrail(context.getSource(), false)))
                        .then(Commands.literal("show")
                                .executes(context -> renderTrail(context.getSource(), true)))));
    }

    public static int carveCaveTunnel(CommandSource source, Vec3d pos, @Nullable Vec2f rot, @Nullable Float radius, @Nullable Integer depth, boolean debug) {
        world = source.getWorld();
        SharedSeedRandom rand = new SharedSeedRandom();
        removeAll();
        CavePatcher carver = new CavePatcher();
        float yaw, pitch;
        if (rot == null) {
            yaw = rand.nextFloat() * (float) Math.PI * 2F;
            pitch = (rand.nextFloat() - 0.5F) / 4F;
        } else {
            yaw = (rot.y + 90F) * (float) Math.PI / 180F;
            pitch = -rot.x * (float) Math.PI / 180F;
        }
        if (radius == null) {
            radius = rand.nextFloat() * 2F + rand.nextFloat();
            if (rand.nextInt(10) == 0)
                radius *= rand.nextFloat() * rand.nextFloat() * 3 + 1;
        }
        if (depth == null) {
            int i = (carver.func_202520_b() * 2 - 1) * 16;
            depth = i - rand.nextInt(i / 4);
        }
        carver.carveTunnel(world, rand, pos.x, pos.y, pos.z, radius, yaw, pitch, 0, depth, 1, debug);
        sendMessage();
        source.sendFeedback(new TextComponentTranslation("commands.carve.start.success", pos.x, pos.y, pos.z), true);
        return 1;
    }

    public static int carveUnderwaterCaveTunnel(CommandSource source, Vec3d pos, @Nullable Vec2f rot, @Nullable Float radius, @Nullable Integer depth, boolean debug) {
        world = source.getWorld();
        SharedSeedRandom rand = new SharedSeedRandom();
        removeAll();
        UnderwaterCavePatcher carver = new UnderwaterCavePatcher();
        float yaw, pitch;
        if (rot == null) {
            yaw = rand.nextFloat() * (float) Math.PI * 2F;
            pitch = (rand.nextFloat() - 0.5F) / 4F;
        } else {
            yaw = (rot.y + 90F) * (float) Math.PI / 180F;
            pitch = -rot.x * (float) Math.PI / 180F;
        }
        if (radius == null) {
            radius = rand.nextFloat() * 2F + rand.nextFloat();
            if (rand.nextInt(10) == 0)
                radius *= rand.nextFloat() * rand.nextFloat() * 3 + 1;
        }
        if (depth == null) {
            int i = (carver.func_202520_b() * 2 - 1) * 16;
            depth = i - rand.nextInt(i / 4);
        }
        carver.carveTunnel(world, rand, pos.x, pos.y, pos.z, radius, yaw, pitch, 0, depth, 1, debug);
        sendMessage();
        source.sendFeedback(new TextComponentTranslation("commands.carve.start.success", pos.x, pos.y, pos.z), true);
        return 1;
    }

    public static int carveCanyonTunnel(CommandSource source, Vec3d pos, @Nullable Vec2f rot, @Nullable Float radius, @Nullable Integer depth, boolean debug) {
        world = source.getWorld();
        SharedSeedRandom rand = new SharedSeedRandom();
        removeAll();
        CanyonPatcher carver = new CanyonPatcher();
        float yaw, pitch;
        if (rot == null) {
            yaw = rand.nextFloat() * (float) Math.PI * 2F;
            pitch = (rand.nextFloat() - 0.5F) / 4F;
        } else {
            yaw = (rot.y + 90F) * (float) Math.PI / 180F;
            pitch = -rot.x * (float) Math.PI / 180F;
        }
        if (radius == null) {
            radius = (rand.nextFloat() * 2F + rand.nextFloat()) * 2F;
        }
        if (depth == null) {
            int i = (carver.func_202520_b() * 2 - 1) * 16;
            depth = i - rand.nextInt(i / 4);
        }
        carver.carveTunnel(world, rand, pos.x, pos.y, pos.z, radius, yaw, pitch, 0, depth, 3, debug);
        sendMessage();
        source.sendFeedback(new TextComponentTranslation("commands.carve.start.success", pos.x, pos.y, pos.z), true);
        return 1;
    }

    public static int carveUnderwaterCanyonTunnel(CommandSource source, Vec3d pos, @Nullable Vec2f rot, @Nullable Float radius, @Nullable Integer depth, boolean debug) {
        world = source.getWorld();
        SharedSeedRandom rand = new SharedSeedRandom();
        removeAll();
        UnderwaterCanyonPatcher carver = new UnderwaterCanyonPatcher();
        float yaw, pitch;
        if (rot == null) {
            yaw = rand.nextFloat() * (float) Math.PI * 2F;
            pitch = (rand.nextFloat() - 0.5F) / 4F;
        } else {
            yaw = (rot.y + 90F) * (float) Math.PI / 180F;
            pitch = -rot.x * (float) Math.PI / 180F;
        }
        if (radius == null) {
            radius = (rand.nextFloat() * 2F + rand.nextFloat()) * 2F;
        }
        if (depth == null) {
            int i = (carver.func_202520_b() * 2 - 1) * 16;
            depth = i - rand.nextInt(i / 4);
        }
        carver.carveTunnel(world, rand, pos.x, pos.y, pos.z, radius, yaw, pitch, 0, depth, 3, debug);
        sendMessage();
        source.sendFeedback(new TextComponentTranslation("commands.carve.start.success", pos.x, pos.y, pos.z), true);
        return 1;
    }

    public static int carveNetherCaveTunnel(CommandSource source, Vec3d pos, @Nullable Vec2f rot, @Nullable Float radius, @Nullable Integer depth, boolean debug) {
        world = source.getWorld();
        SharedSeedRandom rand = new SharedSeedRandom();
        removeAll();
        NetherCavePatcher carver = new NetherCavePatcher();
        float yaw, pitch;
        if (rot == null) {
            yaw = rand.nextFloat() * (float) Math.PI * 2F;
            pitch = (rand.nextFloat() - 0.5F) / 4F;
        } else {
            yaw = (rot.y + 90F) * (float) Math.PI / 180F;
            pitch = -rot.x * (float) Math.PI / 180F;
        }
        if (radius == null) {
            radius = (rand.nextFloat() * 2F + rand.nextFloat()) * 2F;
        }
        if (depth == null) {
            int i = (carver.func_202520_b() * 2 - 1) * 16;
            depth = i - rand.nextInt(i / 4);
        }
        carver.carveTunnel(world, rand, pos.x, pos.y, pos.z, radius, yaw, pitch, 0, depth, 5, debug);
        sendMessage();
        source.sendFeedback(new TextComponentTranslation("commands.carve.start.success", pos.x, pos.y, pos.z), true);
        return 1;
    }

    public static int carveCaveRoom(CommandSource source, Vec3d pos, @Nullable Float radius) {
        world = source.getWorld();
        SharedSeedRandom rand = new SharedSeedRandom();
        removeAll();
        if (radius == null) {
            radius = 1 + rand.nextFloat() * 6;
        }
        new CavePatcher().carveRoom(world, rand, pos.x, pos.y, pos.z, radius, 0.5);
        sendMessage();
        source.sendFeedback(new TextComponentTranslation("commands.carve.start.success", pos.x, pos.y, pos.z), true);
        return 1;
    }

    public static int carveUnderwaterCaveRoom(CommandSource source, Vec3d pos, @Nullable Float radius) {
        world = source.getWorld();
        SharedSeedRandom rand = new SharedSeedRandom();
        removeAll();
        if (radius == null) {
            radius = 1 + rand.nextFloat() * 6;
        }
        new UnderwaterCavePatcher().carveRoom(world, rand, pos.x, pos.y, pos.z, radius, 0.5);
        sendMessage();
        source.sendFeedback(new TextComponentTranslation("commands.carve.start.success", pos.x, pos.y, pos.z), true);
        return 1;
    }

    public static int carveNetherCaveRoom(CommandSource source, Vec3d pos, @Nullable Float radius) {
        world = source.getWorld();
        SharedSeedRandom rand = new SharedSeedRandom();
        removeAll();
        if (radius == null) {
            radius = 1 + rand.nextFloat() * 6;
        }
        new NetherCavePatcher().carveRoom(world, rand, pos.x, pos.y, pos.z, radius, 0.5);
        sendMessage();
        source.sendFeedback(new TextComponentTranslation("commands.carve.start.success", pos.x, pos.y, pos.z), true);
        return 1;
    }

    public static int continueCarve(CommandSource source) throws CommandSyntaxException {
        ICarverPatcher current = getCarver();
        if (current == null)
            throw COMPLETE_EXCEPTION.create();
        World worldIn = source.getWorld();
        if (worldIn != world)
            throw DIMENSION_EXCEPTION.create();
        current.updateAndCarve();
        Vec3d pos = current.getPos();
        source.sendFeedback(new TextComponentTranslation("commands.carve.continue.success", pos.x, pos.y, pos.z), false);
        return 1;
    }

    public static int renderTrail(CommandSource source, boolean isRender) throws CommandSyntaxException {
        EntityPlayerMP player = source.asPlayer();
        if (isRender) {
            showTrail(player);
            source.sendFeedback(new TextComponentTranslation("commands.carve.trail.show.success"), false);
        } else {
            hideTrail(player);
            source.sendFeedback(new TextComponentTranslation("commands.carve.trail.hide.success"), false);
        }
        return 1;
    }

    private static void removeAll() {
        list.clear();
        posList.clear();
    }

    @Nullable
    public static ICarverPatcher getCarver() {
        if (!list.isEmpty())
            return list.get(list.size() - 1);
        else
            return null;
    }

    public static void addCarver(ICarverPatcher carver) {
        list.add(carver);
    }

    public static void removeCarver() {
        list.remove(list.size() - 1);
    }

    public static void addPosList(List<Vec3d> subList) {
        posList.add(subList);
    }

    public static void addPos(@Nullable Vec3d pos) {
        for (int i = posList.size() - 1; i >= 0; --i) {
            List<Vec3d> list = posList.get(i);
            if (list.isEmpty() || list.get(list.size() - 1) != null) {
                list.add(pos);
                break;
            }
        }
    }

    public static void sendMessage() {
        NBTTagList list = new NBTTagList();
        for (List<Vec3d> tunnel : posList) {
            NBTTagList subList = new NBTTagList();
            for (Vec3d pos : tunnel) {
                if (pos == null)
                    subList.add(new NBTTagCompound());
                else {
                    NBTTagCompound posTag = new NBTTagCompound();
                    posTag.setDouble("x", pos.x);
                    posTag.setDouble("y", pos.y);
                    posTag.setDouble("z", pos.z);
                    subList.add(posTag);
                }
            }
            NBTTagCompound listTag = new NBTTagCompound();
            listTag.setTag("PosList", subList);
            list.add(listTag);
        }
        MessageCarveTrail message = new MessageCarveTrail();
        message.compound = new NBTTagCompound();
        message.compound.setTag("TunnelList", list);
        NetworkManager.instance.send(PacketDistributor.DIMENSION.with(() -> world.getDimension().getType()), message);
    }

    public static void sendMessageToPlayer(EntityPlayerMP player, World worldIn) {
        NBTTagList list = new NBTTagList();
        for (List<Vec3d> tunnel : posList) {
            NBTTagList subList = new NBTTagList();
            for (Vec3d pos : tunnel) {
                if (pos == null)
                    subList.add(new NBTTagCompound());
                else {
                    NBTTagCompound posTag = new NBTTagCompound();
                    posTag.setDouble("x", pos.x);
                    posTag.setDouble("y", pos.y);
                    posTag.setDouble("z", pos.z);
                    subList.add(posTag);
                }
            }
            NBTTagCompound listTag = new NBTTagCompound();
            listTag.setTag("PosList", subList);
            list.add(listTag);
        }
        MessageCarveTrail message = new MessageCarveTrail();
        message.compound = new NBTTagCompound();
        if (worldIn == world)
            message.compound.setTag("TunnelList", list);
        else
            message.compound.setTag("TunnelList", new NBTTagList());
        NetworkManager.instance.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void hideTrail(EntityPlayerMP player) {
        MessageRenderControl message = new MessageRenderControl();
        message.renderType = 1;
        message.isRender = false;
        NetworkManager.instance.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void showTrail(EntityPlayerMP player) {
        MessageRenderControl message = new MessageRenderControl();
        message.renderType = 1;
        message.isRender = true;
        NetworkManager.instance.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

}
