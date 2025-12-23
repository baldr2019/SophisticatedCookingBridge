package com.example.sophisticatedcooking.integration;

import com.example.sophisticatedcooking.SophisticatedCookingBridge;
import com.example.sophisticatedcooking.common.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class SophisticatedStorageInventoryProvider {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * 检查是否为精妙存储的方块实体
     * 目前基于注册ID检测，这是最可靠的方式。
     * 如果检测不准确，请使用下方的 debugAllStorageBlockEntities 方法找出真实类名后修改此处。
     */
    public static boolean isSophisticatedStorageBlockEntity(BlockEntity blockEntity) {
        if (blockEntity == null) return false;

        // 方案A：使用注册ID检测（推荐）
        try {
            // 关键修复：Forge 1.18.2 使用 BLOCK_ENTITIES，不是 BLOCK_ENTITY_TYPES
            ResourceLocation typeId = ForgeRegistries.BLOCK_ENTITIES.getKey(blockEntity.getType());
            // 如果注册ID的命名空间是 "sophisticatedstorage"，则认为是其容器
            if (typeId != null && "sophisticatedstorage".equals(typeId.getNamespace())) {
                if (Config.isDebugLoggingEnabled()) {
                    LOGGER.debug("[桥接] 通过注册ID识别到精妙存储容器: {}", typeId);
                }
                return true;
            }
        } catch (Exception e) {
            LOGGER.error("检查注册ID时出错", e);
        }

        // 方案B：类名检测（备用，根据你的实际类名修改）
        String className = blockEntity.getClass().getName();
        // 示例：如果类名以 net.p3pp3rf1y.sophisticatedstorage. 开头
        // 请根据 debugAllStorageBlockEntities 输出的真实类名修改下面的判断条件
        boolean isByClassName = className.startsWith("net.p3pp3rf1y.sophisticatedstorage.");
        if (isByClassName && Config.isDebugLoggingEnabled()) {
            LOGGER.debug("[桥接] 通过类名识别到可能的目标: {}", className);
        }

        // 先禁用类名检测，优先使用注册ID。如果注册ID无效，请启用下面这行：
        // return isByClassName;
        return false;
    }

    /**
     * 获取精妙存储容器的物品处理器
     */
    public static IItemHandler getItemHandler(BlockEntity blockEntity) {
        if (blockEntity == null || !isSophisticatedStorageBlockEntity(blockEntity)) {
            return null;
        }

        try {
            LazyOptional<IItemHandler> itemHandlerCap = blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            if (itemHandlerCap.isPresent()) {
                return itemHandlerCap.orElse(null);
            }
        } catch (Exception e) {
            if (Config.isDebugLoggingEnabled()) {
                LOGGER.error("从方块实体获取物品处理器时出错", e);
            }
        }
        return null;
    }

    /**
     * 获取范围内所有精妙存储容器的物品处理器
     */
    public static List<IItemHandler> getNearbyItemHandlers(Level level, BlockPos centerPos, int range) {
        List<IItemHandler> handlers = new ArrayList<>();

        if (level == null) return handlers;

        int maxContainers = Config.getMaxContainersPerKitchen();
        int containersFound = 0;

        // 搜索范围内的方块实体
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    if (containersFound >= maxContainers) break;

                    BlockPos pos = centerPos.offset(x, y, z);
                    BlockEntity blockEntity = level.getBlockEntity(pos);

                    if (blockEntity != null && isSophisticatedStorageBlockEntity(blockEntity)) {
                        IItemHandler handler = getItemHandler(blockEntity);
                        if (handler != null) {
                            handlers.add(handler);
                            containersFound++;

                            if (Config.isDebugLoggingEnabled()) {
                                LOGGER.debug("发现精妙存储容器，位置: {}，槽位: {}", pos, handler.getSlots());
                            }
                        }
                    }
                }
                if (containersFound >= maxContainers) break;
            }
            if (containersFound >= maxContainers) break;
        }

        if (Config.isDebugLoggingEnabled()) {
            LOGGER.debug("在 {} 格范围内找到 {} 个精妙存储容器", range, containersFound);
        }

        return handlers;
    }

    /**
     * 调试方法：扫描并打印范围内所有方块实体的关键信息
     * 调用方式：在游戏内找到一个合适的地方获取 Level 和坐标后调用
     * 例如：SophisticatedStorageInventoryProvider.debugAllStorageBlockEntities(world, playerPosition, 10);
     */
    public static void debugAllStorageBlockEntities(Level level, BlockPos centerPos, int range) {
        if (level == null) {
            LOGGER.warn("调试失败：Level 为 null");
            return;
        }

        LOGGER.info("=== 开始扫描范围内所有方块实体 ===");
        int totalCount = 0;
        int storageCount = 0;

        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos pos = centerPos.offset(x, y, z);
                    BlockEntity blockEntity = level.getBlockEntity(pos);

                    if (blockEntity != null) {
                        totalCount++;
                        String className = blockEntity.getClass().getName();
                        // 关键修复：Forge 1.18.2 使用 BLOCK_ENTITIES
                        ResourceLocation typeId = ForgeRegistries.BLOCK_ENTITIES.getKey(blockEntity.getType());

                        // 检查是否为精妙存储容器
                        boolean isStorage = false;
                        if (typeId != null && "sophisticatedstorage".equals(typeId.getNamespace())) {
                            isStorage = true;
                            storageCount++;
                        }

                        LOGGER.info("方块实体 #{}{}", totalCount, isStorage ? " [精妙存储]" : "");
                        LOGGER.info("  位置: {}", pos);
                        LOGGER.info("  完整类名: {}", className);
                        LOGGER.info("  注册ID: {}", typeId);

                        LazyOptional<IItemHandler> cap = blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
                        LOGGER.info("  是否有物品能力: {}", cap.isPresent());
                        LOGGER.info("  ---");
                    }
                }
            }
        }
        LOGGER.info("=== 扫描结束 ===");
        LOGGER.info("共发现 {} 个方块实体，其中 {} 个是精妙存储容器", totalCount, storageCount);
    }

    /**
     * 简单的调试方法：只检查一个特定的方块实体
     * 用于快速测试
     */
    public static void debugBlockEntity(BlockEntity blockEntity) {
        if (blockEntity == null) {
            LOGGER.info("调试失败：方块实体为 null");
            return;
        }

        String className = blockEntity.getClass().getName();
        // 关键修复：Forge 1.18.2 使用 BLOCK_ENTITIES
        ResourceLocation typeId = ForgeRegistries.BLOCK_ENTITIES.getKey(blockEntity.getType());

        LOGGER.info("=== 单个方块实体调试信息 ===");
        LOGGER.info("完整类名: {}", className);
        LOGGER.info("注册ID: {}", typeId);
        LOGGER.info("是否为精妙存储 (通过注册ID): {}",
                typeId != null && "sophisticatedstorage".equals(typeId.getNamespace()));

        LazyOptional<IItemHandler> cap = blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        LOGGER.info("是否有物品能力: {}", cap.isPresent());
        LOGGER.info("=== 调试结束 ===");
    }
}