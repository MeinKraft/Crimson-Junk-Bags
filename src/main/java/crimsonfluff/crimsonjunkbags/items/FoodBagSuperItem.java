package crimsonfluff.crimsonjunkbags.items;

import crimsonfluff.crimsonjunkbags.CrimsonJunkBags;
import crimsonfluff.crimsonjunkbags.util.KeyboardHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Random;

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
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (worldIn.isRemote) return new ActionResult<>(ActionResultType.SUCCESS, stack);

        if (CrimsonJunkBags.FoodBagSuperItemLoot.isEmpty()) {
            playerIn.world.playSound(null, playerIn.getPosition(), SoundEvents.ENTITY_VILLAGER_NO, SoundCategory.PLAYERS, 1f, 1f);
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }

        if (CrimsonJunkBags.CONFIGURATION.Loot_Playsound.get())
            playerIn.world.playSound(null, playerIn.getPosition(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1f, 1f);

        int stackCount = (KeyboardHelper.isHoldingShift()) ? stack.getCount() : 1;
        if (!playerIn.isCreative()) stack.shrink(stackCount);

        Random rand = new Random();
        for (int a = 0; a<stackCount; a++) {
            ItemStack item = CrimsonJunkBags.FoodBagSuperItemLoot.get(rand.nextInt(CrimsonJunkBags.FoodBagSuperItemLoot.size()));
            playerIn.dropItem(item.copy(), true);
        }

        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }
}
