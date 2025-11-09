package com.snow.statscreen.PlayerAttributes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class PlayerAttributes {
    private static final String NBT_KEY = "statscreen.max_health_bonus";
    private static final double BASE_MAX_HEALTH = 20.0;
    private static final double HEALTH_CHANGE_AMOUNT = 2.0;
    
    public static double getCurrentHealthBonus(Player player) {
        if (player == null) return 0.0;
        AttributeInstance maxHealthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealthAttribute != null) {
            return maxHealthAttribute.getBaseValue() - BASE_MAX_HEALTH;
        }
        return 0.0;
    }
    
    private static void saveHealthBonus(Player player, double bonus) {
        if (player == null) return;
        CompoundTag persistentData = player.getPersistentData();
        if (bonus != 0.0) {
            persistentData.putDouble(NBT_KEY, bonus);
        } else {
            persistentData.remove(NBT_KEY);
        }
    }
    
    public static void syncHealthBonusToNBT(Player player) {
        if (player == null || player.level().isClientSide) return;
        saveHealthBonus(player, getCurrentHealthBonus(player));
    }
    
    public static void loadAndApplyHealthBonus(Player player) {
        if (player == null || player.level().isClientSide) return;
        AttributeInstance maxHealthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealthAttribute == null) return;
        
        CompoundTag persistentData = player.getPersistentData();
        double savedBonus = persistentData.contains(NBT_KEY) ? persistentData.getDouble(NBT_KEY) : 0.0;
        double targetBase = BASE_MAX_HEALTH + savedBonus;
        
        if (Math.abs(maxHealthAttribute.getBaseValue() - targetBase) > 0.01) {
            maxHealthAttribute.setBaseValue(targetBase);
        }
    }
    
    public static void ensureHealthRegeneration(Player player) {
        if (player == null || player.level().isClientSide) return;
        
        float maxHealth = player.getMaxHealth();
        float currentHealth = player.getHealth();
        
        // Fix vanilla bug: allow regeneration above 20 HP when max health is higher
        if (maxHealth > 20.0f && currentHealth == 20.0f) {
            if (player.getFoodData().getFoodLevel() >= 18 && player.getFoodData().getSaturationLevel() > 0) {
                player.setHealth(20.1f);
            }
        }
        
        if (currentHealth > maxHealth) {
            player.setHealth(maxHealth);
        }
    }

    public static void increaseMaxHealth(Player player) {
        modifyMaxHealth(player, HEALTH_CHANGE_AMOUNT);
    }

    public static void decreaseMaxHealth(Player player) {
        modifyMaxHealth(player, -HEALTH_CHANGE_AMOUNT);
    }
    
    private static void modifyMaxHealth(Player player, double change) {
        if (player == null) return;
        
        AttributeInstance maxHealthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealthAttribute == null) return;
        
        double newBase = maxHealthAttribute.getBaseValue() + change;
        if (newBase < BASE_MAX_HEALTH) return;
        
        maxHealthAttribute.setBaseValue(newBase);
        
        if (!player.level().isClientSide) {
            saveHealthBonus(player, newBase - BASE_MAX_HEALTH);
        }
    }
}
