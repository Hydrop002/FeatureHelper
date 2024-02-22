package org.utm.featurehelper.feature;

import net.minecraft.world.gen.feature.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class FeatureFactory {

    private static final Map<String, Class<? extends Feature<?>>> factory = new HashMap<>();

    static {
        factory.put(getName(DesertWellsFeature.class), DesertWellsFeature.class);
        factory.put(getName(DeadBushFeature.class), DeadBushFeature.class);
        factory.put(getName(PumpkinFeature.class), PumpkinFeature.class);
        factory.put(getName(BigRedMushroomFeature.class), BigRedMushroomFeature.class);
        factory.put(getName(GlowstoneFeature.class), GlowstoneFeature.class);
        factory.put(getName(DoublePlantFeature.class), DoublePlantFeature.class);  // DoublePlantConfig
        factory.put(getName(JungleGrassFeature.class), JungleGrassFeature.class);
        factory.put(getName(SphereReplaceFeature.class), SphereReplaceFeature.class);  // SphereReplaceConfig
        factory.put(getName(BlockWithContextFeature.class), BlockWithContextFeature.class);  // BlockWithContextConfig
        factory.put(getName(BlockBlobFeature.class), BlockBlobFeature.class);  // BlockBlobConfig
        factory.put(getName(DefaultFlowersFeature.class), DefaultFlowersFeature.class);
        factory.put(getName(PlainsFlowersFeature.class), PlainsFlowersFeature.class);
        factory.put(getName(SwampFlowersFeature.class), SwampFlowersFeature.class);
        factory.put(getName(ForestFlowersFeature.class), ForestFlowersFeature.class);
        factory.put(getName(IcebergFeature.class), IcebergFeature.class);  // IcebergConfig
        // RandomDefaultFeatureList.class
        factory.put(getName(IceAndSnowFeature.class), IceAndSnowFeature.class);
        factory.put(getName(EndCrystalTowerFeature.class), EndCrystalTowerFeature.class);
        factory.put(getName(MelonFeature.class), MelonFeature.class);
        factory.put(getName(FireFeature.class), FireFeature.class);
        factory.put(getName(CoralTreeFeature.class), CoralTreeFeature.class);
        factory.put(getName(CoralMushroomFeature.class), CoralMushroomFeature.class);
        factory.put(getName(CoralClawFeature.class), CoralClawFeature.class);
        factory.put(getName(LakesFeature.class), LakesFeature.class);  // LakesConfig
        factory.put(getName(KelpFeature.class), KelpFeature.class);
        factory.put(getName(MinableFeature.class), MinableFeature.class);  // MinableConfig
        factory.put(getName(EndGatewayFeature.class), EndGatewayFeature.class);  // EndGatewayConfig
        factory.put(getName(FossilsFeature.class), FossilsFeature.class);
        factory.put(getName(SeaPickleFeature.class), SeaPickleFeature.class);  // CountConfig
        factory.put(getName(DungeonsFeature.class), DungeonsFeature.class);
        // CompositeFeature.class
        // CompositeFlowerFeature.class
        factory.put(getName(BushFeature.class), BushFeature.class);  // BushConfig
        // TwoFeatureChoiceFeature.class
        factory.put(getName(WaterlilyFeature.class), WaterlilyFeature.class);
        factory.put(getName(CanopyTreeFeature.class), CanopyTreeFeature.class);  // constructor
        factory.put(getName(MegaPineTree.class), MegaPineTree.class);  // constructor
        factory.put(getName(MegaJungleFeature.class), MegaJungleFeature.class);  // constructor
        factory.put(getName(TreeFeature.class), TreeFeature.class);  // constructor*2
        factory.put(getName(JungleTreeFeature.class), JungleTreeFeature.class);  // constructor
        factory.put(getName(BigTreeFeature.class), BigTreeFeature.class);  // constructor
        factory.put(getName(TallTaigaTreeFeature.class), TallTaigaTreeFeature.class);  // constructor
        factory.put(getName(ShrubFeature.class), ShrubFeature.class);  // constructor
        factory.put(getName(SavannaTreeFeature.class), SavannaTreeFeature.class);  // constructor
        factory.put(getName(PointyTaigaTreeFeature.class), PointyTaigaTreeFeature.class);
        factory.put(getName(SwampTreeFeature.class), SwampTreeFeature.class);
        factory.put(getName(BirchTreeFeature.class), BirchTreeFeature.class);  // constructor
        factory.put(getName(EndPodiumFeature.class), EndPodiumFeature.class);  // constructor
        factory.put(getName(ReedFeature.class), ReedFeature.class);
        factory.put(getName(BlueIceFeature.class), BlueIceFeature.class);
        factory.put(getName(TallGrassFeature.class), TallGrassFeature.class);  // TallGrassConfig
        factory.put(getName(VoidStartPlatformFeature.class), VoidStartPlatformFeature.class);
        factory.put(getName(ChorusPlantFeature.class), ChorusPlantFeature.class);
        factory.put(getName(VinesFeature.class), VinesFeature.class);
        factory.put(getName(EndIslandFeature.class), EndIslandFeature.class);
        factory.put(getName(LiquidsFeature.class), LiquidsFeature.class);  // LiquidsConfig
        // RandomFeatureList.class
        factory.put(getName(CactusFeature.class), CactusFeature.class);
        factory.put(getName(IcePathFeature.class), IcePathFeature.class);  // FeatureRadiusConfig
        factory.put(getName(HellLavaFeature.class), HellLavaFeature.class);  // HellLavaConfig
        factory.put(getName(IceSpikeFeature.class), IceSpikeFeature.class);
        factory.put(getName(TaigaGrassFeature.class), TaigaGrassFeature.class);
        // RandomFeatureWithConfigFeature.class
        factory.put(getName(ReplaceBlockFeature.class), ReplaceBlockFeature.class);  // ReplaceBlockConfig
        factory.put(getName(BigBrownMushroomFeature.class), BigBrownMushroomFeature.class);
        factory.put(getName(SeaGrassFeature.class), SeaGrassFeature.class);  // SeaGrassConfig
        factory.put(getName(BonusChestFeature.class), BonusChestFeature.class);
    }

    private static String getName(Class<?> clazz) {
        return clazz.getSimpleName().split("Feature")[0];
    }

    public static Set<String> getNameSet() {
        return factory.keySet();
    }

    public static Constructor<?>[] getConstructorList(String name) {
        return factory.get(name).getDeclaredConstructors();
    }

    public static Constructor<?>[] getConfigConstructorList(String name) {
        Type t = factory.get(name).getGenericSuperclass();
        if (t instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) t;
            Class<?> clazz = (Class<?>) type.getActualTypeArguments()[0];
            return clazz.getDeclaredConstructors();
        } else {
            return getConfigConstructorList(getName((Class<?>) t));
        }
    }

    public static Feature<? extends IFeatureConfig> getFeature(Constructor<?> constructor, Object... args) {
        try {
            return (Feature<? extends IFeatureConfig>) constructor.newInstance(args);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            return null;
        }
    }

    public static IFeatureConfig getConfig(Constructor<?> constructor, Object... args) {
        try {
            return (IFeatureConfig) constructor.newInstance(args);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            return null;
        }
    }

}
