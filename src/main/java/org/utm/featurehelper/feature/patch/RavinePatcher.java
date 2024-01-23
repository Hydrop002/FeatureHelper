package org.utm.featurehelper.feature.patch;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.MapGenRavine;
import org.utm.featurehelper.network.MessageRavineTrail;
import org.utm.featurehelper.network.NetworkManager;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class RavinePatcher extends MapGenRavine {

    private final float[] rs = new float[1024];

    public static World worldObj;
    private double x;
    private double y;
    private double z;
    private float radius;
    private float yaw;
    private float pitch;
    private int index;
    private int length;
    private double heightFactor;
    private boolean debug;

    private float yawDiff;
    private float pitchDiff;
    private boolean isRoom;

    private static List<RavinePatcher> list = new ArrayList<>();
    private static List<List<Vec3d>> posList = new ArrayList<>();

    public void generate(World world, double x, double y, double z, float radius, float yaw, float pitch, int index, int length, double heightFactor, boolean debug) {
        worldObj = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.yaw = yaw;
        this.pitch = pitch;
        this.index = index;
        this.length = length;
        this.heightFactor = heightFactor;
        this.debug = debug;

        this.yawDiff = 0;
        this.pitchDiff = 0;
        if (this.length <= 0) {
            int maxLength = this.range * 16 - 16;
            this.length = maxLength - this.rand.nextInt(maxLength / 4);
        }
        this.isRoom = false;
        if (this.index == -1) {
            this.index = this.length / 2;
            this.isRoom = true;
        }
        float r = 1.0F;
        for (int i = 0; i < 256; ++i) {
            if (i == 0 || this.rand.nextInt(3) == 0) {
                r = 1.0F + this.rand.nextFloat() * this.rand.nextFloat();
            }
            this.rs[i] = r * r;
        }

        if (this.debug) {
            list.add(this);
            List<Vec3d> subList = new ArrayList<>();
            subList.add(this.getPos());
            posList.add(subList);
            this.addRoom();
            return;
        }
        while (this.index < this.length) {
            if (!this.addRoom())
                break;
        }
    }

    public boolean addRoom() {
        double horRadius = 1.5 + MathHelper.sin(this.index * (float) Math.PI / this.length) * this.radius;
        double verRadius = horRadius * this.heightFactor;
        horRadius *= this.rand.nextFloat() * 0.25 + 0.75;
        verRadius *= this.rand.nextFloat() * 0.25 + 0.75;
        float cosPitch = MathHelper.cos(this.pitch);
        float sinPitch = MathHelper.sin(this.pitch);
        this.x += MathHelper.cos(this.yaw) * cosPitch;
        this.y += sinPitch;
        this.z += MathHelper.sin(this.yaw) * cosPitch;
        this.pitch *= 0.7F;
        this.pitch += this.pitchDiff * 0.05F;
        this.yaw += this.yawDiff * 0.05F;
        this.pitchDiff *= 0.8F;
        this.yawDiff *= 0.5F;
        this.pitchDiff += (this.rand.nextFloat() - this.rand.nextFloat()) * this.rand.nextFloat() * 2;
        this.yawDiff += (this.rand.nextFloat() - this.rand.nextFloat()) * this.rand.nextFloat() * 4;
        if (this.isRoom || this.rand.nextInt(4) != 0) {
            int minX = MathHelper.floor(this.x - horRadius) - 1;
            int maxX = MathHelper.floor(this.x + horRadius) + 1;
            int minY = MathHelper.floor(this.y - verRadius) - 1;
            int maxY = MathHelper.floor(this.y + verRadius) + 1;
            int minZ = MathHelper.floor(this.z - horRadius) - 1;
            int maxZ = MathHelper.floor(this.z + horRadius) + 1;
            boolean foundWater = false;
            for (int i = minX; !foundWater && i < maxX; ++i) {
                for (int k = minZ; !foundWater && k < maxZ; ++k) {
                    for (int j = maxY + 1; !foundWater && j >= minY - 1; --j) {
                        if (j >= 0 && j < 256) {
                            Block block = worldObj.getBlockState(new BlockPos(i, j, k)).getBlock();
                            if (block == Blocks.FLOWING_WATER || block == Blocks.WATER)
                                foundWater = true;
                            if (j != minY - 1 && i != minX && i != maxX - 1 && k != minZ && k != maxZ - 1)
                                j = minY;
                        }
                    }
                }
            }
            if (!foundWater) {
                for(int i = minX; i < maxX; ++i) {
                    double offsetRatioX = (i + 0.5 - this.x) / horRadius;
                    for (int k = minZ; k < maxZ; ++k) {
                        double offsetRatioZ = (k + 0.5 - this.z) / horRadius;
                        boolean foundTop = false;
                        if (offsetRatioX * offsetRatioX + offsetRatioZ * offsetRatioZ < 1) {
                            for (int j = maxY; j > minY; --j) {
                                double offsetRatioY = (j - 0.5 - this.y) / verRadius;
                                if ((offsetRatioX * offsetRatioX + offsetRatioZ * offsetRatioZ) * this.rs[j - 1] + offsetRatioY * offsetRatioY / 6 < 1) {
                                    BlockPos blockPos = new BlockPos(i, j, k);
                                    Block block = worldObj.getBlockState(blockPos).getBlock();
                                    Biome biome = worldObj.getBiome(blockPos);
                                    Block topBlock = this.isExceptionBiome(biome) ? Blocks.GRASS : biome.topBlock.getBlock();
                                    Block fillerBlock = this.isExceptionBiome(biome) ? Blocks.DIRT : biome.fillerBlock.getBlock();
                                    if (block == topBlock) {
                                        foundTop = true;
                                    }
                                    if (block == Blocks.STONE || block == topBlock || block == fillerBlock) {
                                        if (j < 11) {
                                            worldObj.setBlockState(blockPos, FLOWING_LAVA, 2);
                                        } else {
                                            worldObj.setBlockState(blockPos, AIR, 2);
                                            if (foundTop && worldObj.getBlockState(blockPos.down()).getBlock() == fillerBlock) {
                                                worldObj.setBlockState(blockPos.down(), topBlock.getDefaultState(), 2);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (this.isRoom) {  // unreachable
                    return false;
                }
            }
        }
        ++this.index;
        if (this.debug) {
            this.addPos(this.getPos());
            if (this.index >= this.length) {
                list.remove(list.size() - 1);
                this.addPos(null);
            }
            sendMessage();
        }
        return true;
    }

    private boolean isExceptionBiome(Biome biome) {
        if (biome == Biomes.BEACH) return true;
        if (biome == Biomes.DESERT) return true;
        if (biome == Biomes.MUSHROOM_ISLAND) return true;
        if (biome == Biomes.MUSHROOM_ISLAND_SHORE) return true;
        return false;
    }

    public Vec3d getPos() {
        return new Vec3d(this.x, this.y, this.z);
    }

    @Nullable
    public static RavinePatcher getCurrent() {
        if (!list.isEmpty())
            return list.get(list.size() - 1);
        else
            return null;
    }

    public void addPos(@Nullable Vec3d pos) {
        for (int i = posList.size() - 1; i >= 0; --i) {
            List<Vec3d> list = posList.get(i);
            if (list.isEmpty() || list.get(list.size() - 1) != null) {
                list.add(pos);
                break;
            }
        }
    }

    public static void removeAll() {
        list.clear();
        posList.clear();
    }

    public static void sendMessage() {
        NBTTagList list = new NBTTagList();
        for (List<Vec3d> tunnel : posList) {
            NBTTagList subList = new NBTTagList();
            for (Vec3d pos : tunnel) {
                if (pos == null)
                    subList.appendTag(new NBTTagCompound());
                else {
                    NBTTagCompound posTag = new NBTTagCompound();
                    posTag.setDouble("x", pos.x);
                    posTag.setDouble("y", pos.y);
                    posTag.setDouble("z", pos.z);
                    subList.appendTag(posTag);
                }
            }
            NBTTagCompound listTag = new NBTTagCompound();
            listTag.setTag("PosList", subList);
            list.appendTag(listTag);
        }
        MessageRavineTrail message = new MessageRavineTrail();
        message.pos = new NBTTagCompound();
        message.pos.setTag("TunnelList", list);
        NetworkManager.instance.sendToDimension(message, worldObj.provider.getDimension());
    }

    public static void sendMessageToPlayer(EntityPlayerMP player, World world) {
        NBTTagList list = new NBTTagList();
        for (List<Vec3d> tunnel : posList) {
            NBTTagList subList = new NBTTagList();
            for (Vec3d pos : tunnel) {
                if (pos == null)
                    subList.appendTag(new NBTTagCompound());
                else {
                    NBTTagCompound posTag = new NBTTagCompound();
                    posTag.setDouble("x", pos.x);
                    posTag.setDouble("y", pos.y);
                    posTag.setDouble("z", pos.z);
                    subList.appendTag(posTag);
                }
            }
            NBTTagCompound listTag = new NBTTagCompound();
            listTag.setTag("PosList", subList);
            list.appendTag(listTag);
        }
        MessageRavineTrail message = new MessageRavineTrail();
        message.pos = new NBTTagCompound();
        if (world == worldObj)
            message.pos.setTag("TunnelList", list);
        else
            message.pos.setTag("TunnelList", new NBTTagList());
        NetworkManager.instance.sendTo(message, player);
    }

}