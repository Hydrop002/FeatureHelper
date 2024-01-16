package org.utm.featurehelper.command;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.ChestGenHooks;
import org.utm.featurehelper.feature.FeatureFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
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
                    if (clazz.equals(int.class)) {
                        if (j >= restArgs.length) break;
                        parsedArgs[i] = parseInt(restArgs[j++]);
                    } else if (clazz.equals(double.class)) {
                        if (j >= restArgs.length) break;
                        parsedArgs[i] = parseDouble(restArgs[j++]);
                    } else if (clazz.equals(boolean.class)) {
                        if (j >= restArgs.length) break;
                        parsedArgs[i] = parseBoolean(restArgs[j++]);
                    } else if (clazz.equals(Block.class)) {
                        if (j >= restArgs.length) break;
                        parsedArgs[i] = getBlockByText(sender, restArgs[j++]);
                    } else if (clazz.equals(IBlockState.class)) {  // IBlockState receive 2 args
                        if (j + 1 >= restArgs.length) break;
                        IBlockState blockState = getBlockByText(sender, restArgs[j++]).getStateFromMeta(parseInt(restArgs[j++]));
                        parsedArgs[i] = blockState;
                    } else if (clazz.equals(BlockBush.class)) {
                        if (j >= restArgs.length) break;
                        Block block = getBlockByText(sender, restArgs[j++]);
                        if (block instanceof BlockBush)
                            parsedArgs[i] = block;
                        else
                            break;
                    } else if (clazz.equals(BlockFlower.class)) {
                        if (j >= restArgs.length) break;
                        Block block = getBlockByText(sender, restArgs[j++]);
                        if (block instanceof BlockFlower)
                            parsedArgs[i] = block;
                        else
                            break;
                    } else if (clazz.equals(BlockTallGrass.EnumType.class)) {
                        if (j >= restArgs.length) break;
                        parsedArgs[i] = BlockTallGrass.EnumType.byMetadata(parseInt(restArgs[j++]));
                    } else if (clazz.equals(BlockFlower.EnumFlowerType.class)) {
                        if (j >= restArgs.length) break;
                        int type = parseInt(restArgs[j++]);
                        if (type < 0 || type >= BlockFlower.EnumFlowerType.values().length) type = 0;
                        parsedArgs[i] = BlockFlower.EnumFlowerType.values()[type];
                    } else if (clazz.equals(Predicate.class)) {
                        ParameterizedType genericType = (ParameterizedType) constructor.getGenericParameterTypes()[i];
                        Type[] types = genericType.getActualTypeArguments();
                        if (types[0].equals(IBlockState.class)) {
                            if (j >= restArgs.length) break;
                            parsedArgs[i] = BlockHelper.forBlock(getBlockByText(sender, restArgs[j++]));
                        }
                    } else if (clazz.equals(List.class)) {
                        ParameterizedType genericType = (ParameterizedType) constructor.getGenericParameterTypes()[i];
                        Type[] types = genericType.getActualTypeArguments();
                        if (types[0].equals(WeightedRandomChestContent.class)) {
                            if (j++ >= restArgs.length) break;
                            parsedArgs[i] = ChestGenHooks.getItems(ChestGenHooks.BONUS_CHEST, rand);
                        }
                    } else {
                        if (j >= restArgs.length) break;
                        parsedArgs[i] = restArgs[j++];
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
                        notifyOperators(sender, this, "commands.populate.success");
                    } else {
                        notifyOperators(sender, this, "commands.populate.failed");
                    }
                } else {
                    success = false;
                }
                break;
            }
        }
        if (!success)
            notifyOperators(sender, this, "commands.populate.invalidArgs");
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length >= 1 && args.length <= 3)
            return func_175771_a(args, 0, pos);
        else if (args.length == 4)
            return getListOfStringsMatchingLastWord(args, FeatureFactory.getNameSet());
        else if (args.length >= 5) {
            if (FeatureFactory.getNameSet().contains(args[3])) {
                Constructor<?>[] list = FeatureFactory.getConstructorList(args[3]);
                for (Constructor<?> constructor : list) {
                    Class<?>[] classList = constructor.getParameterTypes();
                    Type[] genericTypeList = constructor.getGenericParameterTypes();
                    notifyOperators(sender, this, "commands.populate.args", FeatureFactory.getParaString(classList, genericTypeList));
                }
            }
            return null;
        } else
            return null;
    }

}
