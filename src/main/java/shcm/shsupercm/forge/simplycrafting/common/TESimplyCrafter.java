package shcm.shsupercm.forge.simplycrafting.common;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import shcm.shsupercm.forge.core.smart.ItemStackInventory;
import shcm.shsupercm.forge.simplycrafting.utility.CraftingSimplyHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TESimplyCrafter extends TileEntity implements ITickable {
    private IRecipe recipe = null;
    protected ItemStackInventory inventory = new ItemStackInventory(new ItemStackInventory.ExposedItemStackHandler(10) {
        @Override
        public int getSlotLimit(int slot) {
            return slot >= 0 && slot <= 8 ? 1 : super.getSlotLimit(slot);
        }
    }) {
        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate, boolean container, EnumFacing facing) {
            if (slot == 9)
                return stack;

            return super.insertItem(slot, stack, simulate, container, facing);
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate, boolean container, EnumFacing facing) {
            return super.extractItem(slot, amount, simulate, container, facing);
            //todo if slot 9 only, craft. if not , check if items can escape the filter
        }

        @Override
        protected boolean autoHandleFiltering() {
            return true;
        }

        @Override
        protected boolean shouldSlotHandleFiltering(int slot, boolean container, EnumFacing facing) {
            return slot >= 0 && slot <= 8;
        }

        @Override
        protected boolean shouldReplaceFilter(int slot, ItemStack newFilter, boolean container, EnumFacing facing) {
            return slot >= 0 && slot <= 8 && container;
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
            if(recipe != null) {
                CraftingSimplyHelper items = CraftingSimplyHelper.wrap(3, 3, inventory.getInternalItemStackHandler().getStacks());
                if (recipe.matches(items, world)) {
                    ItemStack result = recipe.getCraftingResult(items);

                    NonNullList<ItemStack> remainingItems = recipe.getRemainingItems(items);
                    for (int i = 0; i < remainingItems.size(); i++)
                        if (remainingItems.get(i).isEmpty())
                            inventory.getInternalItemStackHandler().getStackInSlot(i).shrink(1);
                        else
                            inventory.getInternalItemStackHandler().setStackInSlot(i, remainingItems.get(i));

                    inventory.getInternalItemStackHandler().setStackInSlot(9, result);
                }
            }

            if(recipe == null)
                inventory.getInternalItemStackHandler().getStacks().set(9, ItemStack.EMPTY);
            else
                inventory.getInternalItemStackHandler().setStackInSlot(9, recipe.getRecipeOutput());
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
        recipe = CraftingSimplyHelper.findRecipe(3, 3, inventory.getInternalItemStackHandler().getStacks(), world);
        markDirty();
    }

    public void dropItems(World world) {
        for(ItemStack item : inventory.getInternalItemStackHandler().getStacks())
            world.spawnEntity(new EntityItem(world, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, item));
    }
}
