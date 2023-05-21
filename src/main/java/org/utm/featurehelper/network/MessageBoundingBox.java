package org.utm.featurehelper.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import org.utm.featurehelper.render.RenderBoundingBox;

import java.util.ArrayList;

public class MessageBoundingBox implements IMessage {

    public NBTTagCompound bb;

    @Override
    public void fromBytes(ByteBuf buf) {
        this.bb = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, bb);
    }

    public static IMessageHandler<MessageBoundingBox, IMessage> handler = new IMessageHandler<MessageBoundingBox, IMessage>() {

        @Override
        public IMessage onMessage(MessageBoundingBox message, MessageContext ctx) {
            int[] bb = message.bb.getIntArray("lastBB");
            if (bb.length == 0)
                RenderBoundingBox.instance.bb = null;
            else
                RenderBoundingBox.instance.bb = new StructureBoundingBox(bb);
            NBTTagList list = message.bb.getTagList("BBList", 11);
            RenderBoundingBox.instance.bbList = new ArrayList<StructureBoundingBox>();
            for (int i = 0; i < list.tagCount(); ++i) {
                bb = list.func_150306_c(i);
                if (bb.length == 0)
                    RenderBoundingBox.instance.bbList.add(null);
                else
                    RenderBoundingBox.instance.bbList.add(new StructureBoundingBox(bb));
            }
            return null;
        }

    };

}
