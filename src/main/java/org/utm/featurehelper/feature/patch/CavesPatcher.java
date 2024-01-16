package org.utm.featurehelper.feature.patch;

import com.google.common.base.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.MapGenCaves;
import org.utm.featurehelper.network.MessageCaveTrail;
import org.utm.featurehelper.network.NetworkManager;

import java.util.ArrayList;
import java.util.List;

public class CavesPatcher extends MapGenCaves {

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
    private int fork;
    private boolean steep;

    private static List<CavesPatcher> list = new ArrayList<CavesPatcher>();
    private static List<List<Vec3>> posList = new ArrayList<List<Vec3>>();

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
        this.fork = this.rand.nextInt(this.length / 2) + this.length / 4;
        this.steep = this.rand.nextInt(6) == 0;

        if (this.debug) {
            list.add(this);
            List<Vec3> subList = new ArrayList<Vec3>();
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
        float cosPitch = MathHelper.cos(this.pitch);
        float sinPitch = MathHelper.sin(this.pitch);
        this.x += MathHelper.cos(this.yaw) * cosPitch;
        this.y += sinPitch;
        this.z += MathHelper.sin(this.yaw) * cosPitch;
        if (this.steep) {
            this.pitch *= 0.92F;
        } else {
            this.pitch *= 0.7F;
        }
        this.pitch += this.pitchDiff * 0.1F;
        this.yaw += this.yawDiff * 0.1F;
        this.pitchDiff *= 0.9F;
        this.yawDiff *= 0.75F;
        this.pitchDiff += (this.rand.nextFloat() - this.rand.nextFloat()) * this.rand.nextFloat() * 2;
        this.yawDiff += (this.rand.nextFloat() - this.rand.nextFloat()) * this.rand.nextFloat() * 4;
        if (!this.isRoom && this.index == this.fork && this.radius > 1 && this.length > 0) {
            if (this.debug) {
                list.remove(list.size() - 1);
                this.addPos(null);
            }
            new CavesPatcher().generate(worldObj, this.x, this.y, this.z, this.rand.nextFloat() * 0.5F + 0.5F, this.yaw - (float) Math.PI / 2F, this.pitch / 3F, this.index, this.length, 1, this.debug);
            new CavesPatcher().generate(worldObj, this.x, this.y, this.z, this.rand.nextFloat() * 0.5F + 0.5F, this.yaw + (float) Math.PI / 2F, this.pitch / 3F, this.index, this.length, 1, this.debug);
            return false;
        }
        if (this.isRoom || this.rand.nextInt(4) != 0) {
            int minX = MathHelper.floor_double(this.x - horRadius) - 1;
            int maxX = MathHelper.floor_double(this.x + horRadius) + 1;
            int minY = MathHelper.floor_double(this.y - verRadius) - 1;
            int maxY = MathHelper.floor_double(this.y + verRadius) + 1;
            int minZ = MathHelper.floor_double(this.z - horRadius) - 1;
            int maxZ = MathHelper.floor_double(this.z + horRadius) + 1;
            boolean foundWater = false;
            for (int i = minX; !foundWater && i < maxX; ++i) {
                for (int k = minZ; !foundWater && k < maxZ; ++k) {
                    for (int j = maxY + 1; !foundWater && j >= minY - 1; --j) {
                        if (j >= 0 && j < 256) {
                            Block block = worldObj.getBlockState(new BlockPos(i, j, k)).getBlock();
                            if (block == Blocks.flowing_water || block == Blocks.water)
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
                                if (offsetRatioY > -0.7 && offsetRatioX * offsetRatioX + offsetRatioY * offsetRatioY + offsetRatioZ * offsetRatioZ < 1) {
                                    BlockPos blockPos = new BlockPos(i, j, k);
                                    IBlockState blockState = worldObj.getBlockState(blockPos);
                                    IBlockState blockStateUp = Objects.firstNonNull(worldObj.getBlockState(blockPos.up()), Blocks.air.getDefaultState());
                                    BiomeGenBase biome = worldObj.getBiomeGenForCoords(blockPos);
                                    Block topBlock = this.isExceptionBiome(biome) ? Blocks.grass : biome.topBlock.getBlock();
                                    Block fillerBlock = biome.fillerBlock.getBlock();
                                    if (blockState.getBlock() == topBlock) {
                                        foundTop = true;
                                    }
                                    if (this.isDiggable(blockState.getBlock(), blockStateUp.getBlock()) || blockState.getBlock() == topBlock || blockState.getBlock() == fillerBlock) {
                                        if (j < 11) {  // forge bug: forge makes lava height 1 block down.
                                            worldObj.setBlockState(blockPos, Blocks.lava.getDefaultState(), 2);
                                        } else {
                                            worldObj.setBlockState(blockPos, Blocks.air.getDefaultState(), 2);
                                            if (blockStateUp.getBlock() == Blocks.sand) {
                                                worldObj.setBlockState(blockPos.up(), blockStateUp.getValue(BlockSand.VARIANT) == BlockSand.EnumType.RED_SAND ? Blocks.red_sandstone.getDefaultState() : Blocks.sandstone.getDefaultState(), 2);
                                            }
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
                if (this.isRoom) {
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

    private boolean isDiggable(Block block, Block blockUp) {
        return block == Blocks.stone || block == Blocks.dirt || block == Blocks.grass || block == Blocks.hardened_clay || block == Blocks.stained_hardened_clay || block == Blocks.sandstone || block == Blocks.red_sandstone || block == Blocks.mycelium || block == Blocks.snow_layer || (block == Blocks.sand || block == Blocks.gravel) && blockUp.getMaterial() != Material.water;
    }

    private boolean isExceptionBiome(BiomeGenBase biome) {
        if (biome == BiomeGenBase.beach) return true;
        if (biome == BiomeGenBase.desert) return true;
        return false;
    }

    public Vec3 getPos() {
        return new Vec3(this.x, this.y, this.z);
    }

    public static CavesPatcher getCurrent() {
        if (!list.isEmpty())
            return list.get(list.size() - 1);
        else
            return null;
    }

    public void addPos(Vec3 pos) {
        for (int i = posList.size() - 1; i >= 0; --i) {
            List<Vec3> list = posList.get(i);
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
        for (List<Vec3> tunnel : posList) {
            NBTTagList subList = new NBTTagList();
            for (Vec3 pos : tunnel) {
                if (pos == null)
                    subList.appendTag(new NBTTagCompound());
                else {
                    NBTTagCompound posTag = new NBTTagCompound();
                    posTag.setDouble("x", pos.xCoord);
                    posTag.setDouble("y", pos.yCoord);
                    posTag.setDouble("z", pos.zCoord);
                    subList.appendTag(posTag);
                }
            }
            NBTTagCompound listTag = new NBTTagCompound();
            listTag.setTag("PosList", subList);
            list.appendTag(listTag);
        }
        MessageCaveTrail message = new MessageCaveTrail();
        message.pos = new NBTTagCompound();
        message.pos.setTag("TunnelList", list);
        NetworkManager.instance.sendToDimension(message, worldObj.provider.getDimensionId());
    }

    public static void sendMessageToPlayer(EntityPlayerMP player, World world) {
        NBTTagList list = new NBTTagList();
        for (List<Vec3> tunnel : posList) {
            NBTTagList subList = new NBTTagList();
            for (Vec3 pos : tunnel) {
                if (pos == null)
                    subList.appendTag(new NBTTagCompound());
                else {
                    NBTTagCompound posTag = new NBTTagCompound();
                    posTag.setDouble("x", pos.xCoord);
                    posTag.setDouble("y", pos.yCoord);
                    posTag.setDouble("z", pos.zCoord);
                    subList.appendTag(posTag);
                }
            }
            NBTTagCompound listTag = new NBTTagCompound();
            listTag.setTag("PosList", subList);
            list.appendTag(listTag);
        }
        MessageCaveTrail message = new MessageCaveTrail();
        message.pos = new NBTTagCompound();
        if (world == worldObj)
            message.pos.setTag("TunnelList", list);
        else
            message.pos.setTag("TunnelList", new NBTTagList());
        NetworkManager.instance.sendTo(message, player);
    }

}
