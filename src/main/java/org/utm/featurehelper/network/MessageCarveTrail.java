package org.utm.featurehelper.network;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent;
import org.utm.featurehelper.render.RenderTrail;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MessageCarveTrail {

    public CompoundNBT compound;

    public MessageCarveTrail() {}

    public MessageCarveTrail(PacketBuffer buf) {
        this.compound = buf.readNbt();
    }

    public void encode(PacketBuffer buf) {
        buf.writeNbt(this.compound);
    }

    public void handler(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ListNBT list = this.compound.getList("TunnelList", 10);
            RenderTrail.instance.posList = new ArrayList<>();
            for (int i = 0; i < list.size(); ++i) {
                CompoundNBT listTag = list.getCompound(i);
                ListNBT subList = listTag.getList("PosList", 10);
                List<Vector3d> tunnel = new ArrayList<>();
                for (int j = 0; j < subList.size(); ++j) {
                    CompoundNBT posTag = subList.getCompound(j);
                    if (posTag.isEmpty())
                        tunnel.add(null);
                    else
                        tunnel.add(new Vector3d(
                                posTag.getDouble("x"),
                                posTag.getDouble("y"),
                                posTag.getDouble("z")
                        ));
                }
                RenderTrail.instance.posList.add(tunnel);
            }
        });
    }

}
