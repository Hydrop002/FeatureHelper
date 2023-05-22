package org.utm.featurehelper.feature;

import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FeatureFactory {

    private static Map<String, IFeature> factory = new HashMap<String, IFeature>();

    static {
        factory.put(getName(WorldGenLakes.class), new IFeature() {
            @Override
            public WorldGenerator getFeature(Object... args) {
                return null;
            }
            @Override
            public Object[] getArgs() {
                return new Object[0];
            }
        });
    }

    public static String getName(Class<? extends WorldGenerator> clazz) {
        return clazz.getSimpleName().split("WorldGen")[1];
    }

    public static Set<String> getNameSet() {
        return factory.keySet();
    }

    public static WorldGenerator getFeature(String name, Object... args) {
        return factory.get(name).getFeature(args);
    }

}
