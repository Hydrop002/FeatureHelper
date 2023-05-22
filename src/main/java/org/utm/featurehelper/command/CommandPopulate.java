package org.utm.featurehelper.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.MathHelper;
import org.utm.featurehelper.feature.FeatureFactory;

import java.util.Arrays;
import java.util.List;

public class CommandPopulate extends CommandBase {

    @Override
    public String getCommandName() {
        return "populate";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.populate.usage";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 4)
            throw new WrongUsageException("commands.populate.usage");

        int x = sender.getPlayerCoordinates().posX;
        int y = sender.getPlayerCoordinates().posY;
        int z = sender.getPlayerCoordinates().posZ;
        x = MathHelper.floor_double(func_110666_a(sender, x, args[0]));
        y = MathHelper.floor_double(func_110666_a(sender, y, args[1]));
        z = MathHelper.floor_double(func_110666_a(sender, z, args[2]));

        if (!FeatureFactory.getNameSet().contains(args[3]))
            throw new CommandException("commands.populate.invalidName");

        String[] restArgs = Arrays.copyOfRange(args, 4, args.length);
        Class<?>[] classList = FeatureFactory.getParameters(args[3]);
        if (restArgs.length != classList.length) {
            String[] classNameList = new String[classList.length];
            for (int i = 0; i < classList.length; ++i) {
                classNameList[i] = classList[i].getSimpleName();
            }
            throw new CommandException("commands.populate.invalidArgs", Arrays.toString(classNameList));
        }

        Object[] parsedArgs = new Object[restArgs.length];
        for (int i = 0; i < classList.length; ++i) {
            Class<?> clazz = classList[i];
            if (clazz.equals(Integer.class)) {
                parsedArgs[i] = parseInt(sender, restArgs[i]);
            } else if (clazz.equals(Double.class)) {
                parsedArgs[i] = parseDouble(sender, restArgs[i]);
            } else if (clazz.equals(Boolean.class)) {
                parsedArgs[i] = parseBoolean(sender, restArgs[i]);
            } else {
                parsedArgs[i] = restArgs[i];
            }
        }
        FeatureFactory.getFeature(args[3], parsedArgs);
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 4)
            return getListOfStringsFromIterableMatchingLastWord(args, FeatureFactory.getNameSet());
        else
            return null;
    }

}
