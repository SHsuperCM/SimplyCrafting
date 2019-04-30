package shcm.shsupercm.forge.simplycrafting.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.SlotItemHandler;
import shcm.shsupercm.forge.simplycrafting.common.ContainerSimplyCrafter;
import shcm.shsupercm.forge.simplycrafting.common.TESimplyCrafter;

public class GuiSimplyCrafter extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/crafting_table.png");

    private TESimplyCrafter tileEntity;

    public GuiSimplyCrafter(TESimplyCrafter tileEntity, InventoryPlayer inventoryPlayer) {
        super(new ContainerSimplyCrafter(tileEntity, inventoryPlayer));
        this.tileEntity = tileEntity;
        this.width = 176;
        this.height = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawDefaultBackground();

        mc.getTextureManager().bindTexture(TEXTURE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        RenderHelper.disableStandardItemLighting();
        RenderHelper.enableGUIStandardItemLighting();
        itemRender.zLevel = 100;
        for (int y = 0; y < 3; ++y)
            for (int x = 0; x < 3; ++x)
                if (tileEntity.inventory.getInternalItemStackHandler().getStackInSlot(x + y * 3).isEmpty())
                    drawItemStack(tileEntity.inventory.getFilterItemStackHandler().getStackInSlot(x + y * 3), guiLeft + 30 + x * 18, guiTop + 17 + y * 18, "0");
        if (tileEntity.recipe != null && tileEntity.inventory.getInternalItemStackHandler().getStackInSlot(9).isEmpty())
            drawItemStack(tileEntity.recipe.getRecipeOutput(), guiLeft + 124, guiTop + 35, "0");
        RenderHelper.disableStandardItemLighting();
        RenderHelper.enableStandardItemLighting();
        itemRender.zLevel = 0;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(I18n.format("container.crafting"), 28, 6, 4210752);
        this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);

        this.renderHoveredToolTip(mouseX-guiLeft,mouseY-guiTop);
    }

    private void drawItemStack(ItemStack stack, int x, int y, String altText)
    {
        GlStateManager.translate(0.0F, 0.0F, 32.0F);
        //GlStateManager.pushMatrix();
        this.zLevel = 200.0F;
        this.itemRender.zLevel = 200.0F;
        net.minecraft.client.gui.FontRenderer font = stack.getItem().getFontRenderer(stack);
        if (font == null) font = fontRenderer;
        this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        this.itemRender.renderItemOverlayIntoGUI(font, stack, x, y, altText);
        //this.zLevel = 0.0F;
        this.itemRender.zLevel = 0.0F;
        //GlStateManager.popMatrix();
    }
}
