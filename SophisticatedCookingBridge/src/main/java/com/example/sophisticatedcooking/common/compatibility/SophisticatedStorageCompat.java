package com.example.sophisticatedcooking.common.compatibility;

import com.example.sophisticatedcooking.SophisticatedCookingBridge;
import com.example.sophisticatedcooking.common.Config;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;

public class SophisticatedStorageCompat {
    private static final Logger LOGGER = LogManager.getLogger();
    private static boolean isInitialized = false;

    public static boolean isInitialized() {
        return isInitialized;
    }

    public static void init() {
        // 检查所需模组是否加载
        if (!ModList.get().isLoaded("cookingforblockheads")) {
            LOGGER.warn("Cooking for Blockheads not found. Bridge will not function.");
            return;
        }

        if (!ModList.get().isLoaded("sophisticatedstorage")) {
            LOGGER.warn("Sophisticated Storage not found. Bridge will not function.");
            return;
        }

        if (Config.showCompatMessage()) {
            LOGGER.info("Initializing bridge between Cooking for Blockheads and Sophisticated Storage");
        }

        try {
            // 使用反射获取并调用Cooking for Blockheads的API
            registerWithCookingForBlockheads();
            isInitialized = true;
            LOGGER.info("✅ 兼容性桥接初始化成功！");
        } catch (Exception e) {
            LOGGER.error("❌ 桥接初始化失败，详细信息：", e);
            LOGGER.error("提示：请检查 Cooking for Blockheads (v12.2.0) 的 API 是否与桥接模组兼容。");
        }
    }

    private static void registerWithCookingForBlockheads() throws Exception {
        LOGGER.info("正在尝试连接 Cooking for Blockheads API...");

        // 1. 加载正确的类（已从GitHub源码确认）
        String correctClassPath = "net.blay09.mods.cookingforblockheads.KitchenMultiBlock";
        Class<?> kitchenMultiBlockClass;

        try {
            kitchenMultiBlockClass = Class.forName(correctClassPath);
            LOGGER.info("✅ 成功找到 API 类: {}", correctClassPath);
        } catch (ClassNotFoundException e) {
            LOGGER.error("❌ 致命错误：无法在类路径下找到 API 类: {}", correctClassPath);
            throw new ClassNotFoundException("无法找到 Cooking for Blockheads 的核心 API 类: " + correctClassPath, e);
        }

        // 2. 动态探索并获取实例
        Object kitchenMultiBlockInstance = null;
        String instanceSource = "";

        LOGGER.info("正在分析 API 接口的所有公共方法...");
        Method[] allMethods = kitchenMultiBlockClass.getMethods();
        for (Method method : allMethods) {
            LOGGER.info("  - 方法名: {} | 参数个数: {} | 返回类型: {}",
                    method.getName(),
                    method.getParameterCount(),
                    method.getReturnType().getSimpleName());
        }

        // 常见获取实例的静态方法名列表（按可能性排序）
        String[] possibleStaticGetterNames = {"get", "getInstance", "instance", "getKitchenMultiBlock"};

        for (String methodName : possibleStaticGetterNames) {
            try {
                // 寻找无参数且返回类型匹配的静态方法
                Method getterMethod = kitchenMultiBlockClass.getMethod(methodName);
                if (java.lang.reflect.Modifier.isStatic(getterMethod.getModifiers())) {
                    kitchenMultiBlockInstance = getterMethod.invoke(null);
                    instanceSource = "静态方法 " + methodName + "()";
                    LOGGER.info("✅ 通过 {} 成功获取 API 实例。", instanceSource);
                    break;
                }
            } catch (NoSuchMethodException e) {
                // 继续尝试下一个方法名
                continue;
            }
        }

        // 如果通过静态方法没找到，尝试静态字段
        if (kitchenMultiBlockInstance == null) {
            try {
                java.lang.reflect.Field instanceField = kitchenMultiBlockClass.getDeclaredField("INSTANCE");
                if (java.lang.reflect.Modifier.isStatic(instanceField.getModifiers())) {
                    kitchenMultiBlockInstance = instanceField.get(null);
                    instanceSource = "静态字段 INSTANCE";
                    LOGGER.info("✅ 通过 {} 成功获取 API 实例。", instanceSource);
                }
            } catch (NoSuchFieldException e) {
                // 字段也不存在，继续下面的处理
            }
        }

        // 如果以上方式都失败了，抛出详细错误
        if (kitchenMultiBlockInstance == null) {
            LOGGER.error("❌ 无法通过常规方式获取 API 实例。");
            LOGGER.error("请根据上方列出的方法列表，确认获取实例的正确方式。");
            LOGGER.error("可能的方式包括：");
            LOGGER.error("  1. 调用某个无参数的静态方法。");
            LOGGER.error("  2. 访问某个静态字段。");
            LOGGER.error("  3. 通过其他API类（如 CookingForBlockheadsAPI）间接获取。");
            throw new RuntimeException("无法确定获取 KitchenMultiBlock 实例的方式。请检查上方日志列出的方法。");
        }

        // 3. 查找并调用 addItemHandler 方法
        Method addItemHandlerMethod = null;
        try {
            // 方法名和参数类型根据通用API设计
            addItemHandlerMethod = kitchenMultiBlockClass.getMethod("addItemHandler", Function.class);
            LOGGER.info("✅ 成功找到 addItemHandler 方法。");
        } catch (NoSuchMethodException e) {
            LOGGER.error("❌ 在 API 类中找不到关键的 addItemHandler(Function) 方法。");
            LOGGER.error("可用的公共方法有：");
            for (Method m : kitchenMultiBlockClass.getMethods()) {
                LOGGER.error("  - {}({})", m.getName(), Arrays.toString(m.getParameterTypes()));
            }
            throw new RuntimeException("不兼容的 API: 缺少 addItemHandler 方法。");
        }

        // 4. 创建并注册我们的物品处理器提供函数
        Function<BlockEntity, Object> itemHandlerProvider = createItemHandlerProvider();
        addItemHandlerMethod.invoke(kitchenMultiBlockInstance, itemHandlerProvider);

        LOGGER.info("✅ 成功向 Cooking for Blockheads 注册物品处理器提供器。桥接逻辑已就绪。");
    }

