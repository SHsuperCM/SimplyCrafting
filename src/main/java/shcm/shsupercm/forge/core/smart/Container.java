package shcm.shsupercm.forge.core.smart;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class Container extends net.minecraft.inventory.Container {
    public ItemStack transferStackInSlot(EntityPlayer player, int sourceSlotIndex) {
        Slot sourceSlot = inventorySlots.get(sourceSlotIndex);
        if (sourceSlot == null || !sourceSlot.getHasStack())
            return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getStack();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (sourceSlotIndex < 36) {
            if (!mergeItemStack(sourceStack, 36, inventorySlots.size(), false))
                return ItemStack.EMPTY;
        } else if (sourceSlotIndex < inventorySlots.size()) {
            if (!mergeItemStack(sourceStack, 0, 36, false))
                return ItemStack.EMPTY;
        } else {
            return ItemStack.EMPTY;
        }

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
}
