package shcm.shsupercm.forge.simplycrafting.common;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

public class TESimplyCrafter extends TileEntity implements ITickable {
    protected CraftingItemHandler craftingItemHandler = new CraftingItemHandler();
    protected IRecipe recipe = null;

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if(compound.hasKey("items"))
            craftingItemHandler.deserializeNBT(compound.getCompoundTag("items"));

        if(compound.hasKey("recipe"))
            this.recipe = CraftingManager.getRecipe(new ResourceLocation(compound.getString("recipe")));
        else
            this.recipe = null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("items", craftingItemHandler.serializeNBT());

        compound.removeTag("recipe");
        if(recipe != null)
            compound.setString("recipe", recipe.getRegistryName().toString());

        return super.writeToNBT(compound);
    }

    @Override
    public void update() {
        if(world.isRemote) {
            world.spawnParticle(EnumParticleTypes.FLAME, getPos().getX() + 0.5f, pos.getY() + 1.5f, pos.getZ() + 0.5f, 0.0001d, 0.0001d, 0.0001d);
        }
        if(!world.isRemote) {
            if(recipe == null)
                craftingItemHandler.getStacks().set(9, ItemStack.EMPTY);
            else
                craftingItemHandler.getStacks().set(9, recipe.getRecipeOutput());
        }
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return hasCapability(capability, facing) ? (T) this.craftingItemHandler : null;
    }

    public class CraftingItemHandler extends ItemStackHandler {
        private DummyInventoryCrafting dummyInventoryCrafting;

        public CraftingItemHandler() {
            super(10);
            this.dummyInventoryCrafting = new DummyInventoryCrafting(this.stacks);
        }

        protected NonNullList<ItemStack> getStacks() {
            return stacks;
        }

        public void updateRecipe() {
            dummyInventoryCrafting.stackList = stacks;
            recipe = CraftingManager.findMatchingRecipe(dummyInventoryCrafting, world);
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if(slot == 9 || (recipe != null && !recipe.getIngredients().get(slot).apply(stacks.get(slot))))
                return super.extractItem(slot, amount, simulate);
            else
                return ItemStack.EMPTY;
        }
    }

    public class CraftingResultItemHandler extends ItemStackHandler {
        public CraftingResultItemHandler() {
            super(1);
        }

        protected NonNullList<ItemStack> getStacks() {
            return stacks;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return stack;
        }
    }
}
