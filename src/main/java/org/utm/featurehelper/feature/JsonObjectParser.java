package org.utm.featurehelper.feature;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.util.text.TranslationTextComponent;

import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class JsonObjectParser {

    private static final DynamicCommandExceptionType ERROR_PARSE = new DynamicCommandExceptionType(
            (str) -> new TranslationTextComponent("argument.json.failed", str)
    );

    public static Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggester;

    public static JsonObject parse(StringReader reader) throws CommandSyntaxException {
        suggester = (builder) -> builder.suggest("{").buildFuture();
        if (!reader.canRead() || reader.peek() != '{')
            return new JsonObject();
        suggester = SuggestionsBuilder::buildFuture;
        JsonReader jsonReader = new JsonReader(new java.io.StringReader(reader.getRemaining()));
        jsonReader.setLenient(false);
        try {
            JsonObject json = new JsonParser().parse(jsonReader).getAsJsonObject();
            reader.setCursor(reader.getCursor() + getPos(jsonReader));
            return json;
        } catch (RuntimeException e) {
            throw ERROR_PARSE.create(e.getMessage().split(": ", 2)[1]);
        }
    }

    private static int getPos(JsonReader reader) {
        try {
            Field field = JsonReader.class.getDeclaredField("pos");
            field.setAccessible(true);
            return field.getInt(reader);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
