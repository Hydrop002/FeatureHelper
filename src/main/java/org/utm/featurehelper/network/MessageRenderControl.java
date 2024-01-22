package org.utm.featurehelper.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import org.utm.featurehelper.render.RenderBoundingBox;
import org.utm.featurehelper.render.RenderTrail;

public class MessageRenderControl implements IMessage {

    public NBTTagCompound rc;

    @Override
    public void fromBytes(ByteBuf buf) {
        this.rc = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, this.rc);
    }

    public static IMessageHandler<MessageRenderControl, IMessage> handler = (message, ctx) -> {
        byte renderType = message.rc.getByte("renderType");
        boolean isRender = message.rc.getBoolean("render");
        if (renderType == 0)
            RenderBoundingBox.instance.isRender = isRender;
        else if (renderType == 1)
            RenderTrail.caveRenderer.isRender = isRender;
        else if (renderType == 2)
            RenderTrail.caveHellRenderer.isRender = isRender;
        else if (renderType == 3)
            RenderTrail.ravineRenderer.isRender = isRender;
        return null;
    };

}
