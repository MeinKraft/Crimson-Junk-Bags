package crimsonfluff.crimsonjunkbags;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import crimsonfluff.crimsonjunkbags.init.itemsInit;
import crimsonfluff.crimsonjunkbags.util.ConfigBuilder;
import crimsonfluff.crimsonjunkbags.util.JunkBagsCommands;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;

@Mod("crimsonjunkbags")
public class CrimsonJunkBags {
    public static final String MOD_ID = "crimsonjunkbags";
    public static final Logger LOGGER = LogManager.getLogger("CrimsonJunkBags");
    final IEventBus MOD_EVENTBUS = FMLJavaModLoadingContext.get().getModEventBus();
    public static final ConfigBuilder CONFIGURATION = new ConfigBuilder();

// Food
    public static ArrayList<ItemStack> FoodBagItemLoot = new ArrayList<>();
    public static ArrayList<ItemStack> FoodBagSuperItemLoot = new ArrayList<>();
// Loot
    public static ArrayList<ItemStack> LootBagCommonItemLoot = new ArrayList<>();
    public static ArrayList<ItemStack> LootBagUnCommonItemLoot = new ArrayList<>();
    public static ArrayList<ItemStack> LootBagRareItemLoot = new ArrayList<>();
    public static ArrayList<ItemStack> LootBagEpicItemLoot = new ArrayList<>();
    public static ArrayList<ItemStack> LootBagLegendaryItemLoot = new ArrayList<>();
// Unknown
    public static ArrayList<String> UnknownItemLoot = new ArrayList<>();


    @SubscribeEvent
    public void onCommandsRegister(RegisterCommandsEvent event) { new JunkBagsCommands(event.getDispatcher()); }


    public CrimsonJunkBags() {
        MOD_EVENTBUS.addListener(this::setup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CONFIGURATION.CLIENT);
        itemsInit.ITEMS.register(MOD_EVENTBUS);

        MinecraftForge.EVENT_BUS.register(this);
    }


    // This Event is fired after all mods have loaded/done their init stages
    // we need this so we can pull Items from other Mods in ForgeRegistries.ITEMS
    private void setup(final FMLCommonSetupEvent event) {
        initLootBagItems();
    }


    private void initLootBagItems() {
        // TODO: If folder 'config/junkbags' not found then create example configs

        initLootConfigs();

        if (CONFIGURATION.Food_Auto.get()) {
            boolean isSuper = false;

            for (Item item : ForgeRegistries.ITEMS) {
                if (item.isFood()) {
                    if (item.getFood().getHealing() >= CONFIGURATION.Food_Hunger.get()) isSuper = true;
                    if (item.getFood().getSaturation() >= CONFIGURATION.Food_Saturation.get()) isSuper = true;
                    if (item == Items.ENCHANTED_GOLDEN_APPLE) isSuper = true;
                    if (item == Items.GOLDEN_APPLE) isSuper = true;
                    if (item == Items.GOLDEN_CARROT) isSuper = true;

                    if (isSuper) {
                        FoodBagSuperItemLoot.add(new ItemStack(item));
                        isSuper = false;

                    } else
                        FoodBagItemLoot.add(new ItemStack(item));
                }
            }
        }

        else {
            LootLoader(FoodBagItemLoot, "food_loot.txt");
            LootLoader(FoodBagSuperItemLoot, "food_super_loot.txt");
        }

        LootLoader(LootBagCommonItemLoot, "common_loot.txt");
        LootLoader(LootBagUnCommonItemLoot, "uncommon_loot.txt");
        LootLoader(LootBagRareItemLoot, "rare_loot.txt");
        LootLoader(LootBagEpicItemLoot, "epic_loot.txt");
        LootLoader(LootBagLegendaryItemLoot, "legendary_loot.txt");

        SaveUnknownItems();
    }

    private void LootLoader(List<ItemStack> list, String path) {
        List<String> LootFromFile;

        try {
            LootFromFile = Files.readAllLines(FMLPaths.CONFIGDIR.get().resolve("junk_bags/" + path), StandardCharsets.UTF_8);
            UnknownItemLoot.add("[" + path + "]");

            String itemName;
            String nbt = "";
            int find;
            ItemStack newItem;
            Item item;

            for (String itemString : LootFromFile) {
                itemString = itemString.trim();

                if (!itemString.equals("")) {
                    find = itemString.indexOf("{", 0);

                    if (find == -1)
                        itemName = itemString;      // '{' not found

                    else {
                        itemName = itemString.substring(0, find).trim();   // NOT find-1
                        nbt = itemString.substring(find).trim();
                    }

                    item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));

                    // if AIR then item not found
                    if (item == Items.AIR) {
                        LOGGER.info("Item404: " + itemString + " @ " + path);
                        UnknownItemLoot.add(itemString);

                    } else {
                        newItem = new ItemStack(item);

                        if (!nbt.equals("")) {
                            newItem.setTag(JsonToNBT.getTagFromJson(nbt));

                            if (newItem.getTag().contains("jbCount")) {
                                // add protection against negative values and values greater than max stack size

                                find = Integer.min(newItem.getTag().getInt("jbCount"), newItem.getMaxStackSize());
                                find = Integer.max(1, find);
                                newItem.setCount(find);
                            }

                            nbt = "";
                        }

                        list.add(newItem);
                    }
                }
            }

