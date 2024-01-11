package org.utm.featurehelper.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.utm.featurehelper.render.RenderRavineTrail;

import java.util.ArrayList;
import java.util.List;

public class MessageRavineTrail implements IMessage {

    public NBTTagCompound pos;

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, this.pos);
    }

    public static IMessageHandler<MessageRavineTrail, IMessage> handler = new IMessageHandler<MessageRavineTrail, IMessage>() {

        @Override
        public IMessage onMessage(MessageRavineTrail message, MessageContext ctx) {
            NBTTagList list = message.pos.getTagList("TunnelList", 10);
            RenderRavineTrail.instance.posList = new ArrayList<List<double[]>>();
            for (int i = 0; i < list.tagCount(); ++i) {
                NBTTagCompound listTag = list.getCompoundTagAt(i);
                NBTTagList subList = listTag.getTagList("PosList", 10);
                List<double[]> tunnel = new ArrayList<double[]>();
                for (int j = 0; j < subList.tagCount(); ++j) {
                    NBTTagCompound posTag = subList.getCompoundTagAt(j);
                    if (posTag.hasNoTags())
                        tunnel.add(null);
                    else
                        tunnel.add(new double[] {
                                posTag.getDouble("x"),
                                posTag.getDouble("y"),
                                posTag.getDouble("z"),
                        });
                }
                RenderRavineTrail.instance.posList.add(tunnel);
            }
            return null;
        }

    };

}
