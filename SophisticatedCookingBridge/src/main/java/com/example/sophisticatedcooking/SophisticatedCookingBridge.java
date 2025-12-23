package com.example.sophisticatedcooking;

import com.example.sophisticatedcooking.common.Config;
import com.example.sophisticatedcooking.common.compatibility.SophisticatedStorageCompat;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SophisticatedCookingBridge.MOD_ID)
public class SophisticatedCookingBridge {
    public static final String MOD_ID = "sophisticatedcooking";
    public static final Logger LOGGER = LogManager.getLogger();

    public SophisticatedCookingBridge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册配置
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC, "sophisticatedcooking-common.toml");

        // 注册初始化事件
        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        LOGGER.info("Sophisticated Cooking Bridge mod constructed");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Starting Sophisticated Cooking Bridge setup");

        // 在后台线程中初始化反射兼容层
        event.enqueueWork(() -> {
            try {
                // 初始化反射兼容层
                SophisticatedStorageCompat.init();
                LOGGER.info("Sophisticated Cooking Bridge setup complete");
            } catch (Exception e) {
                LOGGER.error("Failed to setup Sophisticated Cooking Bridge", e);
            }
        });
    }
}