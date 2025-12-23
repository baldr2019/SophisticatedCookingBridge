package com.example.sophisticatedcooking.common.compatibility;

import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CookingForBlockheadsCompat {
    private static final Logger LOGGER = LogManager.getLogger();

    public static boolean isAvailable() {
        return ModList.get().isLoaded("cookingforblockheads");
    }

    public static void logAvailability() {
        if (isAvailable()) {
            LOGGER.debug("Cooking for Blockheads is available");
        } else {
            LOGGER.warn("Cooking for Blockheads is not available");
        }
    }
}