    private static Function<BlockEntity, Object> createItemHandlerProvider() {
        return blockEntity -> {
            try {
                if (blockEntity == null) {
                    return null;
                }

                String className = blockEntity.getClass().getName();
                boolean isSophisticatedStorage = className.contains("sophisticatedstorage");
                boolean isSophisticatedBackpacks = className.contains("sophisticatedbackpacks");

                // 检查是否为目标模组的方块实体
                if (!isSophisticatedStorage && !(isSophisticatedBackpacks && Config.isBackpacksEnabled())) {
                    return null;
                }

                // 检查是否需要电源
                if (Config.requirePowerForAccess()) {
                    try {
                        // 使用反射检查能量
                        Object energyCap = blockEntity.getCapability(
                                net.minecraftforge.energy.CapabilityEnergy.ENERGY
                        ).orElse(null);

                        if (energyCap != null) {
                            // 反射调用getEnergyStored方法
                            Method getEnergyStoredMethod = energyCap.getClass().getMethod("getEnergyStored");
                            int energy = (int) getEnergyStoredMethod.invoke(energyCap);
                            if (energy <= 0) {
                                return null; // 没有能量，不提供访问
                            }
                        }
                    } catch (Exception e) {
                        // 忽略能量检查错误
                    }
                }

                // 返回物品处理器
                return blockEntity.getCapability(
                        net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                ).orElse(null);

            } catch (Exception e) {
                if (Config.isDebugLoggingEnabled()) {
                    SophisticatedCookingBridge.LOGGER.error("Error in item handler provider", e);
                }
                return null;
            }
        };
    }
}