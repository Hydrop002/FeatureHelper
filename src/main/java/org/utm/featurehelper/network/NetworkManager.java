package org.utm.featurehelper.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.utm.featurehelper.FeatureHelperMod;

public class NetworkManager {

    public static SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel(FeatureHelperMod.MODID);

}
