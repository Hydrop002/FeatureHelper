package org.utm.featurehelper.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import org.utm.featurehelper.render.RenderBoundingBox;

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

    public static IMessageHandler<MessageRenderControl, IMessage> handler = new IMessageHandler<MessageRenderControl, IMessage>() {

        @Override
        public IMessage onMessage(MessageRenderControl message, MessageContext ctx) {
            byte renderType = message.rc.getByte("renderType");
            boolean isRender = message.rc.getBoolean("render");
            if (renderType == 0)
                RenderBoundingBox.instance.isRender = isRender;
            else if (renderType == 1)
                ;
            else if (renderType == 2)
                ;
            else if (renderType == 3)
                ;
            return null;
        }

    };

}
