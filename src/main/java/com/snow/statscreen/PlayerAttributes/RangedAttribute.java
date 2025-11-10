package com.snow.statscreen.PlayerAttributes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class RangedAttribute {
    private static final String NBT_KEY = "statscreen.ranged_bonus";
    private static final double PERCENTAGE_PER_LEVEL = 0.10;
    
    public static int getCurrentRangedLevel(Player player) {
        if (player == null) return 0;
        CompoundTag persistentData = player.getPersistentData();
        return persistentData.contains(NBT_KEY) ? persistentData.getInt(NBT_KEY) : 0;
    }
    
    public static double getRangedDamageMultiplier(Player player) {
        int level = getCurrentRangedLevel(player);
        return 1.0 + (level * PERCENTAGE_PER_LEVEL);
    }
    
    private static void saveRangedLevel(Player player, int level) {
        if (player == null) return;
        CompoundTag persistentData = player.getPersistentData();
        if (level != 0) {
            persistentData.putInt(NBT_KEY, level);
        } else {
            persistentData.remove(NBT_KEY);
        }
    }
    
    public static void syncRangedLevelToNBT(Player player) {
        if (player == null || player.level().isClientSide) return;
        saveRangedLevel(player, getCurrentRangedLevel(player));
    }
    
    public static void loadAndApplyRangedLevel(Player player) {
        // Ranged damage is applied via event handlers, no attribute to modify
        syncRangedLevelToNBT(player);
    }

    public static void increaseRanged(Player player) {
        modifyRanged(player, 1);
    }

    public static void decreaseRanged(Player player) {
        modifyRanged(player, -1);
    }
    
    private static void modifyRanged(Player player, int levelChange) {
        if (player == null) return;
        
        int currentLevel = getCurrentRangedLevel(player);
        int newLevel = currentLevel + levelChange;
        if (newLevel < 0) return;
        
        // Save on both sides for immediate UI update; server remains authoritative
        saveRangedLevel(player, newLevel);
    }
}

