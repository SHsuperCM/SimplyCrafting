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
import shcm.shsupercm.forge.core.smart.ItemStackInventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

public class TESimplyCrafter extends TileEntity implements ITickable {
    //protected CraftingItemHandler craftingItemHandler = new CraftingItemHandler();
    private DummyInventoryCrafting dummyInventoryCrafting = new DummyInventoryCrafting(null);
    protected IRecipe recipe = null;
    protected ItemStackInventory inventory = new ItemStackInventory(new ItemStackInventory.ExposedItemStackHandler(10)) {
        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate, boolean container, EnumFacing facing) {
            if(slot == 9)
                return stack;
            if(!container) {
                if(recipe != null && recipe.getIngredients().get(slot).apply(stack))
                    return super.insertItem(slot, stack, simulate, container, facing);
                else
                    return stack;
            } else {
                ItemStack item = super.insertItem(slot, stack, simulate, container, facing);

                dummyInventoryCrafting.stackList = getInternalItemStackHandler().getStacks();
                recipe = CraftingManager.findMatchingRecipe(dummyInventoryCrafting, world);

                return item;
            }
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate, boolean container, EnumFacing facing) {
            if(slot == 9) {
                if(recipe.matches(dummyInventoryCrafting, world)) {
                    ItemStack result = recipe.getCraftingResult(dummyInventoryCrafting);
                    if(!simulate)
                        getInternalItemStackHandler().setStacks(recipe.getRemainingItems(dummyInventoryCrafting));

                    return result;
                }

                return ItemStack.EMPTY;
            }
            if(!container) {
                if(recipe != null && !recipe.getIngredients().get(slot).apply(getInternalItemStackHandler().getStackInSlot(slot)))
                    return super.extractItem(slot, amount, simulate, container, facing);
                return ItemStack.EMPTY;
            } else {
                ItemStack item = super.extractItem(slot, amount, simulate, container, facing);
                if(!simulate) {
                    dummyInventoryCrafting.stackList = getInternalItemStackHandler().getStacks();
                    recipe = CraftingManager.findMatchingRecipe(dummyInventoryCrafting, world);
                }
                return item;
            }
        }

    };

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if(compound.hasKey("items"))
            inventory.getInternalItemStackHandler().deserializeNBT(compound.getCompoundTag("items"));

        if(compound.hasKey("recipe"))
            this.recipe = CraftingManager.getRecipe(new ResourceLocation(compound.getString("recipe")));
        else
            this.recipe = null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("items", inventory.getInternalItemStackHandler().serializeNBT());

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
            //TODO Balance Items
            if(recipe == null)
                inventory.getInternalItemStackHandler().getStacks().set(9, ItemStack.EMPTY);
            else
                inventory.getInternalItemStackHandler().getStacks().set(9, recipe.getRecipeOutput());
        }
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return hasCapability(capability, facing) ? (T) this.inventory.facing(facing) : null;
    }
}
