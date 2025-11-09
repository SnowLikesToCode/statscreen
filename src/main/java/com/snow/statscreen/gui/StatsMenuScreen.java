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
    private static final ResourceLocation HEART_ICON = new ResourceLocation(StatScreen.MODID, "textures/gui/icons/heart.png");
    private static final int ICON_SIZE = 9;
    private static final int TEXT_SPACING = 4;
    private static final int BUTTON_SPACING = 6;

    public StatsMenuScreen(Screen parent) {
        super(Component.translatable("statscreen.stats_menu"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        
        int startY = this.height / 4 + 20;
        int buttonWidth = 40;
        int buttonHeight = 20;
        int buttonSpacing = 10;
        
        // Calculate button positions based on text widths
        Player player = Minecraft.getInstance().player;
        String hpAmountText = player != null ? String.format("%.1f / %.1f", player.getHealth(), player.getMaxHealth()) : "0.0 / 0.0";
        Component hpLabel = Component.translatable("statscreen.hp");
        int hpLabelWidth = this.font.width(hpLabel);
        int hpValueWidth = this.font.width(hpAmountText);
        
        // Position: [icon] [HP label] [HP value] [-] [+]
        int startX = this.width / 2 - 100; // Left edge of health row
        int hpLabelX = startX + ICON_SIZE + TEXT_SPACING;
        int hpValueX = hpLabelX + hpLabelWidth + BUTTON_SPACING;
        int minusX = hpValueX + hpValueWidth + BUTTON_SPACING;
        int plusX = minusX + buttonWidth + buttonSpacing;
        
        // Health stat controls
        this.addRenderableWidget(createHealthButton("-", minusX, startY - 2, buttonWidth, buttonHeight, false));
        this.addRenderableWidget(createHealthButton("+", plusX, startY - 2, buttonWidth, buttonHeight, true));
        
        // Future: Add more stat controls here
        // Example: int speedY = startY + 40;
        // this.addRenderableWidget(createSpeedButton("-", minusX, speedY - 2, buttonWidth, buttonHeight, false));
        // this.addRenderableWidget(createSpeedButton("+", plusX, speedY - 2, buttonWidth, buttonHeight, true));
        
        // Back button
        this.addRenderableWidget(Button.builder(
            Component.translatable("gui.back"),
            button -> this.minecraft.setScreen(this.parent)
        ).bounds(this.width / 2 - 100, this.height / 4 + 120, 200, 20).build());
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
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
        
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            int startY = this.height / 4 + 20;
            int startX = this.width / 2 - 100;
            
            // Render Health stat: [icon] [HP label] [HP value]
            Component hpLabel = Component.translatable("statscreen.hp");
            String hpAmountText = String.format("%.1f / %.1f", player.getHealth(), player.getMaxHealth());
            int iconX = startX;
            int hpLabelX = startX + ICON_SIZE + TEXT_SPACING;
            int hpValueX = hpLabelX + this.font.width(hpLabel) + BUTTON_SPACING;
            
            guiGraphics.blit(HEART_ICON, iconX, startY - 1, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
            guiGraphics.drawString(this.font, hpLabel, hpLabelX, startY, 16777215, false);
            guiGraphics.drawString(this.font, hpAmountText, hpValueX, startY, 16777215, false);
            
            // Future: Add more stat displays here
            // Example: Render Speed stat
            // int speedY = startY + 40;
            // Component speedLabel = Component.translatable("statscreen.speed");
            // Component speedValue = Component.literal(String.format("%.1f", player.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            // guiGraphics.drawString(this.font, speedLabel, hpLabelX, speedY, 16777215, false);
            // guiGraphics.drawString(this.font, speedValue, hpValueX, speedY, 16777215, false);
        }
    }
}

