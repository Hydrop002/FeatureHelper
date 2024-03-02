package org.utm.featurehelper.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.concurrent.CompletableFuture;

public class ConfiguredStructureArgument implements ArgumentType<StructureFeature<?, ?>> {

    private static final DynamicCommandExceptionType ERROR_NOT_FOUND = new DynamicCommandExceptionType(
            (str) -> new TranslationTextComponent("argument.structure.start.invalid", str)
    );

    public static ConfiguredStructureArgument configuredStructure() {
        return new ConfiguredStructureArgument();
    }

    public static StructureFeature<?, ?> getConfiguredStructure(final CommandContext<?> context, final String name) {
        return context.getArgument(name, StructureFeature.class);
    }

    @Override
    public StructureFeature<?, ?> parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation location = ResourceLocation.read(reader);
        return WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE.getOptional(location).orElseThrow(() -> ERROR_NOT_FOUND.create(location));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return ISuggestionProvider.suggestResource(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE.keySet(), builder);
    }

}
