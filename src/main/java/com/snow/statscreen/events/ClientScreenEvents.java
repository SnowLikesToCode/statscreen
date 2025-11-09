package com.snow.statscreen.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import com.snow.statscreen.gui.StatsMenuScreen;

public class ClientScreenEvents {
    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof PauseScreen) {
            int buttonWidth = 100;
            int buttonHeight = 20;
            int buttonX = event.getScreen().width - buttonWidth - 10;
            int buttonY = event.getScreen().height - buttonHeight - 10;
            
            event.addListener(Button.builder(
                Component.translatable("statscreen.stats"),
                button -> Minecraft.getInstance().setScreen(new StatsMenuScreen(event.getScreen()))
            ).bounds(buttonX, buttonY, buttonWidth, buttonHeight).build());
        }
    }
}

