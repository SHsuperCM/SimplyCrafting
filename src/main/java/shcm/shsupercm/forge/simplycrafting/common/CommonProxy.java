package shcm.shsupercm.forge.simplycrafting.common;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import shcm.shsupercm.forge.simplycrafting.SimplyCrafting;
import shcm.shsupercm.forge.simplycrafting.client.GuiSimplyCrafter;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class CommonProxy {
    public static BlockSimplyCrafter simplyCrafter = new BlockSimplyCrafter();
    public static ItemBlock itemSimplyCrafter = new ItemBlock(simplyCrafter);

    public void init(FMLPreInitializationEvent event) {

    }

    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(SimplyCrafting.INSTANCE, new IGuiHandler() {
            @Nullable
            @Override
            public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
                TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

                if (tileEntity != null && tileEntity instanceof TESimplyCrafter && ID == 0) {
                    ((EntityPlayerMP)player).connection.sendPacket(new SPacketUpdateTileEntity(tileEntity.getPos(), 0, tileEntity.serializeNBT()));
                    return new ContainerSimplyCrafter((TESimplyCrafter) tileEntity, player.inventory);
                }

                return null;
            }

            @Nullable
            @Override
            public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
                TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

                if (tileEntity != null && tileEntity instanceof TESimplyCrafter && ID == 0)
                    return new GuiSimplyCrafter((TESimplyCrafter) tileEntity, player.inventory);

                return null;
            }
        });
    }

    public void init(FMLPostInitializationEvent event) {

    }

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(itemSimplyCrafter.setRegistryName("simply_crafter").setUnlocalizedName(itemSimplyCrafter.getRegistryName().toString()));
    }

    @SubscribeEvent
    public static void registerBlock(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(simplyCrafter.setRegistryName("simply_crafter").setUnlocalizedName(simplyCrafter.getRegistryName().toString()));

        GameRegistry.registerTileEntity(TESimplyCrafter.class,new ResourceLocation(simplyCrafter.getRegistryName() + "_tileentity"));
    }
}

