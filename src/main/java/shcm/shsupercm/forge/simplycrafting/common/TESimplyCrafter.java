package shcm.shsupercm.forge.simplycrafting.common;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import shcm.shsupercm.forge.core.api1.smart.ItemStackInventory;
import shcm.shsupercm.forge.simplycrafting.utility.CraftingSimplyHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TESimplyCrafter extends TileEntity {
    public IRecipe recipe = null;
    public ItemStackInventory inventory = new ItemStackInventory(new ItemStackInventory.ExposedItemStackHandler(10) {
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
            else {
                if(container) {
                    ItemStack result = super.insertItem(slot, stack, true, container, facing);
                    markDirty();
                    return result;
                } else {
                    if(recipe == null)
                        return stack;
                    return super.insertItem(slot, stack, simulate, container, facing);
                }
            }
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate, boolean container, EnumFacing facing) {
            if  (!container) {
                if (slot >= 0 && slot <= 8) {
                    if (doesItemMatchFilter(getInternalItemStackHandler().getStackInSlot(slot), slot))
                        return ItemStack.EMPTY;
                } else if (slot == 9) {
                    if (getInternalItemStackHandler().getStackInSlot(slot).isEmpty() && recipe != null) {
                        boolean matchesFilter = true;
                        for (int i = 0; i <= 8 && (matchesFilter = doesItemMatchFilter(getInternalItemStackHandler().getStackInSlot(i), i)); i++);
                        if(matchesFilter) {
                            CraftingSimplyHelper items = CraftingSimplyHelper.wrap(3, 3, inventory.getInternalItemStackHandler().getStacks());
                            if (recipe.matches(items, world)) {
                                ItemStack result = recipe.getCraftingResult(items);

                                NonNullList<ItemStack> remainingItems = recipe.getRemainingItems(items);
                                for (int i = 0; i < remainingItems.size(); i++) {
                                    ItemStack item = remainingItems.get(i);
                                    if (item.isEmpty())
                                        inventory.getInternalItemStackHandler().getStackInSlot(i).shrink(1);
                                    else
                                        inventory.getInternalItemStackHandler().setStackInSlot(i, item);
                                }

                                inventory.getInternalItemStackHandler().setStackInSlot(9, result);
                            }
                        }
                    }
                }
            }
            return super.extractItem(slot, amount, simulate, container, facing);
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot, boolean container, EnumFacing facing) {
            if(slot == 9 && !container && recipe != null)
                return recipe.getRecipeOutput();
            return super.getStackInSlot(slot, container, facing);
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
            inventory.deserializeNBT(compound.getCompoundTag("items"));

        if(compound.hasKey("recipe"))
            this.recipe = CraftingManager.getRecipe(new ResourceLocation(compound.getString("recipe")));
        else
            this.recipe = null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("items", inventory.serializeNBT());

        compound.removeTag("recipe");
        if(recipe != null)
            compound.setString("recipe", recipe.getRegistryName().toString());

        return super.writeToNBT(compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        deserializeNBT(pkt.getNbtCompound());
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
        recipe = CraftingSimplyHelper.findRecipe(3, 3, inventory.getFilterItemStackHandler().getStacks(), world);
        markDirty();
    }

    public void dropItems(World world) {
        for(ItemStack item : inventory.getInternalItemStackHandler().getStacks())
            world.spawnEntity(new EntityItem(world, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, item));
    }
}
