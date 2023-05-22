package org.utm.featurehelper.feature;

import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FeatureFactory {

    private static Map<String, IFeature> factory = new HashMap<String, IFeature>();

    static {
        factory.put(getName(WorldGenLakes.class), new IFeature() {
            @Override
            public WorldGenerator getFeature(Object... args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
                return (WorldGenerator) WorldGenLakes.class.getDeclaredConstructors()[0].newInstance(args);
            }
            @Override
            public Class<?>[] getParameters() {
                return WorldGenLakes.class.getDeclaredConstructors()[0].getParameterTypes();
            }
        });
    }

    public static String getName(Class<? extends WorldGenerator> clazz) {
        return clazz.getSimpleName().split("WorldGen")[1];
    }

    public static Set<String> getNameSet() {
        return factory.keySet();
    }

    public static Class<?>[] getParameters(String name) {
        return factory.get(name).getParameters();
    }

    public static WorldGenerator getFeature(String name, Object... args) {
        try {
            return factory.get(name).getFeature(args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

}
