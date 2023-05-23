package org.utm.featurehelper.feature;

import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.lang.reflect.Constructor;
import java.util.*;

public class FeatureFactory {

    private static Map<String, Class<? extends WorldGenerator>> factory = new HashMap<String, Class<? extends WorldGenerator>>();

    static {
        factory.put(getName(WorldGenLakes.class), WorldGenLakes.class);
    }

    public static String getName(Class<? extends WorldGenerator> clazz) {
        return clazz.getSimpleName().split("WorldGen")[1];
    }

    public static Set<String> getNameSet() {
        return factory.keySet();
    }

    public static List<Class<?>[]> getParameters(String name) {
        Constructor<?>[] constructors = factory.get(name).getDeclaredConstructors();
        List<Class<?>[]> list = new ArrayList<Class<?>[]>();
        for (Constructor<?> constructor : constructors) {
            list.add(constructor.getParameterTypes());
        }
        return list;
    }

    public static WorldGenerator getFeature(String name, Object... args) {
        try {
            Constructor<?>[] constructors = factory.get(name).getDeclaredConstructors();
            for (Constructor<?> constructor : constructors) {
                Class<?>[] validArgs = constructor.getParameterTypes();
                boolean valid = true;
                for (int i = 0; i < validArgs.length; ++i) {
                    if (!validArgs[i].isAssignableFrom(args[i].getClass())) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    return (WorldGenerator) constructor.newInstance(args);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
