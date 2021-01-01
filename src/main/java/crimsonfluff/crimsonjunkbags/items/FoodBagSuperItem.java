package crimsonfluff.crimsonjunkbags.items;

import crimsonfluff.crimsonjunkbags.CrimsonJunkBags;
import crimsonfluff.crimsonjunkbags.util.CommonCode;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class FoodBagSuperItem extends Item {
    public FoodBagSuperItem() {
        super(new Properties().group(ItemGroup.MISC));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (CrimsonJunkBags.FoodBagSuperItemLoot == null)
            tooltip.add(new TranslationTextComponent("tip." + CrimsonJunkBags.MOD_ID + ".no_loot").mergeStyle(TextFormatting.RED));
        else {
            IFormattableTextComponent TTC;

            TTC = new TranslationTextComponent("tip." + CrimsonJunkBags.MOD_ID + ".food_bag_super");

            tooltip.add(TTC);
        }

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        return new ActionResult<>(CrimsonJunkBags.COMMON.onItemRightClick(worldIn, playerIn, handIn, CrimsonJunkBags.FoodBagSuperItemLoot), playerIn.getHeldItem(handIn));
    }
}
