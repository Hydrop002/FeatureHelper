package org.utm.featurehelper.network;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.network.NetworkEvent;
import org.utm.featurehelper.render.RenderTrail;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MessageCaveTrail {

    public NBTTagCompound compound;

    public MessageCaveTrail(PacketBuffer buf) {
        this.compound = buf.readCompoundTag();
    }

    public void encode(PacketBuffer buf) {
        buf.writeCompoundTag(this.compound);
    }

    public void handler(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            NBTTagList list = this.compound.getList("TunnelList", 10);
            RenderTrail.caveRenderer.posList = new ArrayList<>();
            for (int i = 0; i < list.size(); ++i) {
                NBTTagCompound listTag = list.getCompound(i);
                NBTTagList subList = listTag.getList("PosList", 10);
                List<Vec3d> tunnel = new ArrayList<>();
                for (int j = 0; j < subList.size(); ++j) {
                    NBTTagCompound posTag = subList.getCompound(j);
                    if (posTag.isEmpty())
                        tunnel.add(null);
                    else
                        tunnel.add(new Vec3d(
                                posTag.getDouble("x"),
                                posTag.getDouble("y"),
                                posTag.getDouble("z")
                        ));
                }
                RenderTrail.caveRenderer.posList.add(tunnel);
            }
        });
    }

}
