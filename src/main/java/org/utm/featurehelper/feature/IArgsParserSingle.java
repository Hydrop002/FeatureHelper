package org.utm.featurehelper.feature;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public interface IArgsParserSingle<T> {

    T parse(ICommandSender sender, String str) throws CommandException;

}
