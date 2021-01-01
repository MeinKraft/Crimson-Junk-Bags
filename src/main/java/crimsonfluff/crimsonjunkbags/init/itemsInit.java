package crimsonfluff.crimsonjunkbags.init;

import crimsonfluff.crimsonjunkbags.CrimsonJunkBags;
import crimsonfluff.crimsonjunkbags.items.*;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class itemsInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CrimsonJunkBags.MOD_ID);

    public static final RegistryObject<Item> FOOD_BAG = ITEMS.register("food_bag", FoodBagItem::new);
    public static final RegistryObject<Item> FOOD_SUPERBAG = ITEMS.register("food_bag_super", FoodBagSuperItem::new);

    public static final RegistryObject<Item> JUNK_BAG_COMMON = ITEMS.register("junk_bag_common", JunkBagCommonItem::new);
    public static final RegistryObject<Item> JUNK_BAG_UNCOMMON = ITEMS.register("junk_bag_uncommon", JunkBagUnCommonItem::new);
    public static final RegistryObject<Item> JUNK_BAG_RARE = ITEMS.register("junk_bag_rare", JunkBagRareItem::new);
    public static final RegistryObject<Item> JUNK_BAG_EPIC = ITEMS.register("junk_bag_epic", JunkBagEpicItem::new);
    public static final RegistryObject<Item> JUNK_BAG_LEGENDARY = ITEMS.register("junk_bag_legendary", JunkBagLegendaryItem::new);

// Custom
    public static final RegistryObject<Item> JUNK_BAG_STOMP = ITEMS.register("junk_bag_stomp", JunkBagStompItem::new);
    public static final RegistryObject<Item> JUNK_BAG_BATS = ITEMS.register("bats_bag", JunkBagBatsItem::new);
}
