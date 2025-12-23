package com.example.sophisticatedcooking.client;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientSetup {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void init() {
        LOGGER.info("Initializing client setup for Sophisticated Cooking Bridge");

        // 客户端初始化代码
        // 可以在这里注册渲染器、按键绑定等
    }
}