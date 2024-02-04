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
import org.utm.featurehelper.feature.PieceFactory;

import java.util.concurrent.CompletableFuture;

public class PieceArgument implements ArgumentType<String> {

    private static final DynamicCommandExceptionType NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType(
            (str) -> new TextComponentTranslation("argument.structure.piece.invalid", str)
    );

    public static PieceArgument piece() {
        return new PieceArgument();
    }

    public static String getPiece(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public <S> String parse(StringReader reader) throws CommandSyntaxException {
        String str = reader.readUnquotedString();
        if (PieceFactory.getNameSet().contains(str)) {
            return str;
        } else {
            throw NOT_FOUND_EXCEPTION.create(str);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return ISuggestionProvider.suggest(PieceFactory.getNameSet(), builder);
    }

}
