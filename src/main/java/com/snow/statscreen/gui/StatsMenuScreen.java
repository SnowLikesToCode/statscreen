package com.snow.statscreen.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import com.snow.statscreen.StatScreen;
import com.snow.statscreen.network.*;
import com.snow.statscreen.PlayerAttributes.*;
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
        int rowSpacing = 25;
        int buttonWidth = 40;
        int buttonHeight = 20;
        int buttonSpacing = 10;

        Player player = Minecraft.getInstance().player;
        int startX = this.width / 2 - 100;

        // Calculate max label width for consistent alignment
        Component[] statLabels = new Component[] {
            Component.translatable("statscreen.hp"),
            Component.translatable("statscreen.strength"),
            Component.translatable("statscreen.resistance"),
            Component.translatable("statscreen.dexterity"),
            Component.translatable("statscreen.ranged"),
            Component.translatable("statscreen.hunger")
        };
        int maxLabelWidth = 0;
        for (Component c : statLabels) {
            int w = this.font.width(c);
            if (w > maxLabelWidth) maxLabelWidth = w;
        }

        // Consistent button positions: [icon] [label] [-][+] [value]
        int labelXBase = startX + ICON_SIZE + TEXT_SPACING;
        int minusX = labelXBase + maxLabelWidth + BUTTON_SPACING;
        int plusX = minusX + buttonWidth + buttonSpacing;
        
        // Health stat controls
        this.addRenderableWidget(createHealthButton("-", minusX, startY - 2, buttonWidth, buttonHeight, false));
        this.addRenderableWidget(createHealthButton("+", plusX, startY - 2, buttonWidth, buttonHeight, true));

        // Strength stat controls
        int strengthY = startY + rowSpacing;
        this.addRenderableWidget(createStrengthButton("-", minusX, strengthY - 2, buttonWidth, buttonHeight, false));
        this.addRenderableWidget(createStrengthButton("+", plusX, strengthY - 2, buttonWidth, buttonHeight, true));

        // Resistance stat controls
        int resistanceY = startY + rowSpacing * 2;
        this.addRenderableWidget(createResistanceButton("-", minusX, resistanceY - 2, buttonWidth, buttonHeight, false));
        this.addRenderableWidget(createResistanceButton("+", plusX, resistanceY - 2, buttonWidth, buttonHeight, true));

        // Dexterity stat controls
        int dexterityY = startY + rowSpacing * 3;
        this.addRenderableWidget(createDexterityButton("-", minusX, dexterityY - 2, buttonWidth, buttonHeight, false));
        this.addRenderableWidget(createDexterityButton("+", plusX, dexterityY - 2, buttonWidth, buttonHeight, true));

        // Ranged stat controls
        int rangedY = startY + rowSpacing * 4;
        this.addRenderableWidget(createRangedButton("-", minusX, rangedY - 2, buttonWidth, buttonHeight, false));
        this.addRenderableWidget(createRangedButton("+", plusX, rangedY - 2, buttonWidth, buttonHeight, true));

        // Hunger stat controls
        int hungerY = startY + rowSpacing * 5;
        this.addRenderableWidget(createHungerButton("-", minusX, hungerY - 2, buttonWidth, buttonHeight, false));
        this.addRenderableWidget(createHungerButton("+", plusX, hungerY - 2, buttonWidth, buttonHeight, true));
        
        // Back button - moved down to accommodate more stats
        this.addRenderableWidget(Button.builder(
            Component.translatable("gui.back"),
            button -> this.minecraft.setScreen(this.parent)
        ).bounds(this.width / 2 - 100, startY + rowSpacing * 6, 200, 20).build());
    }
    
    private Button createHealthButton(String label, int x, int y, int width, int height, boolean increase) {
        return Button.builder(
            Component.literal(label),
            button -> {
                Player player = Minecraft.getInstance().player;
                if (player != null) {
                    if (increase) {
                        HealthAttribute.increaseMaxHealth(player);
                    } else {
                        HealthAttribute.decreaseMaxHealth(player);
                    }
                }
                PacketDistributor.SERVER.noArg().send(new ChangeMaxHealthPacket(increase));
            }
        ).bounds(x, y, width, height).build();
    }
    
    private Button createStrengthButton(String label, int x, int y, int width, int height, boolean increase) {
        return Button.builder(
            Component.literal(label),
            button -> {
                Player player = Minecraft.getInstance().player;
                if (player != null) {
                    if (increase) {
                        StrengthAttribute.increaseStrength(player);
                    } else {
                        StrengthAttribute.decreaseStrength(player);
                    }
                }
                PacketDistributor.SERVER.noArg().send(new ChangeStrengthPacket(increase));
            }
        ).bounds(x, y, width, height).build();
    }
    
    private Button createResistanceButton(String label, int x, int y, int width, int height, boolean increase) {
        return Button.builder(
            Component.literal(label),
            button -> {
                Player player = Minecraft.getInstance().player;
                if (player != null) {
                    if (increase) {
                        ResistanceAttribute.increaseResistance(player);
                    } else {
                        ResistanceAttribute.decreaseResistance(player);
                    }
                }
                PacketDistributor.SERVER.noArg().send(new ChangeResistancePacket(increase));
            }
        ).bounds(x, y, width, height).build();
    }
    
    private Button createDexterityButton(String label, int x, int y, int width, int height, boolean increase) {
        return Button.builder(
            Component.literal(label),
            button -> {
                Player player = Minecraft.getInstance().player;
                if (player != null) {
                    if (increase) {
                        DexterityAttribute.increaseDexterity(player);
                    } else {
                        DexterityAttribute.decreaseDexterity(player);
                    }
                }
                PacketDistributor.SERVER.noArg().send(new ChangeDexterityPacket(increase));
            }
        ).bounds(x, y, width, height).build();
    }
    
    private Button createRangedButton(String label, int x, int y, int width, int height, boolean increase) {
        return Button.builder(
            Component.literal(label),
            button -> {
                Player player = Minecraft.getInstance().player;
                if (player != null) {
                    if (increase) {
                        RangedAttribute.increaseRanged(player);
                    } else {
                        RangedAttribute.decreaseRanged(player);
                    }
                }
                PacketDistributor.SERVER.noArg().send(new ChangeRangedPacket(increase));
            }
        ).bounds(x, y, width, height).build();
    }
    
    private Button createHungerButton(String label, int x, int y, int width, int height, boolean increase) {
        return Button.builder(
            Component.literal(label),
            button -> {
                Player player = Minecraft.getInstance().player;
                if (player != null) {
                    if (increase) {
                        HungerAttribute.increaseHunger(player);
                    } else {
                        HungerAttribute.decreaseHunger(player);
                    }
                }
                PacketDistributor.SERVER.noArg().send(new ChangeHungerPacket(increase));
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
            int rowSpacing = 25;
            int iconX = startX;
            int labelX = startX + ICON_SIZE + TEXT_SPACING;
            int buttonWidth = 40;
            int buttonSpacing = 10;
            Component[] statLabelsRender = new Component[] {
                Component.translatable("statscreen.hp"),
                Component.translatable("statscreen.strength"),
                Component.translatable("statscreen.resistance"),
                Component.translatable("statscreen.dexterity"),
                Component.translatable("statscreen.ranged"),
                Component.translatable("statscreen.hunger")
            };
            int maxLabelWidthRender = 0;
            for (Component c : statLabelsRender) {
                int w = this.font.width(c);
                if (w > maxLabelWidthRender) maxLabelWidthRender = w;
            }
            int minusXRender = labelX + maxLabelWidthRender + BUTTON_SPACING;
            int plusXRender = minusXRender + buttonWidth + buttonSpacing;
            int valueX = plusXRender + buttonWidth + BUTTON_SPACING;
            
            // Render Health stat
            Component hpLabel = Component.translatable("statscreen.hp");
            String hpAmountText = String.format("%.1f / %.1f", player.getHealth(), player.getMaxHealth());
            guiGraphics.blit(HEART_ICON, iconX, startY - 1, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
            guiGraphics.drawString(this.font, hpLabel, labelX, startY, 16777215, false);
            guiGraphics.drawString(this.font, hpAmountText, valueX, startY, 16777215, false);
            
            // Render Strength stat
            int strengthY = startY + rowSpacing;
            Component strengthLabel = Component.translatable("statscreen.strength");
            String strengthValue = String.format("Level %d", StrengthAttribute.getCurrentStrengthLevel(player));
            guiGraphics.drawString(this.font, strengthLabel, labelX, strengthY, 16777215, false);
            guiGraphics.drawString(this.font, strengthValue, valueX, strengthY, 16777215, false);
            
            // Render Resistance stat
            int resistanceY = startY + rowSpacing * 2;
            Component resistanceLabel = Component.translatable("statscreen.resistance");
            String resistanceValue = String.format("Level %d", ResistanceAttribute.getCurrentResistanceLevel(player));
            guiGraphics.drawString(this.font, resistanceLabel, labelX, resistanceY, 16777215, false);
            guiGraphics.drawString(this.font, resistanceValue, valueX, resistanceY, 16777215, false);
            
            // Render Dexterity stat
            int dexterityY = startY + rowSpacing * 3;
            Component dexterityLabel = Component.translatable("statscreen.dexterity");
            String dexterityValue = String.format("Level %d", DexterityAttribute.getCurrentDexterityLevel(player));
            guiGraphics.drawString(this.font, dexterityLabel, labelX, dexterityY, 16777215, false);
            guiGraphics.drawString(this.font, dexterityValue, valueX, dexterityY, 16777215, false);
            
            // Render Ranged stat
            int rangedY = startY + rowSpacing * 4;
            Component rangedLabel = Component.translatable("statscreen.ranged");
            String rangedValue = String.format("Level %d", RangedAttribute.getCurrentRangedLevel(player));
            guiGraphics.drawString(this.font, rangedLabel, labelX, rangedY, 16777215, false);
            guiGraphics.drawString(this.font, rangedValue, valueX, rangedY, 16777215, false);
            
            // Render Hunger stat
            int hungerY = startY + rowSpacing * 5;
            Component hungerLabel = Component.translatable("statscreen.hunger");
            String hungerValue = String.format("Level %d", HungerAttribute.getCurrentHungerLevel(player));
            guiGraphics.drawString(this.font, hungerLabel, labelX, hungerY, 16777215, false);
            guiGraphics.drawString(this.font, hungerValue, valueX, hungerY, 16777215, false);
        }
    }
}

