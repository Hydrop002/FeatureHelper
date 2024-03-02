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
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;

import java.util.concurrent.CompletableFuture;

public class StructureArgument implements ArgumentType<StructureFeature<?, ?>> {

    private static final DynamicCommandExceptionType ERROR_NOT_FOUND = new DynamicCommandExceptionType(
            (str) -> new TranslationTextComponent("argument.structure.start.invalid", str)
    );

    public static StructureArgument structure() {
        return new StructureArgument();
    }

    public static StructureFeature<?, ?> getStructure(final CommandContext<?> context, final String name) {
        return context.getArgument(name, StructureFeature.class);
    }

    private static <FC extends IFeatureConfig> StructureFeature<?, ?> createStructureFeature(Structure<FC> feature, IFeatureConfig config) {
        return new StructureFeature<>(feature, (FC) config);
    }

    @Override
    public StructureFeature<?, ?> parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation location = ResourceLocation.read(reader);
        Structure<?> structure = Registry.STRUCTURE_FEATURE.getOptional(location).orElseThrow(() -> ERROR_NOT_FOUND.create(location));
        //ConstructorResult result = new ConstructorParser().parse(reader);
        //IFeatureConfig config = null;
        //return createStructureFeature(structure, config);
        return null;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return ISuggestionProvider.suggestResource(Registry.STRUCTURE_FEATURE.keySet(), builder);
    }

}
