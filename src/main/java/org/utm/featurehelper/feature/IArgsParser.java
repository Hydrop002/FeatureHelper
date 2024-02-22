package org.utm.featurehelper.feature;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public interface IArgsParser<T> {

    T parse(StringReader reader) throws CommandSyntaxException;

}
