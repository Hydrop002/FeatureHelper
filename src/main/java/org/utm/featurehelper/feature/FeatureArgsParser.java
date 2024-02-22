package org.utm.featurehelper.feature;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.command.arguments.BlockStateInput;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.MinableConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;

public class FeatureArgsParser {

    private static final SimpleCommandExceptionType NO_CANDIDATE_EXCEPTION = new SimpleCommandExceptionType(
            new TextComponentTranslation("argument.feature.args.candidate")
    );
    private static final SimpleCommandExceptionType TOO_MUCH_ARGS_EXCEPTION = new SimpleCommandExceptionType(
            new TextComponentTranslation("argument.feature.args.exceed")
    );
    private static final SimpleCommandExceptionType ARGS_SEPARATOR_EXCEPTION = new SimpleCommandExceptionType(
            new TextComponentTranslation("argument.feature.args.separator")
    );
    private static final SimpleCommandExceptionType UNCLOSED_ARGS_EXCEPTION = new SimpleCommandExceptionType(
            new TextComponentTranslation("argument.feature.args.unclosed")
    );
    private static final SimpleCommandExceptionType INVALID_ARGS_EXCEPTION = new SimpleCommandExceptionType(
            new TextComponentTranslation("argument.feature.args.invalid")
    );
    private static final SimpleCommandExceptionType LIST_SEPARATOR_EXCEPTION = new SimpleCommandExceptionType(
            new TextComponentTranslation("argument.feature.args.list.separator")
    );
    private static final SimpleCommandExceptionType UNCLOSED_LIST_EXCEPTION = new SimpleCommandExceptionType(
            new TextComponentTranslation("argument.feature.args.list.unclosed")
    );
    public static final DynamicCommandExceptionType FLUID_NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType(
            (str) -> new TextComponentTranslation("argument.feature.args.fluid.invalid", str)
    );
    public static final DynamicCommandExceptionType PREDICATE_NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType(
            (str) -> new TextComponentTranslation("argument.feature.args.predicate.invalid", str)
    );

    private static final Map<Class<?>, IArgsParser<?>> dispatcher = new HashMap<>();
    private static final Map<Class<?>, IArgsParserT<?>> dispatcherT = new HashMap<>();

    static {
        dispatcher.put(int.class, StringReader::readInt);
        dispatcher.put(boolean.class, StringReader::readBoolean);
        dispatcher.put(double.class, StringReader::readDouble);
        dispatcher.put(IBlockState.class, (reader) -> {
            BlockStateParser parser = new BlockStateParser(reader, false);
            parser.readBlock();
            if (reader.canRead() && reader.peek() == '[')
                parser.readProperties();
            BlockStateInput input = new BlockStateInput(parser.getState(), parser.getProperties().keySet(), null);
            return input.getBlockState();
        });
        dispatcher.put(Block.class, (reader) -> {
            BlockStateParser parser = new BlockStateParser(reader, false);
            parser.readBlock();
            BlockStateInput input = new BlockStateInput(parser.getState(), Collections.emptySet(), null);
            return input.getBlockState().getBlock();
        });
        dispatcher.put(IBlockState[].class, (reader) -> {
            reader.expect('[');
            reader.skipWhitespace();
            if (!reader.canRead())
                throw UNCLOSED_LIST_EXCEPTION.createWithContext(reader);
            List<IBlockState> list = new ArrayList<>();
            if (reader.peek() == ']')
                reader.skip();
            else while (true) {
                IBlockState blockState = parse(reader, IBlockState.class);
                list.add(blockState);
                reader.skipWhitespace();
                if (!reader.canRead())
                    throw UNCLOSED_LIST_EXCEPTION.createWithContext(reader);
                if (reader.peek() == ']') {
                    reader.skip();
                    break;
                } else if (reader.peek() == ',') {
                    reader.skip();
                    reader.skipWhitespace();
                } else {
                    throw LIST_SEPARATOR_EXCEPTION.createWithContext(reader);
                }
            }
            return list.toArray(new IBlockState[] {});
        });
        dispatcher.put(Fluid.class, (reader) -> {
            ResourceLocation fluidId = ResourceLocation.read(reader);
            if (IRegistry.field_212619_h.func_212607_c(fluidId)) {
                return IRegistry.field_212619_h.get(fluidId);
            } else {
                throw FLUID_NOT_FOUND_EXCEPTION.createWithContext(reader, fluidId.toString());
            }
        });
        dispatcherT.put(List.class, (reader, types) -> {
            if (types[0].equals(Block.class)) {
                reader.expect('[');
                reader.skipWhitespace();
                if (!reader.canRead())
                    throw UNCLOSED_LIST_EXCEPTION.createWithContext(reader);
                List<Block> list = new ArrayList<>();
                if (reader.peek() == ']')
                    reader.skip();
                else while (true) {
                    Block block = parse(reader, Block.class);
                    list.add(block);
                    reader.skipWhitespace();
                    if (!reader.canRead())
                        throw UNCLOSED_LIST_EXCEPTION.createWithContext(reader);
                    if (reader.peek() == ']') {
                        reader.skip();
                        break;
                    } else if (reader.peek() == ',') {
                        reader.skip();
                        reader.skipWhitespace();
                    } else {
                        throw LIST_SEPARATOR_EXCEPTION.createWithContext(reader);
                    }
                }
                return list;
            }
            return null;
        });
        dispatcherT.put(Predicate.class, (reader, types) -> {
            if (types[0].equals(IBlockState.class)) {
                if (reader.canRead() && reader.peek() == '$') {
                    reader.skip();
                    String str = reader.readUnquotedString();
                    if (str.equals("IS_ROCK")) {
                        return MinableConfig.IS_ROCK;
                    } else {
                        throw PREDICATE_NOT_FOUND_EXCEPTION.createWithContext(reader, str);
                    }
                }
                Block block = parse(reader, Block.class);
                return BlockMatcher.forBlock(block);
            }
            return null;
        });
    }

