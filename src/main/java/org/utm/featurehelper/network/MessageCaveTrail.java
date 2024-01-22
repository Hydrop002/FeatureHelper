package org.utm.featurehelper.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import org.utm.featurehelper.render.RenderTrail;

import java.util.ArrayList;
import java.util.List;

public class MessageCaveTrail implements IMessage {

    public NBTTagCompound pos;

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, this.pos);
    }

    public static IMessageHandler<MessageCaveTrail, IMessage> handler = (message, ctx) -> {
        NBTTagList list = message.pos.getTagList("TunnelList", 10);
        RenderTrail.caveRenderer.posList = new ArrayList<>();
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound listTag = list.getCompoundTagAt(i);
            NBTTagList subList = listTag.getTagList("PosList", 10);
            List<Vec3d> tunnel = new ArrayList<>();
            for (int j = 0; j < subList.tagCount(); ++j) {
                NBTTagCompound posTag = subList.getCompoundTagAt(j);
                if (posTag.hasNoTags())
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
        return null;
    };

}
