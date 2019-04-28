package shcm.shsupercm.forge.core.smart;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class ItemStackInventory {
    private final ExposedItemStackHandler itemStackHandler;

    private final Handler handlerContainer;
    private final Handler handlerFacingNull;
    private final HashMap<EnumFacing, Handler> handlerFacing = new HashMap<>();

    public ItemStackInventory(ExposedItemStackHandler itemStackHandler) {
        this.itemStackHandler = itemStackHandler;
        handlerContainer = new Handler(this, true, null);
        handlerFacingNull = new Handler(this, false, null);
        for (EnumFacing enumFacing : EnumFacing.values())
            handlerFacing.put(enumFacing, new Handler(this, false, enumFacing));
    }

    public final ItemStackHandler container() {
        return handlerContainer;
    }

    public final ItemStackHandler facing(EnumFacing facing) {
        if(facing == null)
            return handlerFacingNull;
        return handlerFacing.get(facing);
    }

    public final ExposedItemStackHandler getInternalItemStackHandler() {
        return itemStackHandler;
    }

    public static class ExposedItemStackHandler extends ItemStackHandler {
        public ExposedItemStackHandler(int size) {
            super(size);
        }

        public ExposedItemStackHandler(NonNullList<ItemStack> stacks) {
            super(stacks);
        }

        public NonNullList<ItemStack> getStacks() {
            return stacks;
        }

        public void setStacks(NonNullList<ItemStack> stacks) {
            this.stacks = stacks;
        }
    }

    private static class Handler extends ItemStackHandler {
        private final ItemStackInventory itemStackInventory;
        private final boolean container;
        private final EnumFacing facing;

        public Handler(ItemStackInventory itemStackInventory, boolean container, EnumFacing facing) {
            this.itemStackInventory = itemStackInventory;
            this.container = container;
            this.facing = facing;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return itemStackInventory.insertItem(slot, stack, simulate, container, facing);
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return itemStackInventory.extractItem(slot, amount, simulate, container, facing);
        }

        @Override
        public void setSize(int size) {
            itemStackInventory.itemStackHandler.setSize(size);
        }

        @Override
        public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
            itemStackInventory.itemStackHandler.setStackInSlot(slot, stack);
        }

        @Override
        public int getSlots() {
            return itemStackInventory.itemStackHandler.getSlots();
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            return itemStackInventory.itemStackHandler.getStackInSlot(slot);
        }

        @Override
        public int getSlotLimit(int slot) {
            return itemStackInventory.itemStackHandler.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return itemStackInventory.itemStackHandler.isItemValid(slot, stack);
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return itemStackInventory.itemStackHandler.serializeNBT();
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            itemStackInventory.itemStackHandler.deserializeNBT(nbt);
        }
    }

    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate, boolean container, EnumFacing facing) {
        return itemStackHandler.insertItem(slot, stack, simulate);
    }

    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate, boolean container, EnumFacing facing) {
        return itemStackHandler.extractItem(slot, amount, simulate);
    }
}
