package com.example.sophisticatedcooking.common;

import com.example.sophisticatedcooking.common.Config;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Config {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> SEARCH_RANGE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_BACKPACKS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SHOW_COMPAT_MESSAGE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_DEBUG_LOGGING;
    public static final ForgeConfigSpec.ConfigValue<Integer> MAX_CONTAINERS_PER_KITCHEN;
    public static final ForgeConfigSpec.ConfigValue<Boolean> REQUIRE_POWER_FOR_ACCESS;

    static {
        BUILDER.push("Sophisticated Cooking Bridge Configuration");

        SEARCH_RANGE = BUILDER
                .comment("Range (in blocks) for searching Sophisticated Storage containers from kitchen workstations",
                        "Default: 16, Min: 1, Max: 64")
                .defineInRange("searchRange", 16, 1, 64);

        ENABLE_BACKPACKS = BUILDER
                .comment("Enable access to Sophisticated Backpacks (if installed)",
                        "Default: true")
                .define("enableBackpacks", true);

        SHOW_COMPAT_MESSAGE = BUILDER
                .comment("Show compatibility message on game load",
                        "Default: true")
                .define("showCompatMessage", true);

        ENABLE_DEBUG_LOGGING = BUILDER
                .comment("Enable debug logging for troubleshooting",
                        "Default: true")
                .define("enableDebugLogging", true);

        MAX_CONTAINERS_PER_KITCHEN = BUILDER
                .comment("Maximum number of Sophisticated Storage containers a kitchen can access",
                        "Default: 32, Min: 1, Max: 256")
                .defineInRange("maxContainersPerKitchen", 32, 1, 256);

        REQUIRE_POWER_FOR_ACCESS = BUILDER
                .comment("Require Sophisticated Storage containers to have power for kitchen access",
                        "Default: false")
                .define("requirePowerForAccess", false);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static int getSearchRange() {
        return SEARCH_RANGE.get();
    }

    public static boolean isBackpacksEnabled() {
        return ENABLE_BACKPACKS.get();
    }

    public static boolean showCompatMessage() {
        return SHOW_COMPAT_MESSAGE.get();
    }

    public static boolean isDebugLoggingEnabled() {
        return ENABLE_DEBUG_LOGGING.get();
    }

    public static int getMaxContainersPerKitchen() {
        return MAX_CONTAINERS_PER_KITCHEN.get();
    }

    public static boolean requirePowerForAccess() {
        return REQUIRE_POWER_FOR_ACCESS.get();
    }
}