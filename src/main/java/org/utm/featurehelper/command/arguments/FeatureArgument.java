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
import net.minecraft.world.gen.feature.CompositeFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Passthrough;
import org.utm.featurehelper.feature.FeatureArgsParser;
import org.utm.featurehelper.feature.FeatureFactory;

import java.util.concurrent.CompletableFuture;

public class FeatureArgument implements ArgumentType<CompositeFeature<?, ?>> {

    private static final DynamicCommandExceptionType NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType(
            (str) -> new TextComponentTranslation("argument.feature.invalid", str)
    );

    public static FeatureArgument feature() {
        return new FeatureArgument();
    }

    public static CompositeFeature<?, ?> getFeature(final CommandContext<?> context, final String name) {
        return context.getArgument(name, CompositeFeature.class);
    }

    private static <C extends IFeatureConfig> CompositeFeature<?, ?> createCompositeFeature(Feature<C> feature, IFeatureConfig config) {
        return new CompositeFeature<>(feature, (C) config, new Passthrough(), IPlacementConfig.NO_PLACEMENT_CONFIG);
    }

    @Override
    public <S> CompositeFeature<?, ?> parse(StringReader reader) throws CommandSyntaxException {
        FeatureArgsParser.suggester = (builder) -> ISuggestionProvider.suggest(FeatureFactory.getNameSet(), builder);
        int start = reader.getCursor();
        String str = reader.readUnquotedString();
        if (!FeatureFactory.getNameSet().contains(str)) {
            reader.setCursor(start);
            throw NOT_FOUND_EXCEPTION.create(str);
        }
        FeatureArgsParser featureParser = new FeatureArgsParser(FeatureFactory.getConstructorList(str));
        Feature<? extends IFeatureConfig> feature = featureParser.parseFeature(reader);
        FeatureArgsParser configParser = new FeatureArgsParser(FeatureFactory.getConfigConstructorList(str));
        IFeatureConfig config = configParser.parseConfig(reader);
        return createCompositeFeature(feature, config);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        StringReader reader = new StringReader(builder.getInput());
        reader.setCursor(builder.getStart());
        try {
            parse(reader);  // update suggester
        } catch (CommandSyntaxException ignored) {
        }
        return FeatureArgsParser.suggester.apply(builder.createOffset(reader.getCursor()));
    }

}
