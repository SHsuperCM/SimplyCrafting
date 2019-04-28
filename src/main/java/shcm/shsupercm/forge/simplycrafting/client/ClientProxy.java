package shcm.shsupercm.forge.simplycrafting.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import shcm.shsupercm.forge.simplycrafting.common.CommonProxy;

@Mod.EventBusSubscriber
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    @SuppressWarnings({"ConstantConditions"})
    public void init(FMLInitializationEvent event) {
        super.init(event);
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(simplyCrafter), 0, new ModelResourceLocation( Item.getItemFromBlock(simplyCrafter).getRegistryName(), "normal"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(simplyCrafter),0,new ModelResourceLocation(simplyCrafter.getRegistryName(),"normal"));
    }
}
