package shcm.shsupercm.forge.simplycrafting;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import shcm.shsupercm.forge.simplycrafting.common.CommonProxy;

@Mod(modid = SimplyCrafting.MODID)
public class SimplyCrafting {
    public static final String MODID = "simplycrafting";

    @SidedProxy(clientSide = "shcm.shsupercm.forge." + MODID + ".client.ClientProxy", serverSide = "shcm.shsupercm.forge." + MODID + ".common.CommonProxy")
    public static CommonProxy PROXY;

    @Mod.Instance
    public static SimplyCrafting INSTANCE;

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event) {
        PROXY.init(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        PROXY.init(event);
    }

    @Mod.EventHandler
    public void init(FMLPostInitializationEvent event) {
        PROXY.init(event);
    }
}
