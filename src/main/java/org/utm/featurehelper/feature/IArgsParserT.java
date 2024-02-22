package org.utm.featurehelper.feature;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.lang.reflect.Type;

public interface IArgsParserT<T> {

    T parse(StringReader reader, Type... types) throws CommandSyntaxException;

}
