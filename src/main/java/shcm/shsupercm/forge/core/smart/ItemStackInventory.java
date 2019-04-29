package shcm.shsupercm.forge.core.smart;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.EnumMap;

public class ItemStackInventory implements INBTSerializable<NBTTagCompound> {
    private final ExposedItemStackHandler itemStackHandler;
    private ExposedItemStackHandler filterItemStackHandler;

    private final Handler handlerContainer;
    private final Handler handlerFacingNull;
    private final EnumMap<EnumFacing, Handler> handlerFacing = new EnumMap<EnumFacing, Handler>(EnumFacing.class);

    public ItemStackInventory(ExposedItemStackHandler itemStackHandler) {
        this.itemStackHandler = itemStackHandler;
        this.filterItemStackHandler = new ExposedItemStackHandler(10);
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

    public ExposedItemStackHandler getFilterItemStackHandler() {
        return filterItemStackHandler;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = itemStackHandler.serializeNBT();
        if(filterItemStackHandler != null && autoHandleFiltering())
            compound.setTag("filter", filterItemStackHandler.serializeNBT());
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound compound) {
        itemStackHandler.deserializeNBT(compound);
        if(autoHandleFiltering() && compound.hasKey("filter"))
            (filterItemStackHandler = new ExposedItemStackHandler(NonNullList.create())).deserializeNBT(compound.getCompoundTag("filter"));
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
        if(autoHandleFiltering() && shouldSlotHandleFiltering(slot, container, facing)) {
            if(shouldReplaceFilter(slot, stack, container, facing))
                filterItemStackHandler.setStackInSlot(slot, ItemHandlerHelper.copyStackWithSize(stack, 1));
            else if(!filterItemStackHandler.getStackInSlot(slot).isItemEqual(stack))
                return stack;
        }
        return itemStackHandler.insertItem(slot, stack, simulate);
    }

    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate, boolean container, EnumFacing facing) {
        ItemStack result = itemStackHandler.extractItem(slot, amount, simulate);
        if(autoHandleFiltering() && shouldSlotHandleFiltering(slot, container, facing)) {
            if(shouldReplaceFilter(slot, ItemStack.EMPTY, container, facing))
                filterItemStackHandler.setStackInSlot(slot, ItemStack.EMPTY);
        }
        return result;
    }

    protected boolean autoHandleFiltering() {
        return false;
    }

    protected boolean shouldSlotHandleFiltering(int slot, boolean container, EnumFacing facing) {
        return false;
    }

    protected boolean shouldReplaceFilter(int slot, ItemStack newFilter, boolean container, EnumFacing facing) {
        return false;
    }
}
