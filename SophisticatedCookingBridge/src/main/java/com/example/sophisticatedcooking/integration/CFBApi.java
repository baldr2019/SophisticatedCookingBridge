package com.example.sophisticatedcooking.integration;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;

public class CFBApi {
    private static final Logger LOGGER = LogManager.getLogger();
    private static Object kitchenMultiBlockInstance = null;
    private static boolean apiAvailable = false;

    static {
        if (ModList.get().isLoaded("cookingforblockheads")) {
            try {
                // 尝试加载Cooking for Blockheads API类
                Class<?> kitchenMultiBlockClass = Class.forName(
                        "net.blay09.mods.cookingforblockheads.api.KitchenMultiBlock");

                // 尝试获取实例
                try {
                    // 旧版本：INSTANCE字段
                    java.lang.reflect.Field instanceField = kitchenMultiBlockClass.getDeclaredField("INSTANCE");
                    kitchenMultiBlockInstance = instanceField.get(null);
                } catch (NoSuchFieldException e) {
                    // 新版本：get()方法
                    Method getMethod = kitchenMultiBlockClass.getMethod("get");
                    kitchenMultiBlockInstance = getMethod.invoke(null);
                }

                apiAvailable = (kitchenMultiBlockInstance != null);
                LOGGER.debug("Cooking for Blockheads API loaded successfully via reflection");
            } catch (Exception e) {
                LOGGER.error("Failed to load Cooking for Blockheads API via reflection", e);
                apiAvailable = false;
            }
        }
    }

    public static boolean isApiAvailable() {
        return apiAvailable;
    }

    /**
     * 检查位置是否为有效的厨房（通过反射）
     */
    public static boolean isValidKitchen(Level level, BlockPos pos) {
        if (!apiAvailable || level == null || kitchenMultiBlockInstance == null) {
            return false;
        }

        try {
            Class<?> kitchenMultiBlockClass = kitchenMultiBlockInstance.getClass();
            Method isPartOfKitchenMethod = kitchenMultiBlockClass.getMethod(
                    "isPartOfKitchen", Level.class, BlockPos.class);

            Object result = isPartOfKitchenMethod.invoke(kitchenMultiBlockInstance, level, pos);
            return result instanceof Boolean ? (Boolean) result : false;
        } catch (Exception e) {
            LOGGER.error("Error checking kitchen validity via reflection", e);
            return false;
        }
    }
}