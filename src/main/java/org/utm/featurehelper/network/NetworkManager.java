package org.utm.featurehelper.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.utm.featurehelper.FeatureHelperMod;

public class NetworkManager {

    private static final String PROTOCOL_VERSION = "1.0";

    public static SimpleChannel instance = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(FeatureHelperMod.MODID, "render_channel"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

}
