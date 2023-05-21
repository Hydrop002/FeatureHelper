package org.utm.featurehelper.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.utm.featurehelper.FeatureHelperMod;

public class NetworkManager {

    public static SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel(FeatureHelperMod.MODID);

}
