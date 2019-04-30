package shcm.shsupercm.forge.core.api1.smart;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

import java.util.HashMap;

public class Container extends net.minecraft.inventory.Container {
    private HashMap<Slot, SlotModifier> slotModifiers = new HashMap<>();


    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if (slotId >= 36) {
            Slot slot = getSlot(slotId);
            if (slot instanceof SlotItemHandler && ((SlotItemHandler) slot).getItemHandler() instanceof ItemStackInventory.Handler) {
                if(!slot.getHasStack())
                    ((ItemStackInventory.Handler) ((SlotItemHandler) slot).getItemHandler()).itemStackInventory.clickEmptySlot(slotId - 36);
            }
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    protected Slot addSlotToContainer(Slot slotIn) {
        new SlotModifier(slotIn);
        return super.addSlotToContainer(slotIn);
    }

    protected SlotModifier addSlot(Slot slotIn) {
        SlotModifier modifier = new SlotModifier(slotIn);
        super.addSlotToContainer(slotIn);
        return modifier;
    }

    public ItemStack transferStackInSlot(EntityPlayer player, int sourceSlotIndex) {
        Slot sourceSlot = inventorySlots.get(sourceSlotIndex);
        if (sourceSlot == null || !sourceSlot.getHasStack())
            return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getStack();
        ItemStack copyOfSourceStack = sourceStack.copy();

        SlotModifier modifier = slotModifiers.get(sourceSlot);
        if(modifier.shiftDestMin == -1 || modifier.shiftDestMax == -1)
            return ItemStack.EMPTY;

        if (!mergeItemStack(sourceStack, modifier.shiftDestMin, modifier.shiftDestMax, false))
            return ItemStack.EMPTY;

        if (sourceStack.getCount() == 0)
            sourceSlot.putStack(ItemStack.EMPTY);
        else
            sourceSlot.onSlotChanged();

        sourceSlot.onTake(player, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    protected class SlotModifier {
        private Slot slot;

        private int shiftDestMin = -1, shiftDestMax = -1;

        private SlotModifier(Slot slot) {
            this.slot = slot;
            slotModifiers.put(slot, this);
        }

        public SlotModifier setShiftDest(int min, int max) {
            assert max >= min;
            this.shiftDestMin = min;
            this.shiftDestMax = max + 1;
            return this;
        }
    }
}
