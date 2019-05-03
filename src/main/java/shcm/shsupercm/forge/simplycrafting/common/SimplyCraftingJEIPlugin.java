package shcm.shsupercm.forge.simplycrafting.common;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.startup.StackHelper;
import mezz.jei.transfer.BasicRecipeTransferHandler;
import mezz.jei.transfer.BasicRecipeTransferInfo;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

import javax.annotation.Nullable;

@JEIPlugin
public class SimplyCraftingJEIPlugin implements IModPlugin {
    @Override
    public void register(IModRegistry registry) {
        registry.addIngredientInfo(new ItemStack(CommonProxy.itemSimplyCrafter), VanillaTypes.ITEM,
                I18n.format("tile.simplycrafting:simply_crafter.jei.1"),
                I18n.format("tile.simplycrafting:simply_crafter.jei.2"),
                I18n.format("tile.simplycrafting:simply_crafter.jei.3"),
                I18n.format("tile.simplycrafting:simply_crafter.jei.4"),
                I18n.format("tile.simplycrafting:simply_crafter.jei.5"));

        registry.addRecipeCatalyst(new ItemStack(CommonProxy.simplyCrafter), VanillaRecipeCategoryUid.CRAFTING);

        registry.getRecipeTransferRegistry().addRecipeTransferHandler(new RecipeTransferHandler(registry.getJeiHelpers()), VanillaRecipeCategoryUid.CRAFTING);
    }

    private static class RecipeTransferHandler extends BasicRecipeTransferHandler<ContainerSimplyCrafter> {
        RecipeTransferHandler(IJeiHelpers helpers) {
            super(
                    (StackHelper) helpers.getStackHelper(),
                    helpers.recipeTransferHandlerHelper(),
                    new BasicRecipeTransferInfo<>(ContainerSimplyCrafter.class, VanillaRecipeCategoryUid.CRAFTING, 36, 9, 0, 36));
        }

        @Nullable
        @Override
        public IRecipeTransferError transferRecipe(ContainerSimplyCrafter container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
            IRecipeTransferError transferError = super.transferRecipe(container, recipeLayout, player, maxTransfer, doTransfer);
            if(doTransfer && player.world.isRemote) {
                //player.closeScreen();
                ((EntityPlayerSP)player).connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(container.tileEntity.getPos(), EnumFacing.UP, EnumHand.MAIN_HAND, 0f, 0f, 0f));
            }
            return transferError;
        }
    }
}