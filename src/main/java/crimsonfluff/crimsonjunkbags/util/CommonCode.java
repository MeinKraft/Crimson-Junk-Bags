package crimsonfluff.crimsonjunkbags.util;

import crimsonfluff.crimsonjunkbags.CrimsonJunkBags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.Random;

public class CommonCode {
    public void addItemParticles(ItemStack stack, int count, World worldIn, PlayerEntity playerIn) {
        for (int i = 0; i < count; ++i) {
            Vector3d vector3d = new Vector3d(((double) worldIn.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
            vector3d = vector3d.rotatePitch(-playerIn.rotationPitch * ((float) Math.PI / 180F));
            vector3d = vector3d.rotateYaw(-playerIn.rotationYaw * ((float) Math.PI / 180F));

            double d0 = (double) (-worldIn.rand.nextFloat()) * 0.6D - 0.3D;
            Vector3d vector3d1 = new Vector3d(((double) worldIn.rand.nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
            vector3d1 = vector3d1.rotatePitch(-playerIn.rotationPitch * ((float) Math.PI / 180F));
            vector3d1 = vector3d1.rotateYaw(-playerIn.rotationYaw * ((float) Math.PI / 180F));
            vector3d1 = vector3d1.add(playerIn.getPosX(), playerIn.getPosYEye() + 0.25, playerIn.getPosZ());

            //if (worldIn instanceof ServerWorld) //Forge: Fix MC-2518 spawnParticle is nooped on server, need to use server specific variant
                ((ServerWorld) worldIn).spawnParticle(new ItemParticleData(ParticleTypes.ITEM, stack), vector3d1.x, vector3d1.y, vector3d1.z, 1, vector3d.x, vector3d.y + 0.05D, vector3d.z, 0.0D);
            //else
            //    worldIn.addParticle(new ItemParticleData(ParticleTypes.ITEM, stack), vector3d1.x, vector3d1.y, vector3d1.z, vector3d.x, vector3d.y + 0.05D, vector3d.z);
        }
    }

    public ActionResultType onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn, ArrayList<ItemStack> JunkBag) {
        if (worldIn.isRemote) return ActionResultType.SUCCESS;

        if (JunkBag.isEmpty()) {
            if (CrimsonJunkBags.CONFIGURATION.Loot_Playsound.get())
                playerIn.world.playSound(null, playerIn.getPosition(), SoundEvents.ENTITY_VILLAGER_NO, SoundCategory.PLAYERS, 1f, 1f);
            return ActionResultType.SUCCESS;
        }

        if (CrimsonJunkBags.CONFIGURATION.Loot_Playsound.get())
            playerIn.world.playSound(null, playerIn.getPosition(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1f, 1f);

        ItemStack stack = playerIn.getHeldItem(handIn);
        int stackCount = (playerIn.isCrouching()) ? stack.getCount() : 1;
        if (CrimsonJunkBags.CONFIGURATION.Loot_Particles.get()) addItemParticles(stack, 5 + (stackCount / 8), worldIn, playerIn);
        if (!playerIn.isCreative()) stack.shrink(stackCount);

        //Random rand = new Random();
        for (int a = 0; a < stackCount; a++) {
            ItemStack item = JunkBag.get(worldIn.rand.nextInt(JunkBag.size()));
            playerIn.dropItem(item.copy(), false);
        }

        return ActionResultType.SUCCESS;
    }
}
