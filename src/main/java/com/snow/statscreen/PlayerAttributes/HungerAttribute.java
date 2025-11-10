package com.snow.statscreen.PlayerAttributes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class HungerAttribute {
    private static final String NBT_KEY = "statscreen.hunger_slowdown";
    private static final double PERCENTAGE_PER_LEVEL = 0.10;
    
    public static int getCurrentHungerLevel(Player player) {
        if (player == null) return 0;
        CompoundTag persistentData = player.getPersistentData();
        return persistentData.contains(NBT_KEY) ? persistentData.getInt(NBT_KEY) : 10;
    }
    
    public static double getHungerConsumptionMultiplier(Player player) {
        int level = getCurrentHungerLevel(player);
        // Higher level = slower hunger consumption
        // Level 0 = 1.0 (normal), Level 1 = 0.9, Level 2 = 0.8, etc.
        return Math.max(0.0, 1.0 - (level * PERCENTAGE_PER_LEVEL));
    }
    
    private static void saveHungerLevel(Player player, int level) {
        if (player == null) return;
        CompoundTag persistentData = player.getPersistentData();
        if (level != 0) {
            persistentData.putInt(NBT_KEY, level);
        } else {
            persistentData.remove(NBT_KEY);
        }
    }
    
    public static void syncHungerLevelToNBT(Player player) {
        if (player == null || player.level().isClientSide) return;
        saveHungerLevel(player, getCurrentHungerLevel(player));
    }
    
    public static void loadAndApplyHungerLevel(Player player) {
        // Hunger consumption is modified via event handlers
        syncHungerLevelToNBT(player);
    }

    public static void increaseHunger(Player player) {
        modifyHunger(player, 1);
    }

    public static void decreaseHunger(Player player) {
        modifyHunger(player, -1);
    }
    
    private static void modifyHunger(Player player, int levelChange) {
        if (player == null) return;
        
        int currentLevel = getCurrentHungerLevel(player);
        int newLevel = currentLevel + levelChange;
        if (newLevel < 0) return;
        
        // Save on both sides for immediate UI update; server remains authoritative
        saveHungerLevel(player, newLevel);
    }
}