            UnknownItemLoot.add("");
        }
        catch (IOException | CommandSyntaxException e) {
            //e.printStackTrace();
        }
    }

    private void SaveUnknownItems() {
        Path path = FMLPaths.CONFIGDIR.get().resolve("junk_bags/unknown.txt");

        try {
            Files.deleteIfExists(path);
            Files.write(path, UnknownItemLoot, StandardCharsets.UTF_8);

        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    private void initLootConfigs() {
        Path path = FMLPaths.CONFIGDIR.get().resolve("junk_bags");

        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);

                // NOTE: Use separate StandardCharsets.UTF_8.newEncoder() for each writer
                // else causes file exception errors

                path = FMLPaths.CONFIGDIR.get().resolve("junk_bags/food_loot.txt");
                try (OutputStream out = Files.newOutputStream(path, StandardOpenOption.CREATE_NEW);
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out,  StandardCharsets.UTF_8.newEncoder()))) {
                    writer.append("minecraft:apple");
                    writer.newLine();

                } catch (IOException e) {
                    //e.printStackTrace();
                }

                path = FMLPaths.CONFIGDIR.get().resolve("junk_bags/food_super_loot.txt");
                try (OutputStream out2 = Files.newOutputStream(path, StandardOpenOption.CREATE_NEW);
                    BufferedWriter writer2 = new BufferedWriter(new OutputStreamWriter(out2,  StandardCharsets.UTF_8.newEncoder()))) {
                    writer2.append("minecraft:golden_apple{jbCount:2}");
                    writer2.newLine();

                } catch (IOException e) {
                    //e.printStackTrace();
                }

                path = FMLPaths.CONFIGDIR.get().resolve("junk_bags/common_loot.txt");
                try (OutputStream out3 = Files.newOutputStream(path, StandardOpenOption.CREATE_NEW);
                    BufferedWriter writer3 = new BufferedWriter(new OutputStreamWriter(out3,  StandardCharsets.UTF_8.newEncoder()))) {
                    writer3.append("minecraft:wooden_sword{display:{Name:\"\\\"Bashing Stick\\\"\"},Damage:0}");
                    writer3.newLine();

                } catch (IOException e) {
                    //e.printStackTrace();
                }

                path = FMLPaths.CONFIGDIR.get().resolve("junk_bags/uncommon_loot.txt");
                try (OutputStream out4 = Files.newOutputStream(path, StandardOpenOption.CREATE_NEW);
                    BufferedWriter writer4 = new BufferedWriter(new OutputStreamWriter(out4,  StandardCharsets.UTF_8.newEncoder()))) {
                    writer4.append("minecraft:stone_sword{display:{Name:\"\\\"Stoner Sticks\\\"\"},Damage:0}");
                    writer4.newLine();

                } catch (IOException e) {
                    //e.printStackTrace();
                }

                path = FMLPaths.CONFIGDIR.get().resolve("junk_bags/rare_loot.txt");
                try (OutputStream out5 = Files.newOutputStream(path, StandardOpenOption.CREATE_NEW);
                    BufferedWriter writer5 = new BufferedWriter(new OutputStreamWriter(out5,  StandardCharsets.UTF_8.newEncoder()))) {
                    writer5.append("minecraft:diamond_sword");
                    writer5.newLine();

                } catch (IOException e) {
                    //e.printStackTrace();
                }

                path = FMLPaths.CONFIGDIR.get().resolve("junk_bags/epic_loot.txt");
                try (OutputStream out6 = Files.newOutputStream(path, StandardOpenOption.CREATE_NEW);
                    BufferedWriter writer6 = new BufferedWriter(new OutputStreamWriter(out6,  StandardCharsets.UTF_8.newEncoder()))) {
                    writer6.append("minecraft:diamond{jbCount:2}");
                    writer6.newLine();

                } catch (IOException e) {
                    //e.printStackTrace();
                }

                path = FMLPaths.CONFIGDIR.get().resolve("junk_bags/legendary_loot.txt");
                try (OutputStream out7 = Files.newOutputStream(path, StandardOpenOption.CREATE_NEW);
                    BufferedWriter writer7 = new BufferedWriter(new OutputStreamWriter(out7,  StandardCharsets.UTF_8.newEncoder()))) {
                    writer7.append("minecraft:nether_star");
                    writer7.newLine();

                } catch (IOException e) {
                    //e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}