package org.utm.featurehelper.command;

import com.google.common.base.Predicate;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.utm.featurehelper.feature.FeatureArgsParser;
import org.utm.featurehelper.feature.FeatureFactory;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CommandPopulate extends CommandBase {

    @Override
    public String getName() {
        return "populate";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.populate.usage";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 4)
            throw new WrongUsageException("commands.populate.usage");

        World world = sender.getEntityWorld();
        Random rand = new Random();

        BlockPos blockPos = parseBlockPos(sender, args, 0, false);

        if (!FeatureFactory.getNameSet().contains(args[3]))
            throw new CommandException("commands.populate.invalidName");

        String[] restArgs = Arrays.copyOfRange(args, 4, args.length);
        Constructor<?>[] list = FeatureFactory.getConstructorList(args[3]);
        boolean success = false;
        for (Constructor<?> constructor : list) {
            Class<?>[] classList = constructor.getParameterTypes();
            Object[] parsedArgs = new Object[classList.length];
            try {
                int i = 0, j = 0;
                for (; i < classList.length; ++i) {
                    Class<?> clazz = classList[i];
                    if (clazz.equals(Predicate.class)) {
                        ParameterizedType genericType = (ParameterizedType) constructor.getGenericParameterTypes()[i];
                        clazz = (Class<?>) genericType.getActualTypeArguments()[0];
                    }
                    if (FeatureArgsParser.isDouble(clazz)) {
                        if (j + 1 >= restArgs.length) break;
                        Object res = FeatureArgsParser.parseDouble(clazz, sender, restArgs[j++], restArgs[j++]);
                        if (res == null) break;
                        parsedArgs[i] = res;
                    } else {
                        if (j >= restArgs.length) break;
                        Object res = FeatureArgsParser.parseSingle(clazz, sender, restArgs[j++]);
                        if (res == null) break;
                        parsedArgs[i] = res;
                    }
                }
                if (i == classList.length && j == restArgs.length) {
                    success = true;
                }
            } catch (CommandException e) {
                continue;
            }
            if (success) {
                WorldGenerator gen = FeatureFactory.getFeature(constructor, parsedArgs);
                if (gen != null) {
                    if (gen.generate(world, rand, blockPos)) {
                        notifyCommandListener(sender, this, "commands.populate.success");
                    } else {
                        notifyCommandListener(sender, this, "commands.populate.failed");
                    }
                } else {
                    success = false;
                }
                break;
            }
        }
        if (!success)
            notifyCommandListener(sender, this, "commands.populate.invalidArgs");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        if (args.length >= 1 && args.length <= 3)
            return getTabCompletionCoordinate(args, 0, pos);
        else if (args.length == 4)
            return getListOfStringsMatchingLastWord(args, FeatureFactory.getNameSet());
        else if (args.length >= 5) {
            if (FeatureFactory.getNameSet().contains(args[3])) {
                Constructor<?>[] list = FeatureFactory.getConstructorList(args[3]);
                for (Constructor<?> constructor : list) {
                    Class<?>[] classList = constructor.getParameterTypes();
                    Type[] genericTypeList = constructor.getGenericParameterTypes();
                    notifyCommandListener(sender, this, "commands.populate.args", FeatureFactory.getParaString(classList, genericTypeList));
                }
            }
            return Collections.emptyList();
        } else
            return Collections.emptyList();
    }

}
