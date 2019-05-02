package shcm.shsupercm.forge.simplycrafting.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.item.ItemStack;
import shcm.shsupercm.forge.simplycrafting.common.CommonProxy;

@JEIPlugin
public class SimplyCraftingJEIPlugin implements IModPlugin {
    @Override
    public void register(IModRegistry registry) {
        registry.addRecipeCatalyst(new ItemStack(CommonProxy.simplyCrafter), VanillaRecipeCategoryUid.CRAFTING);
    }
}
