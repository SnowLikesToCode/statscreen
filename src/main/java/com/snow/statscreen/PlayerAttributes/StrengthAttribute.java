package com.snow.statscreen.PlayerAttributes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class StrengthAttribute {
    private static final String NBT_KEY = "statscreen.strength_bonus";
    private static final double BASE_ATTACK_DAMAGE = 1.0;
    private static final double PERCENTAGE_PER_LEVEL = 0.10;
    
    public static int getCurrentStrengthLevel(Player player) {
        if (player == null) return 0;
        AttributeInstance attackDamageAttribute = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamageAttribute != null) {
            double bonus = attackDamageAttribute.getBaseValue() - BASE_ATTACK_DAMAGE;
            return (int) Math.round(bonus / (BASE_ATTACK_DAMAGE * PERCENTAGE_PER_LEVEL));
        }
        return 0;
    }
    
    private static void saveStrengthLevel(Player player, int level) {
        if (player == null) return;
        CompoundTag persistentData = player.getPersistentData();
        if (level != 0) {
            persistentData.putInt(NBT_KEY, level);
        } else {
            persistentData.remove(NBT_KEY);
        }
    }
    
    public static void syncStrengthLevelToNBT(Player player) {
        if (player == null || player.level().isClientSide) return;
        saveStrengthLevel(player, getCurrentStrengthLevel(player));
    }
    
    public static void loadAndApplyStrengthLevel(Player player) {
        if (player == null || player.level().isClientSide) return;
        AttributeInstance attackDamageAttribute = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamageAttribute == null) return;
        
        CompoundTag persistentData = player.getPersistentData();
        int savedLevel = persistentData.contains(NBT_KEY) ? persistentData.getInt(NBT_KEY) : 0;
        double targetBase = BASE_ATTACK_DAMAGE * (1.0 + savedLevel * PERCENTAGE_PER_LEVEL);
        
        if (Math.abs(attackDamageAttribute.getBaseValue() - targetBase) > 0.01) {
            attackDamageAttribute.setBaseValue(targetBase);
        }
    }

    public static void increaseStrength(Player player) {
        modifyStrength(player, 1);
    }

    public static void decreaseStrength(Player player) {
        modifyStrength(player, -1);
    }
    
    private static void modifyStrength(Player player, int levelChange) {
        if (player == null) return;
        
        AttributeInstance attackDamageAttribute = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamageAttribute == null) return;
        
        int currentLevel = getCurrentStrengthLevel(player);
        int newLevel = currentLevel + levelChange;
        if (newLevel < 0) return;
        
        double newBase = BASE_ATTACK_DAMAGE * (1.0 + newLevel * PERCENTAGE_PER_LEVEL);
        attackDamageAttribute.setBaseValue(newBase);
        
        if (!player.level().isClientSide) {
            saveStrengthLevel(player, newLevel);
        }
    }
}

