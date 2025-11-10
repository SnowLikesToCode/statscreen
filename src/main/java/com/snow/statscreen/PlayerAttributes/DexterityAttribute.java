package com.snow.statscreen.PlayerAttributes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class DexterityAttribute {
    private static final String NBT_KEY = "statscreen.dexterity_bonus";
    private static final double BASE_MOVEMENT_SPEED = 0.1;
    private static final double PERCENTAGE_PER_LEVEL = 0.10;
    
    public static int getCurrentDexterityLevel(Player player) {
        if (player == null) return 0;
        AttributeInstance movementSpeedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeedAttribute != null) {
            double bonus = movementSpeedAttribute.getBaseValue() - BASE_MOVEMENT_SPEED;
            return (int) Math.round(bonus / (BASE_MOVEMENT_SPEED * PERCENTAGE_PER_LEVEL));
        }
        return 0;
    }
    
    private static void saveDexterityLevel(Player player, int level) {
        if (player == null) return;
        CompoundTag persistentData = player.getPersistentData();
        if (level != 0) {
            persistentData.putInt(NBT_KEY, level);
        } else {
            persistentData.remove(NBT_KEY);
        }
    }
    
    public static void syncDexterityLevelToNBT(Player player) {
        if (player == null || player.level().isClientSide) return;
        saveDexterityLevel(player, getCurrentDexterityLevel(player));
    }
    
    public static void loadAndApplyDexterityLevel(Player player) {
        if (player == null || player.level().isClientSide) return;
        AttributeInstance movementSpeedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeedAttribute == null) return;
        
        CompoundTag persistentData = player.getPersistentData();
        int savedLevel = persistentData.contains(NBT_KEY) ? persistentData.getInt(NBT_KEY) : 0;
        double targetBase = BASE_MOVEMENT_SPEED * (1.0 + savedLevel * PERCENTAGE_PER_LEVEL);
        
        if (Math.abs(movementSpeedAttribute.getBaseValue() - targetBase) > 0.01) {
            movementSpeedAttribute.setBaseValue(targetBase);
        }
    }

    public static void increaseDexterity(Player player) {
        modifyDexterity(player, 1);
    }

    public static void decreaseDexterity(Player player) {
        modifyDexterity(player, -1);
    }
    
    private static void modifyDexterity(Player player, int levelChange) {
        if (player == null) return;
        
        AttributeInstance movementSpeedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeedAttribute == null) return;
        
        int currentLevel = getCurrentDexterityLevel(player);
        int newLevel = currentLevel + levelChange;
        if (newLevel < 0) return;
        
        double newBase = BASE_MOVEMENT_SPEED * (1.0 + newLevel * PERCENTAGE_PER_LEVEL);
        movementSpeedAttribute.setBaseValue(newBase);
        
        if (!player.level().isClientSide) {
            saveDexterityLevel(player, newLevel);
        }
    }
}
