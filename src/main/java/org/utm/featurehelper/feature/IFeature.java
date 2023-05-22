package org.utm.featurehelper.feature;

import net.minecraft.world.gen.feature.WorldGenerator;

import java.lang.reflect.InvocationTargetException;

public interface IFeature {

    public WorldGenerator getFeature(Object... args) throws IllegalAccessException, InvocationTargetException, InstantiationException;

    public Class<?>[] getParameters();

}
