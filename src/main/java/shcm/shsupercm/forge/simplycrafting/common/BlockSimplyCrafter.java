package shcm.shsupercm.forge.simplycrafting.common;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import shcm.shsupercm.forge.core.smart.ItemStackInventory;
import shcm.shsupercm.forge.simplycrafting.SimplyCrafting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockSimplyCrafter extends Block {
    public BlockSimplyCrafter() {
        super(Material.WOOD, MapColor.WOOD);
        setHardness(0.75F);
        setCreativeTab(CreativeTabs.REDSTONE);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TESimplyCrafter();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote)
            playerIn.openGui(SimplyCrafting.INSTANCE, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());

        return true;
    }

    @Override
    public boolean hasTileEntity(@Nonnull final IBlockState state) {
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return super.createBlockState();
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if(!worldIn.isRemote)
            for(ItemStack item : (((ItemStackInventory.ExposedItemStackHandler)worldIn.getTileEntity(pos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).getStacks()))
                worldIn.spawnEntity(new EntityItem(worldIn, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, item));
        super.breakBlock(worldIn, pos, state);
    }
}