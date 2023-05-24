package org.utm.featurehelper.command;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.ChestGenHooks;
import org.utm.featurehelper.feature.FeatureFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

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

        World world = sender.getEntityWorld();
        Random rand = new Random();

        int x = sender.getPlayerCoordinates().posX;
        int y = sender.getPlayerCoordinates().posY;
        int z = sender.getPlayerCoordinates().posZ;
        x = MathHelper.floor_double(func_110666_a(sender, x, args[0]));
        y = MathHelper.floor_double(func_110666_a(sender, y, args[1]));
        z = MathHelper.floor_double(func_110666_a(sender, z, args[2]));

        if (!FeatureFactory.getNameSet().contains(args[3]))
            throw new CommandException("commands.populate.invalidName");

        String[] restArgs = Arrays.copyOfRange(args, 4, args.length);
        List<Class<?>[]> list = FeatureFactory.getParaList(args[3]);
        for (Class<?>[] classList : list) {
            if (restArgs.length != classList.length)
                continue;
            Object[] parsedArgs = new Object[restArgs.length];
            for (int i = 0; i < classList.length; ++i) {
                Class<?> clazz = classList[i];
                if (clazz.equals(Integer.class)) {
                    parsedArgs[i] = parseInt(sender, restArgs[i]);
                } else if (clazz.equals(Double.class)) {
                    parsedArgs[i] = parseDouble(sender, restArgs[i]);
                } else if (clazz.equals(Boolean.class)) {
                    parsedArgs[i] = parseBoolean(sender, restArgs[i]);
                } else if (clazz.equals(Block.class)) {
                    parsedArgs[i] = Block.blockRegistry.getObject(restArgs[i]);
                } else if (clazz.equals(WeightedRandomChestContent[].class)) {
                    parsedArgs[i] = ChestGenHooks.getItems(ChestGenHooks.BONUS_CHEST, rand);
                } else {
                    parsedArgs[i] = restArgs[i];
                }
            }
            WorldGenerator gen = FeatureFactory.getFeature(args[3], parsedArgs);
            if (gen != null) {
                gen.generate(world, rand, x, y, z);
            }
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 4)
            return getListOfStringsFromIterableMatchingLastWord(args, FeatureFactory.getNameSet());
        else if (args.length >= 5) {
            if (FeatureFactory.getNameSet().contains(args[3])) {
                List<Class<?>[]> list = FeatureFactory.getParaList(args[3]);
                for (Class<?>[] classList : list) {
                    func_152373_a(sender, this, "commands.populate.args", FeatureFactory.getParaString(classList));
                }
            }
            return null;
        } else
            return null;
    }

}
