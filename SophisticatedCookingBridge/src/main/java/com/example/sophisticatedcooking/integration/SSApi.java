package com.example.sophisticatedcooking.integration;

import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;

public class SSApi {
    private static final Logger LOGGER = LogManager.getLogger();
    private static Class<?> storageBlockEntityBaseClass = null;
    private static boolean apiAvailable = false;

    static {
        if (ModList.get().isLoaded("sophisticatedstorage")) {
            try {
                // 尝试加载Sophisticated Storage核心类
                storageBlockEntityBaseClass = Class.forName(
                        "net.p3pp3rf1y.sophisticatedstorage.block.StorageBlockEntityBase");
                apiAvailable = true;
                LOGGER.debug("Sophisticated Storage API classes found via reflection");
            } catch (ClassNotFoundException e) {
                LOGGER.debug("Sophisticated Storage API classes not found via reflection");
                apiAvailable = false;
            } catch (Exception e) {
                LOGGER.error("Error loading Sophisticated Storage API via reflection", e);
                apiAvailable = false;
            }
        }
    }

    public static boolean isApiAvailable() {
        return apiAvailable;
    }

    public static boolean isStorageBlockEntity(Object blockEntity) {
        if (!apiAvailable || storageBlockEntityBaseClass == null) {
            return false;
        }

        try {
            return storageBlockEntityBaseClass.isInstance(blockEntity);
        } catch (Exception e) {
            return false;
        }
    }
}