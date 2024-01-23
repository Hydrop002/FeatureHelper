package org.utm.featurehelper.feature;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import java.util.HashMap;
import java.util.Map;

public class FeatureArgsParser {

    private static Map<Class<?>, IArgsParserSingle<?>> dispatcher1 = new HashMap<>();
    private static Map<Class<?>, IArgsParserDouble<?>> dispatcher2 = new HashMap<>();

    static {
        dispatcher1.put(int.class, (sender, str) -> CommandBase.parseInt(str));
        dispatcher1.put(double.class, (sender, str) -> CommandBase.parseDouble(str));
        dispatcher1.put(boolean.class, (sender, str) -> CommandBase.parseBoolean(str));
        dispatcher1.put(Block.class, CommandBase::getBlockByText);
        dispatcher2.put(IBlockState.class, (sender, str1, str2) -> {
            Block block = CommandBase.getBlockByText(sender, str1);
            return CommandBase.convertArgToBlockState(block, str2);
        });
        dispatcher1.put(BlockBush.class, (sender, str) -> {
            Block block = CommandBase.getBlockByText(sender, str);
            return block instanceof BlockBush ? block : null;
        });
        dispatcher1.put(BlockFlower.class, (sender, str) -> {
            Block block = CommandBase.getBlockByText(sender, str);
            return block instanceof BlockFlower ? block : null;
        });
        dispatcher1.put(BlockTallGrass.EnumType.class, (sender, str) -> BlockTallGrass.EnumType.byMetadata(CommandBase.parseInt(str)));
        dispatcher1.put(BlockFlower.EnumFlowerType.class, (sender, str) -> {
            int type = CommandBase.parseInt(str);
            if (type < 0 || type >= BlockFlower.EnumFlowerType.values().length) type = 0;
            return BlockFlower.EnumFlowerType.values()[type];
        });
    }

    public static boolean isDouble(Class<?> clazz) {
        return dispatcher2.containsKey(clazz);
    }

    public static <T> T parseSingle(Class<T> clazz, ICommandSender sender, String str) throws CommandException {
        return (T) dispatcher1.get(clazz).parse(sender, str);
    }

    public static <T> T parseDouble(Class<T> clazz, ICommandSender sender, String str1, String str2) throws CommandException {
        return (T) dispatcher2.get(clazz).parse(sender, str1, str2);
    }

}
