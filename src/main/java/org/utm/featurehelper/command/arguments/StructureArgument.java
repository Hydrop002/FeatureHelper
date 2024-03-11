package org.utm.featurehelper.command.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.JsonOps;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import org.utm.featurehelper.feature.JsonObjectParser;

import java.util.concurrent.CompletableFuture;

public class StructureArgument implements ArgumentType<StructureFeature<?, ?>> {

    private static final DynamicCommandExceptionType ERROR_NOT_FOUND = new DynamicCommandExceptionType(
            (str) -> new TranslationTextComponent("argument.structure.start.invalid", str)
    );
    private static final DynamicCommandExceptionType ERROR_DECODE = new DynamicCommandExceptionType(
            (str) -> new TranslationTextComponent("argument.structure.start.config", str)
    );

    public static StructureArgument structure() {
        return new StructureArgument();
    }

    public static StructureFeature<?, ?> getStructure(final CommandContext<?> context, final String name) {
        return context.getArgument(name, StructureFeature.class);
    }

    @Override
    public StructureFeature<?, ?> parse(StringReader reader) throws CommandSyntaxException {
        JsonObjectParser.suggester = (builder) -> ISuggestionProvider.suggestResource(Registry.STRUCTURE_FEATURE.keySet(), builder);
        int start = reader.getCursor();
        ResourceLocation location = ResourceLocation.read(reader);
        Structure<?> structure = Registry.STRUCTURE_FEATURE.getOptional(location).orElseThrow(() -> {
            reader.setCursor(start);
            return ERROR_NOT_FOUND.create(location);
        });
        JsonObject config = JsonObjectParser.parse(reader);
        JsonObject json = new JsonObject();
        json.add("config", config);
        String[] str = new String[1];
        return structure.configuredStructureCodec().decode(JsonOps.INSTANCE, json).resultOrPartial(
                message -> str[0] = message
        ).orElseThrow(
                () -> ERROR_DECODE.create(str[0])
        ).getFirst();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        StringReader reader = new StringReader(builder.getInput());
        reader.setCursor(builder.getStart());
        try {
            parse(reader);  // update suggester
        } catch (CommandSyntaxException ignored) {
        }
        return JsonObjectParser.suggester.apply(builder.createOffset(reader.getCursor()));
    }

}
