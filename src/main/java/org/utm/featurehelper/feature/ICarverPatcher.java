package org.utm.featurehelper.feature;

import net.minecraft.util.math.Vec3d;

public interface ICarverPatcher {

    boolean updateAndCarve();

    Vec3d getPos();

}
