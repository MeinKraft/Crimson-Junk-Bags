package crimsonfluff.crimsonjunkbags;

import crimsonfluff.crimsonjunkbags.init.itemsInit;
import crimsonfluff.crimsonjunkbags.util.ConfigBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Mod("crimsonjunkbags")
public class CrimsonJunkBags {
    public static final String MOD_ID = "crimsonjunkbags";
    public static final Logger LOGGER = LogManager.getLogger("crimsonjunkbags");
    final IEventBus MOD_EVENTBUS = FMLJavaModLoadingContext.get().getModEventBus();
    public static final ConfigBuilder CONFIGURATION = new ConfigBuilder();

// Food
    public static ArrayList<Item> FoodBagItemLoot = new ArrayList<Item>();
    public static ArrayList<Item> FoodBagSuperItemLoot = new ArrayList<Item>();
// Loot
    public static ArrayList<Item> LootBagCommonItemLoot = new ArrayList<Item>();
    public static ArrayList<Item> LootBagUnCommonItemLoot = new ArrayList<Item>();
    public static ArrayList<Item> LootBagRareItemLoot = new ArrayList<Item>();
    public static ArrayList<Item> LootBagEpicItemLoot = new ArrayList<Item>();
    public static ArrayList<Item> LootBagLegendaryItemLoot = new ArrayList<Item>();


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


    private void initLootBagItems()  {
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
                        FoodBagSuperItemLoot.add(item);
                        isSuper = false;

                    } else
                        FoodBagItemLoot.add(item);
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
    }

    private void LootLoader(List list, String path) {
        List<String> Loot;
        Item item;

        try {
            Loot = Files.readAllLines(FMLPaths.CONFIGDIR.get().resolve("junk_bags/" + path), StandardCharsets.UTF_8);

            for (String itemString : Loot) {
                item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemString));

                // if AIR then item not found
                if (item == Items.AIR) {
                    LOGGER.info("Item404: " + itemString + " @ " + path);

                } else {
                    list.add(item);
                }
            }
        }
        catch (IOException e) {
            //e.printStackTrace();
        }
    }
}