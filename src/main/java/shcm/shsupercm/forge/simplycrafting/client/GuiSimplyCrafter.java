package shcm.shsupercm.forge.simplycrafting.client;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import shcm.shsupercm.forge.simplycrafting.common.ContainerSimplyCrafter;
import shcm.shsupercm.forge.simplycrafting.common.TESimplyCrafter;

public class GuiSimplyCrafter extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/crafting_table.png");

    public GuiSimplyCrafter(TESimplyCrafter tileEntity, InventoryPlayer inventoryPlayer) {
        super(new ContainerSimplyCrafter(tileEntity, inventoryPlayer));
        this.width = 176;
        this.height = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawDefaultBackground();

        mc.getTextureManager().bindTexture(TEXTURE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.renderHoveredToolTip(mouseX-guiLeft,mouseY-guiTop);
    }
}
