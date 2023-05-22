package org.utm.featurehelper.feature;

import net.minecraft.world.gen.feature.WorldGenerator;

public interface IFeature {

    public WorldGenerator getFeature(Object... args);

    public Object[] getArgs();

}
