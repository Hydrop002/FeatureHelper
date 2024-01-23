package org.utm.featurehelper.feature;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public interface IArgsParserDouble<T> {

    T parse(ICommandSender sender, String str1, String str2) throws CommandException;

}
