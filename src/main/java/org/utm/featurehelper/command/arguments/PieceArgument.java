package org.utm.featurehelper.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TranslationTextComponent;
import org.utm.featurehelper.feature.PieceFactory;

import java.util.concurrent.CompletableFuture;

public class PieceArgument implements ArgumentType<String> {

    private static final DynamicCommandExceptionType ERROR_NOT_FOUND = new DynamicCommandExceptionType(
            (str) -> new TranslationTextComponent("argument.structure.piece.invalid", str)
    );

    public static PieceArgument piece() {
        return new PieceArgument();
    }

    public static String getPiece(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String str = reader.readUnquotedString();
        if (PieceFactory.getNameSet().contains(str)) {
            return str;
        } else {
            throw ERROR_NOT_FOUND.create(str);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return ISuggestionProvider.suggest(PieceFactory.getNameSet(), builder);
    }

}
