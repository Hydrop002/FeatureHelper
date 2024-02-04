package org.utm.featurehelper.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.utm.featurehelper.render.RenderBoundingBox;
import org.utm.featurehelper.render.RenderTrail;

import java.util.function.Supplier;

public class MessageRenderControl {

    public byte renderType;
    public boolean isRender;

    public MessageRenderControl() {}

    public MessageRenderControl(PacketBuffer buf) {  // decoder: buf -> message
        this.renderType = buf.readByte();
        this.isRender = buf.readBoolean();
    }

    public void encode(PacketBuffer buf) {  // encoder: (message, buf) -> {}
        buf.writeByte(this.renderType);
        buf.writeBoolean(this.isRender);
    }

    public void handler(Supplier<NetworkEvent.Context> ctx) {  // handler: (message, ctx) -> {}
        ctx.get().enqueueWork(() -> {
            if (this.renderType == 0)
                RenderBoundingBox.instance.isRender = this.isRender;
            else if (this.renderType == 1)
                RenderTrail.caveRenderer.isRender = this.isRender;
            else if (renderType == 2)
                RenderTrail.caveHellRenderer.isRender = this.isRender;
            else if (renderType == 3)
                RenderTrail.ravineRenderer.isRender = this.isRender;
        });
    }

}
