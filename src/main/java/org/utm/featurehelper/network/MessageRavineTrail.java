package org.utm.featurehelper.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.utm.featurehelper.render.RenderTrail;

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
            RenderTrail.ravineRenderer.posList = new ArrayList<List<Vec3>>();
            for (int i = 0; i < list.tagCount(); ++i) {
                NBTTagCompound listTag = list.getCompoundTagAt(i);
                NBTTagList subList = listTag.getTagList("PosList", 10);
                List<Vec3> tunnel = new ArrayList<Vec3>();
                for (int j = 0; j < subList.tagCount(); ++j) {
                    NBTTagCompound posTag = subList.getCompoundTagAt(j);
                    if (posTag.hasNoTags())
                        tunnel.add(null);
                    else
                        tunnel.add(new Vec3(
                                posTag.getDouble("x"),
                                posTag.getDouble("y"),
                                posTag.getDouble("z")
                        ));
                }
                RenderTrail.ravineRenderer.posList.add(tunnel);
            }
            return null;
        }

    };

}
