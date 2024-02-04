package org.utm.featurehelper.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TextComponentTranslation;
import org.utm.featurehelper.feature.StartFactory;

import java.util.concurrent.CompletableFuture;

public class StartArgument implements ArgumentType<String> {

    private static final DynamicCommandExceptionType NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType(
            (str) -> new TextComponentTranslation("argument.structure.start.invalid", str)
    );

    public static StartArgument start() {
        return new StartArgument();
    }

    public static String getStart(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public <S> String parse(StringReader reader) throws CommandSyntaxException {
        String str = reader.readUnquotedString();
        if (StartFactory.getNameSet().contains(str)) {
            return str;
        } else {
            throw NOT_FOUND_EXCEPTION.create(str);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return ISuggestionProvider.suggest(StartFactory.getNameSet(), builder);
    }

}
