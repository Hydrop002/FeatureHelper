package org.utm.featurehelper.feature.patch;

import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.FossilsFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.Random;

public class FossilsPatcher extends FossilsFeature {

    private static final ResourceLocation STRUCTURE_SPINE_01 = new ResourceLocation("fossil/spine_1");
    private static final ResourceLocation STRUCTURE_SPINE_02 = new ResourceLocation("fossil/spine_2");
    private static final ResourceLocation STRUCTURE_SPINE_03 = new ResourceLocation("fossil/spine_3");
    private static final ResourceLocation STRUCTURE_SPINE_04 = new ResourceLocation("fossil/spine_4");
    private static final ResourceLocation STRUCTURE_SPINE_01_COAL = new ResourceLocation("fossil/spine_1_coal");
    private static final ResourceLocation STRUCTURE_SPINE_02_COAL = new ResourceLocation("fossil/spine_2_coal");
    private static final ResourceLocation STRUCTURE_SPINE_03_COAL = new ResourceLocation("fossil/spine_3_coal");
    private static final ResourceLocation STRUCTURE_SPINE_04_COAL = new ResourceLocation("fossil/spine_4_coal");
    private static final ResourceLocation STRUCTURE_SKULL_01 = new ResourceLocation("fossil/skull_1");
    private static final ResourceLocation STRUCTURE_SKULL_02 = new ResourceLocation("fossil/skull_2");
    private static final ResourceLocation STRUCTURE_SKULL_03 = new ResourceLocation("fossil/skull_3");
    private static final ResourceLocation STRUCTURE_SKULL_04 = new ResourceLocation("fossil/skull_4");
    private static final ResourceLocation STRUCTURE_SKULL_01_COAL = new ResourceLocation("fossil/skull_1_coal");
    private static final ResourceLocation STRUCTURE_SKULL_02_COAL = new ResourceLocation("fossil/skull_2_coal");
    private static final ResourceLocation STRUCTURE_SKULL_03_COAL = new ResourceLocation("fossil/skull_3_coal");
    private static final ResourceLocation STRUCTURE_SKULL_04_COAL = new ResourceLocation("fossil/skull_4_coal");
    private static final ResourceLocation[] FOSSILS = new ResourceLocation[]{STRUCTURE_SPINE_01, STRUCTURE_SPINE_02, STRUCTURE_SPINE_03, STRUCTURE_SPINE_04, STRUCTURE_SKULL_01, STRUCTURE_SKULL_02, STRUCTURE_SKULL_03, STRUCTURE_SKULL_04};
    private static final ResourceLocation[] FOSSILS_COAL = new ResourceLocation[]{STRUCTURE_SPINE_01_COAL, STRUCTURE_SPINE_02_COAL, STRUCTURE_SPINE_03_COAL, STRUCTURE_SPINE_04_COAL, STRUCTURE_SKULL_01_COAL, STRUCTURE_SKULL_02_COAL, STRUCTURE_SKULL_03_COAL, STRUCTURE_SKULL_04_COAL};

    public boolean func_212245_a(IWorld world, IChunkGenerator<? extends IChunkGenSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        Random random = world.getRandom();
        Rotation[] arotation = Rotation.values();
        Rotation rotation = arotation[random.nextInt(arotation.length)];
        int i = random.nextInt(FOSSILS.length);
        TemplateManager templatemanager = world.getSaveHandler().getStructureTemplateManager();
        Template template = templatemanager.getTemplateDefaulted(FOSSILS[i]);
        Template template1 = templatemanager.getTemplateDefaulted(FOSSILS_COAL[i]);
        PlacementSettings placementsettings = (new PlacementSettings()).setRotation(rotation).setRandom(random);
        BlockPos blockpos = template.transformedSize(rotation);
        int j = random.nextInt(16 - blockpos.getX());
        int k = random.nextInt(16 - blockpos.getZ());
        int l = 256;

        for (int i1 = 0; i1 < blockpos.getX(); ++i1) {
            for (int j1 = 0; j1 < blockpos.getX(); ++j1) {
                l = Math.min(l, world.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, pos.getX() + i1 + j, pos.getZ() + j1 + k));
            }
        }

        int k1 = Math.max(l - 15 - random.nextInt(10), 10);
        BlockPos blockpos1 = template.getZeroPositionWithTransform(pos.add(j, k1, k), Mirror.NONE, rotation);
        placementsettings.setIntegrity(0.9F);
        template.addBlocksToWorld(world, blockpos1, placementsettings, 2);
        placementsettings.setIntegrity(0.1F);
        template1.addBlocksToWorld(world, blockpos1, placementsettings, 2);
        return true;
    }

}
