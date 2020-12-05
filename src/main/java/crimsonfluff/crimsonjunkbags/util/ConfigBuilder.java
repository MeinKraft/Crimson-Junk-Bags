package crimsonfluff.crimsonjunkbags.util;
import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigBuilder {
    public final ForgeConfigSpec CLIENT;

    public ForgeConfigSpec.BooleanValue Loot_Playsound;
    public ForgeConfigSpec.BooleanValue Food_Auto;
    public ForgeConfigSpec.IntValue Food_Hunger;
    public ForgeConfigSpec.IntValue Food_Saturation;


    public ConfigBuilder() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("General Info");
//            builder.comment("/give @s crimsonjunkbags:junk_bag_common 1");
//            builder.comment("/give @s crimsonjunkbags:junk_bag_epic{Rolls:1} 1");

        Loot_Playsound = builder
            .comment("Play a sound when opened ?")
            .define("JunkBag_Playsound", true);

        builder.pop();


        builder.push("Food Junk");
        Food_Auto = builder
            .comment("Auto Generate List ?")
            .comment("If true food items are sorted into Food_Bag and Food_Bag_Super Junk Bags")
            .define("Food_Auto", true);

        Food_Hunger = builder
            .comment("Hunger level to be considered a Super Food. Default 10")
            .defineInRange("Food_Hunger", 10,10,20);

        Food_Saturation = builder
            .comment("Saturation level to be considered a Super Food. Default 10")
            .defineInRange("Food_Saturation", 10,10,20);

        builder.pop();


        CLIENT = builder.build();
    }
}
