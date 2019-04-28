package shcm.shsupercm.forge.simplycrafting.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerSimplyCrafter extends Container {
    private TESimplyCrafter tileEntity;
    private InventoryPlayer inventoryPlayer;

    public ContainerSimplyCrafter(TESimplyCrafter tileEntity, InventoryPlayer inventoryPlayer) {
        this.tileEntity = tileEntity;
        this.inventoryPlayer = inventoryPlayer;

        for (int x = 0; x < 9; ++x)
            this.addSlotToContainer(new Slot(inventoryPlayer, x, 8 + x * 18, 142));

        for (int y = 0; y < 3; ++y)
            for (int x = 0; x < 9; ++x)
                this.addSlotToContainer(new Slot(inventoryPlayer, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));

        for (int y = 0; y < 3; ++y)
            for (int x = 0; x < 3; ++x)
                this.addSlotToContainer(new SlotItemHandler(tileEntity.craftingItemHandler, x + y * 3, 30 + x * 18, 17 + y * 18));

        this.addSlotToContainer(new SlotItemHandler(tileEntity.craftingItemHandler, 9, 124, 35) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }
        });
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {


        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if(slotId == 45)
            return ItemStack.EMPTY;
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }
}
