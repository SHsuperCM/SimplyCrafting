package shcm.shsupercm.forge.simplycrafting.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.items.SlotItemHandler;
import shcm.shsupercm.forge.core.smart.Container;

public class ContainerSimplyCrafter extends Container {//drag should throw slot click after
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
                this.addSlotToContainer(new SlotItemHandler(tileEntity.inventory.container(), x + y * 3, 30 + x * 18, 17 + y * 18));

        this.addSlotToContainer(new SlotItemHandler(tileEntity.inventory.container(), 9, 124, 35));
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        ://todo work only when closed

        super.onContainerClosed(playerIn);
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if(slotId == 45)
            return ItemStack.EMPTY;
        ItemStack result = super.slotClick(slotId, dragType, clickTypeIn, player);
        if(slotId >= 36 && slotId <= 44)
            tileEntity.refreshRecipe();
        return result;
    }
}