    public static <T> T parse(StringReader reader, Class<T> clazz, Type... types) throws CommandSyntaxException {
        if (types.length == 0)
            return (T) dispatcher.get(clazz).parse(reader);
        else
            return (T) dispatcherT.get(clazz).parse(reader, types);
    }

    public static Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggester;
    private final List<ArgsResult> candidate = new ArrayList<>();

    public FeatureArgsParser(Constructor<?>[] list) {
        for (Constructor<?> constructor : list)
            this.candidate.add(new ArgsResult(constructor));
    }

    public Feature<? extends IFeatureConfig> parseFeature(StringReader reader) throws CommandSyntaxException {
        Feature<? extends IFeatureConfig> feature = null;
        for (ArgsResult result : this.candidate) {
            if (result.isEmpty()) {
                feature = FeatureFactory.getFeature(result.constructor);
                break;
            }
        }
        suggester = (builder) -> {
            for (ArgsResult result : this.candidate)
                builder.suggest("(", result.getArgsTooltip(0));
            return builder.buildFuture();
        };
        if (reader.canRead() && reader.peek() == '(') {
            this.readArgs(reader);
            for (ArgsResult result : this.candidate) {
                if (result.isDone()) {
                    feature = FeatureFactory.getFeature(result.constructor, result.parsedArgs);
                    break;
                }
            }
        }
        if (feature == null)
            throw INVALID_ARGS_EXCEPTION.createWithContext(reader);
        return feature;
    }

    public IFeatureConfig parseConfig(StringReader reader) throws CommandSyntaxException {
        IFeatureConfig config = null;
        for (ArgsResult result : this.candidate) {
            if (result.isEmpty()) {
                config = FeatureFactory.getConfig(result.constructor);
                break;
            }
        }
        if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
            suggester = (builder) -> {
                for (ArgsResult result : this.candidate)
                    builder.suggest("C(", result.getArgsTooltip(0));
                return builder.buildFuture();
            };
            if (reader.canRead(2) && reader.peek() == 'C' && reader.peek(1) == '(') {
                reader.skip();
                this.readArgs(reader);
                for (ArgsResult result : this.candidate) {
                    if (result.isDone()) {
                        config = FeatureFactory.getConfig(result.constructor, result.parsedArgs);
                        break;
                    }
                }
            }
        }
        if (config == null)
            throw INVALID_ARGS_EXCEPTION.createWithContext(reader);
        return config;
    }

    private void readArgs(StringReader reader) throws CommandSyntaxException {
        reader.skip();
        reader.skipWhitespace();
        if (reader.canRead() && reader.peek() == ')') {
            reader.skip();
            suggester = SuggestionsBuilder::buildFuture;
            return;
        }
        int argsIndex = 0;
        while (true) {
            int start = reader.getCursor();
            int end = start;
            List<CommandSyntaxException> es = new ArrayList<>();
            Iterator<ArgsResult> it = this.candidate.iterator();
            while (it.hasNext()) {
                ArgsResult result = it.next();
                Class<?>[] classList = result.constructor.getParameterTypes();
                if (classList.length == 0 && argsIndex == 0) continue;  // no args
                if (argsIndex >= classList.length) {
                    it.remove();
                    continue;
                }
                reader.setCursor(start);
                Class<?> clazz = classList[argsIndex];
                Type t = result.constructor.getGenericParameterTypes()[argsIndex];
                try {
                    if (t instanceof ParameterizedType) {
                        Type[] types = ((ParameterizedType) t).getActualTypeArguments();
                        result.parsedArgs[argsIndex] = parse(reader, clazz, types);
                    } else {
                        result.parsedArgs[argsIndex] = parse(reader, clazz);
                    }
                    end = Math.max(reader.getCursor(), end);
                } catch (CommandSyntaxException e) {
                    it.remove();
                    es.add(e);
                }
            }
            if (this.candidate.isEmpty()) {
                if (es.size() == 1)
                    throw es.get(0);
                else if (es.size() > 1)
                    throw NO_CANDIDATE_EXCEPTION.createWithContext(reader);
                else
                    throw TOO_MUCH_ARGS_EXCEPTION.createWithContext(reader);
            }
            reader.setCursor(end);
            reader.skipWhitespace();
            int finalIndex = argsIndex;
            suggester = (builder) -> {
                for (ArgsResult result : this.candidate) {
                    if (finalIndex == Math.max(result.constructor.getParameterTypes().length - 1, 0))  // full args
                        builder.suggest(")");
                    else
                        builder.suggest(",", result.getArgsTooltip(finalIndex + 1));
                }
                return builder.buildFuture();
            };
            if (!reader.canRead())
                throw UNCLOSED_ARGS_EXCEPTION.createWithContext(reader);
            if (reader.peek() == ')') {
                reader.skip();
                suggester = SuggestionsBuilder::buildFuture;
                break;
            } else if (reader.peek() == ',') {
                reader.skip();
                reader.skipWhitespace();
                ++argsIndex;
            } else {
                throw ARGS_SEPARATOR_EXCEPTION.createWithContext(reader);
            }
        }
    }

}
