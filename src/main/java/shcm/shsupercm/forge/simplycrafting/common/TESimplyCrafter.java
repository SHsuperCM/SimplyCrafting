package shcm.shsupercm.forge.simplycrafting.common;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import shcm.shsupercm.forge.core.smart.ItemStackInventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TESimplyCrafter extends TileEntity implements ITickable {
    private DummyInventoryCrafting dummyInventoryCrafting = new DummyInventoryCrafting(null);
    private IRecipe recipe = null;
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
            } else
                return super.insertItem(slot, stack, simulate, container, facing);
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate, boolean container, EnumFacing facing) {
            if (slot == 9) {
                if (recipe != null && recipe.matches(dummyInventoryCrafting, world)) {
                    ItemStack result = recipe.getCraftingResult(dummyInventoryCrafting);
                    if (!simulate)
                        getInternalItemStackHandler().setStacks(recipe.getRemainingItems(dummyInventoryCrafting));

                    return result;
                }

                return ItemStack.EMPTY;
            }
            if (!container) {
                /*if (recipe != null && !recipe.getIngredients().get(slot).apply(getInternalItemStackHandler().getStackInSlot(slot)))
                    return super.extractItem(slot, amount, simulate, container, facing);*/
                return ItemStack.EMPTY;
            } else {
                return super.extractItem(slot, amount, simulate, container, facing);
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

    public void refreshRecipe() {
        dummyInventoryCrafting.stackList = inventory.getInternalItemStackHandler().getStacks();
        recipe = CraftingManager.findMatchingRecipe(dummyInventoryCrafting, world);
        markDirty();
    }
}
