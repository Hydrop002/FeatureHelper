package org.utm.featurehelper.feature;

import net.minecraft.world.gen.feature.*;
import org.utm.featurehelper.feature.patch.BlockBlobPatcher;

import java.lang.reflect.Constructor;
import java.util.*;

public class FeatureFactory {

    private static Map<String, Class<? extends WorldGenerator>> factory = new HashMap<String, Class<? extends WorldGenerator>>();

    static {
        factory.put(getName(WorldGenLakes.class), WorldGenLakes.class);
        factory.put(getName(WorldGenDungeons.class), WorldGenDungeons.class);
        factory.put(getName(WorldGenBigTree.class), WorldGenBigTree.class);
        factory.put(getName(WorldGenCanopyTree.class), WorldGenCanopyTree.class);
        factory.put(getName(WorldGenForest.class), WorldGenForest.class);
        factory.put(getName(WorldGenMegaJungle.class), WorldGenMegaJungle.class);
        factory.put(getName(WorldGenMegaPineTree.class), WorldGenMegaPineTree.class);
        factory.put(getName(WorldGenSavannaTree.class), WorldGenSavannaTree.class);
        factory.put(getName(WorldGenSwamp.class), WorldGenSwamp.class);
        factory.put(getName(WorldGenTaiga1.class), WorldGenTaiga1.class);
        factory.put(getName(WorldGenTaiga2.class), WorldGenTaiga2.class);
        factory.put(getName(WorldGenTrees.class), WorldGenTrees.class);
        factory.put(getName(WorldGenShrub.class), WorldGenShrub.class);
        factory.put(getName(WorldGenBigMushroom.class), WorldGenBigMushroom.class);
        factory.put(getName(WorldGenBlockBlob.class), BlockBlobPatcher.class);  // re-render
        factory.put(getName(WorldGenCactus.class), WorldGenCactus.class);
        factory.put(getName(WorldGenClay.class), WorldGenClay.class);
        factory.put(getName(WorldGenDeadBush.class), WorldGenDeadBush.class);
        factory.put(getName(WorldGenDesertWells.class), WorldGenDesertWells.class);
        factory.put(getName(WorldGenDoublePlant.class), WorldGenDoublePlant.class);
        factory.put("BonusChest", WorldGeneratorBonusChest.class);
        factory.put(getName(WorldGenFire.class), WorldGenFire.class);
        factory.put(getName(WorldGenFlowers.class), WorldGenFlowers.class);
        factory.put(getName(WorldGenGlowStone1.class), WorldGenGlowStone1.class);
        factory.put(getName(WorldGenGlowStone2.class), WorldGenGlowStone2.class);
        factory.put(getName(WorldGenHellLava.class), WorldGenHellLava.class);
        factory.put(getName(WorldGenIcePath.class), WorldGenIcePath.class);
        factory.put(getName(WorldGenIceSpike.class), WorldGenIceSpike.class);
        factory.put(getName(WorldGenLiquids.class), WorldGenLiquids.class);
        factory.put(getName(WorldGenMelon.class), WorldGenMelon.class);
        factory.put(getName(WorldGenMinable.class), WorldGenMinable.class);
        factory.put(getName(WorldGenPumpkin.class), WorldGenPumpkin.class);
        factory.put(getName(WorldGenReed.class), WorldGenReed.class);
        factory.put(getName(WorldGenSand.class), WorldGenSand.class);
        factory.put(getName(WorldGenSpikes.class), WorldGenSpikes.class);
        factory.put(getName(WorldGenTallGrass.class), WorldGenTallGrass.class);
        factory.put(getName(WorldGenVines.class), WorldGenVines.class);
        factory.put(getName(WorldGenWaterlily.class), WorldGenWaterlily.class);
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
