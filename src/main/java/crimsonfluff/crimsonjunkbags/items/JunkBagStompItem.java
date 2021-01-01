package crimsonfluff.crimsonjunkbags.items;

import crimsonfluff.crimsonjunkbags.CrimsonJunkBags;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class JunkBagStompItem extends Item {
    public JunkBagStompItem() { super(new Properties().group(ItemGroup.MISC).maxStackSize(1)); }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("tip." + CrimsonJunkBags.MOD_ID + ".junk_bag_stomp.item").mergeStyle(TextFormatting.GREEN));

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (worldIn.isRemote) return new ActionResult<>(ActionResultType.SUCCESS, stack);

        DamageSource dm = new DamageSource("boom")
            .setDamageBypassesArmor();

        if (!playerIn.isCreative()) stack.shrink(1);

        playerIn.world.playSound(null, playerIn.getPosition(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1f, 1f);
        playerIn.attackEntityFrom(dm, Float.MAX_VALUE);

        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }
}