package crimsonfluff.crimsonjunkbags.items;

import crimsonfluff.crimsonjunkbags.CrimsonJunkBags;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class LootBagBatsItem extends Item {
    public LootBagBatsItem() {
        super(new Properties().group(ItemGroup.MISC).maxStackSize(1));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("tip." + CrimsonJunkBags.MOD_ID + ".bats_bag.item").mergeStyle(TextFormatting.GREEN));

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (worldIn.isRemote) return new ActionResult<>(ActionResultType.SUCCESS, stack);

        if (!playerIn.isCreative()) stack.shrink(1);

        playerIn.world.playSound(null, playerIn.getPosition(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1f, 1f);

        BlockPos spawnPos = new BlockPos(playerIn.getPosX(), playerIn.getPosY() + 1 , playerIn.getPosZ()).offset(playerIn.getHorizontalFacing());
        //worldIn.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0.0D, 0.0D, 0.0D);

        for (int a=0; a<10; a++) {
            BatEntity ent = (BatEntity) EntityType.BAT.spawn((ServerWorld) worldIn, null, null, spawnPos, SpawnReason.EVENT, false, false);
        }

        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }
}