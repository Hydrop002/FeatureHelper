package org.utm.featurehelper.network;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraftforge.fml.network.NetworkEvent;
import org.utm.featurehelper.render.RenderBoundingBox;

import java.util.ArrayList;
import java.util.function.Supplier;

public class MessageBoundingBox {

    public NBTTagCompound compound;

    public MessageBoundingBox() {}

    public MessageBoundingBox(PacketBuffer buf) {
        this.compound = buf.readCompoundTag();
    }

    public void encode(PacketBuffer buf) {
        buf.writeCompoundTag(this.compound);
    }

    public void handler(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            int[] bb = this.compound.getIntArray("lastBB");
            if (bb.length == 0)
                RenderBoundingBox.instance.bb = null;
            else
                RenderBoundingBox.instance.bb = new MutableBoundingBox(bb);
            NBTTagList list = this.compound.getList("BBList", 11);
            RenderBoundingBox.instance.bbList = new ArrayList<>();
            for (int i = 0; i < list.size(); ++i) {
                bb = list.getIntArray(i);
                if (bb.length == 0)
                    RenderBoundingBox.instance.bbList.add(null);
                else
                    RenderBoundingBox.instance.bbList.add(new MutableBoundingBox(bb));
            }
        });
    }

}
