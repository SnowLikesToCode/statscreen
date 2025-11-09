package com.snow.statscreen.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import com.snow.statscreen.StatScreen;
import com.snow.statscreen.network.ChangeMaxHealthPacket;
import com.snow.statscreen.PlayerAttributes.PlayerAttributes;
import net.neoforged.neoforge.network.PacketDistributor;

public class StatsMenuScreen extends Screen {
    private final Screen parent;
    private static final ResourceLocation STATS_BG = new ResourceLocation(StatScreen.MODID, "textures/gui/stats_menu.png");
    private static final ResourceLocation HEART_ICON = new ResourceLocation(StatScreen.MODID, "textures/gui/icons/heart.png");
    private static final int BG_W = 176;
    private static final int BG_H = 166;

    public StatsMenuScreen(Screen parent) {
        super(Component.translatable("statscreen.stats_menu"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int top = (this.height - BG_H) / 2;
        int startY = top + 20;
        int buttonWidth = 40;
        int buttonHeight = 20;
        int spacing = 10;
        
        // Health stat controls
        int healthButtonY = startY + 25;
        this.addRenderableWidget(createHealthButton("-", centerX - buttonWidth - spacing / 2, healthButtonY, buttonWidth, buttonHeight, false));
        this.addRenderableWidget(createHealthButton("+", centerX + spacing / 2, healthButtonY, buttonWidth, buttonHeight, true));
        
        // Future: Add more stat controls here
        // Example: int speedButtonY = startY + 60;
        // this.addRenderableWidget(createSpeedButton("-", centerX - buttonWidth - spacing / 2, speedButtonY, buttonWidth, buttonHeight, false));
        // this.addRenderableWidget(createSpeedButton("+", centerX + spacing / 2, speedButtonY, buttonWidth, buttonHeight, true));
        
        // Back button
        this.addRenderableWidget(Button.builder(
            Component.translatable("gui.back"),
            button -> this.minecraft.setScreen(this.parent)
        ).bounds(this.width / 2 - 100, top + BG_H - 24, 200, 20).build());
    }
    
    private Button createHealthButton(String label, int x, int y, int width, int height, boolean increase) {
        return Button.builder(
            Component.literal(label),
            button -> {
                Player player = Minecraft.getInstance().player;
                if (player != null) {
                    // Optimistic update for instant GUI feedback
                    if (increase) {
                        PlayerAttributes.increaseMaxHealth(player);
                    } else {
                        PlayerAttributes.decreaseMaxHealth(player);
                    }
                }
                // Send to server (authoritative)
                PacketDistributor.SERVER.noArg().send(new ChangeMaxHealthPacket(increase));
            }
        ).bounds(x, y, width, height).build();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        int left = (this.width - BG_W) / 2;
        int top = (this.height - BG_H) / 2;
        guiGraphics.blit(STATS_BG, left, top, 0, 0, BG_W, BG_H);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, top + 6, 16777215);
        
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            int centerX = this.width / 2;
            int startY = top + 20;
            
            // Render Health stat
            int iconX = centerX - 60;
            int iconY = startY - 1;
            guiGraphics.blit(HEART_ICON, iconX, iconY, 0, 0, 9, 9, 9, 9);
            Component hpLabel = Component.translatable("statscreen.hp");
            Component hpValue = Component.literal(String.format("%.1f / %.1f", player.getHealth(), player.getMaxHealth()));
            guiGraphics.drawCenteredString(this.font, hpLabel, centerX, startY, 16777215);
            guiGraphics.drawCenteredString(this.font, hpValue, centerX, startY + 12, 16777215);
            
            // Future: Add more stat displays here
            // Example: Render Speed stat
            // int speedY = startY + 50;
            // Component speedLabel = Component.translatable("statscreen.speed");
            // Component speedValue = Component.literal(String.format("%.1f", player.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            // guiGraphics.drawCenteredString(this.font, speedLabel, centerX, speedY, 16777215);
            // guiGraphics.drawCenteredString(this.font, speedValue, centerX, speedY + 12, 16777215);
        }
        
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}

