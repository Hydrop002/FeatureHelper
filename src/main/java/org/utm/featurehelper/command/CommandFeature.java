package org.utm.featurehelper.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.CompositeFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import org.utm.featurehelper.command.arguments.FeatureArgument;

public class CommandFeature {

    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(
            new TextComponentTranslation("commands.feature.failed")
    );
    private static final SimpleCommandExceptionType ARGS_EXCEPTION = new SimpleCommandExceptionType(
            new TextComponentTranslation("commands.feature.args")
    );

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("feature")
                .requires(source -> source.hasPermissionLevel(2))
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .then(Commands.argument("feature", FeatureArgument.feature())
                                .executes(context -> placeFeature(
                                        context.getSource(),
                                        BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                        FeatureArgument.getFeature(context, "feature")
                                )))));
    }

    public static int placeFeature(CommandSource source, BlockPos blockPos, CompositeFeature<?, ?> feature) throws CommandSyntaxException {
        World world = source.getWorld();
        SharedSeedRandom rand = new SharedSeedRandom();
        try {
            if (!feature.func_212245_a(world, world.getChunkProvider().getChunkGenerator(), rand, blockPos, IFeatureConfig.NO_FEATURE_CONFIG))
                throw FAILED_EXCEPTION.create();
        } catch (RuntimeException e) {
            throw ARGS_EXCEPTION.create();
        }
        source.sendFeedback(new TextComponentTranslation("commands.feature.success"), true);
        return 1;
    }

}
