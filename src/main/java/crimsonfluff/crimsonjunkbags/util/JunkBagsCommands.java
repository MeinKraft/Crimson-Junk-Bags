package crimsonfluff.crimsonjunkbags.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import crimsonfluff.crimsonjunkbags.CrimsonJunkBags;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class JunkBagsCommands {
    public JunkBagsCommands(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal("junkbags").requires(cs -> {
            return cs.getEntity() instanceof ServerPlayerEntity;
        })
            .then(Commands.literal("itemCount").executes(this::executeItemCount))
            .then(Commands.literal("addToCommon").executes(this::addToCommon))
            .then(Commands.literal("addToUnCommon").executes(this::addToUnCommon))
            .then(Commands.literal("addToRare").executes(this::addToRare))
            .then(Commands.literal("addToEpic").executes(this::addToEpic))
            .then(Commands.literal("addToLegendary").executes(this::addToLegendary))
    );
    }

    private int executeItemCount(CommandContext<CommandSource> cscc) throws CommandSyntaxException {
        PlayerEntity player = cscc.getSource().asPlayer();

        player.sendStatusMessage(new StringTextComponent("\n"), false);
        player.sendStatusMessage(new StringTextComponent("FoodBag - " + CrimsonJunkBags.FoodBagItemLoot.size()), false);
        player.sendStatusMessage(new StringTextComponent("SuperFoodBag - " + CrimsonJunkBags.FoodBagSuperItemLoot.size()), false);
        player.sendStatusMessage(new StringTextComponent("Common - " + CrimsonJunkBags.JunkBagCommonItemLoot.size()), false);
        player.sendStatusMessage(new StringTextComponent("UnCommon - " + CrimsonJunkBags.JunkBagUnCommonItemLoot.size()), false);
        player.sendStatusMessage(new StringTextComponent("Rare - " + CrimsonJunkBags.JunkBagRareItemLoot.size()), false);
        player.sendStatusMessage(new StringTextComponent("Epic - " + CrimsonJunkBags.JunkBagEpicItemLoot.size()), false);
        player.sendStatusMessage(new StringTextComponent("Legendary - " + CrimsonJunkBags.JunkBagLegendaryItemLoot.size()), false);
        player.sendStatusMessage(new StringTextComponent("Unknown - " + CrimsonJunkBags.UnknownItemLoot.size()), false);
        player.sendStatusMessage(new StringTextComponent("\n"), false);

        return 0;
    }

    private int addToCommon(CommandContext<CommandSource> cscc) throws CommandSyntaxException {
        addToBag(CrimsonJunkBags.JunkBagCommonItemLoot, cscc.getSource().asPlayer(), FMLPaths.CONFIGDIR.get().resolve("junk_bags/common_loot.txt"));

        return 0;
    }

    private int addToUnCommon(CommandContext<CommandSource> cscc) throws CommandSyntaxException {
        addToBag(CrimsonJunkBags.JunkBagUnCommonItemLoot, cscc.getSource().asPlayer(), FMLPaths.CONFIGDIR.get().resolve("junk_bags/uncommon_loot.txt"));

        return 0;
    }

    private int addToRare(CommandContext<CommandSource> cscc) throws CommandSyntaxException {
        addToBag(CrimsonJunkBags.JunkBagRareItemLoot, cscc.getSource().asPlayer(), FMLPaths.CONFIGDIR.get().resolve("junk_bags/rare_loot.txt"));

        return 0;
    }

    private int addToEpic(CommandContext<CommandSource> cscc) throws CommandSyntaxException {
        addToBag(CrimsonJunkBags.JunkBagEpicItemLoot, cscc.getSource().asPlayer(),FMLPaths.CONFIGDIR.get().resolve("junk_bags/epic_loot.txt"));

        return 0;
    }

    private int addToLegendary(CommandContext<CommandSource> cscc) throws CommandSyntaxException {
        addToBag(CrimsonJunkBags.JunkBagLegendaryItemLoot, cscc.getSource().asPlayer(), FMLPaths.CONFIGDIR.get().resolve("junk_bags/legendary_loot.txt"));

        return 0;
    }

    private void addToBag(List<ItemStack> list, PlayerEntity player, Path path) {
        int iCount = 0;
        ItemStack newItem;

/*
         TODO: Protect against duplicate entries
               Maybe only write the items added to the lootbag file, not re-write ALL of them ?
*/
        for (ItemStack item : player.inventory.mainInventory) {
            if (!item.isEmpty()) {
                newItem = item.copy();
                if (newItem.getCount() >= 2) newItem.getTag().putInt("jbCount", newItem.getCount());

                list.add(newItem);

                iCount++;

                //CrimsonJunkBags.LOGGER.info("NBT: " + item.getTag().toString());
            }
        }

        if (iCount != 0) {
            player.sendStatusMessage(new StringTextComponent("Added " + iCount + " items"), false);

            try {
                // TODO: rename to .old in case something goes wrong
                Files.deleteIfExists(path);

                CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder();
                try (OutputStream out = Files.newOutputStream(path, StandardOpenOption.CREATE_NEW);
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, encoder))) {

                    for (iCount=0; iCount<list.size(); iCount++) {
                        newItem = list.get(iCount);

                        writer.append(newItem.getItem().getRegistryName().toString());
                        if (newItem.hasTag()) writer.append(newItem.getTag().toString());
//
                        writer.newLine();
                    }

//                   out.flush();        // close file?
                }

            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }
}
