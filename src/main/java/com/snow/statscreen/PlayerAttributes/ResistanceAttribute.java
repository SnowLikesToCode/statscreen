package com.snow.statscreen.PlayerAttributes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class ResistanceAttribute {
    private static final String NBT_KEY = "statscreen.resistance_bonus";
    
    public static int getCurrentResistanceLevel(Player player) {
        if (player == null) return 0;
        CompoundTag persistentData = player.getPersistentData();
        return persistentData.contains(NBT_KEY) ? persistentData.getInt(NBT_KEY) : 0;
    }
    
    private static void saveResistanceLevel(Player player, int level) {
        if (player == null) return;
        CompoundTag persistentData = player.getPersistentData();
        if (level != 0) {
            persistentData.putInt(NBT_KEY, level);
        } else {
            persistentData.remove(NBT_KEY);
        }
    }
    
    public static void syncResistanceLevelToNBT(Player player) {
        if (player == null || player.level().isClientSide) return;
        saveResistanceLevel(player, getCurrentResistanceLevel(player));
    }
    
    public static void loadAndApplyResistanceLevel(Player player) {
        // Resistance is applied via event handlers, no attribute to modify
        syncResistanceLevelToNBT(player);
    }

    public static void increaseResistance(Player player) {
        modifyResistance(player, 1);
    }

    public static void decreaseResistance(Player player) {
        modifyResistance(player, -1);
    }
    
    private static void modifyResistance(Player player, int levelChange) {
        if (player == null) return;
        
        int currentLevel = getCurrentResistanceLevel(player);
        int newLevel = currentLevel + levelChange;
        if (newLevel < 0) return;
        
        // Save on both sides so the UI updates immediately; server remains authoritative
        saveResistanceLevel(player, newLevel);
    }
}

