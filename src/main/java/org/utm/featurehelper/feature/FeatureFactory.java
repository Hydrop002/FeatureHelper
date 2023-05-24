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

    public static String getParaString(Class<?>[] classList) {
        String[] classNameList = new String[classList.length];
        for (int i = 0; i < classList.length; ++i) {
            classNameList[i] = classList[i].getSimpleName();
        }
        return Arrays.toString(classNameList);
    }

    public static Constructor<?>[] getConstructorList(String name) {
        return factory.get(name).getDeclaredConstructors();
    }

    public static WorldGenerator getFeature(Constructor<?> constructor, Object... args) {
        try {
            return (WorldGenerator) constructor.newInstance(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
