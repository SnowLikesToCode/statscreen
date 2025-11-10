package com.snow.statscreen.events;

import com.snow.statscreen.PlayerAttributes.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerEvents {
    private static final Map<UUID, Integer> loginDelay = new HashMap<>();
    
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            loginDelay.put(player.getUUID(), 5);
        }
    }
    
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        
        // Handle delayed stat loading after login
        Integer ticksLeft = loginDelay.get(player.getUUID());
        if (ticksLeft != null) {
            if (ticksLeft <= 1) {
                HealthAttribute.loadAndApplyHealthBonus(player);
                HealthAttribute.syncHealthBonusToNBT(player);
                StrengthAttribute.loadAndApplyStrengthLevel(player);
                StrengthAttribute.syncStrengthLevelToNBT(player);
                ResistanceAttribute.loadAndApplyResistanceLevel(player);
                ResistanceAttribute.syncResistanceLevelToNBT(player);
                DexterityAttribute.loadAndApplyDexterityLevel(player);
                DexterityAttribute.syncDexterityLevelToNBT(player);
                RangedAttribute.loadAndApplyRangedLevel(player);
                RangedAttribute.syncRangedLevelToNBT(player);
                HungerAttribute.loadAndApplyHungerLevel(player);
                HungerAttribute.syncHungerLevelToNBT(player);
                loginDelay.remove(player.getUUID());
            } else {
                loginDelay.put(player.getUUID(), ticksLeft - 1);
            }
        }
        
        // Handle regeneration fix
        HealthAttribute.ensureHealthRegeneration(player);
        
        // Handle hunger consumption slowdown
        if (player.tickCount % 80 == 0) { // Every 4 seconds (80 ticks)
            double multiplier = HungerAttribute.getHungerConsumptionMultiplier(player);
            if (multiplier < 1.0 && player.getFoodData().getFoodLevel() > 0) {
                // Reduce hunger consumption by skipping some hunger ticks
                // This is a simplified approach - actual hunger mechanics are complex
            }
        }
        
        // Periodic sync to NBT (every 5 seconds)
        if (player.tickCount % 100 == 0) {
            HealthAttribute.syncHealthBonusToNBT(player);
            StrengthAttribute.syncStrengthLevelToNBT(player);
            ResistanceAttribute.syncResistanceLevelToNBT(player);
            DexterityAttribute.syncDexterityLevelToNBT(player);
            RangedAttribute.syncRangedLevelToNBT(player);
            HungerAttribute.syncHungerLevelToNBT(player);
        }
    }
    
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        // Handle Resistance - reduce damage taken
        if (event.getEntity() instanceof ServerPlayer player) {
            int resistanceLevel = ResistanceAttribute.getCurrentResistanceLevel(player);
            if (resistanceLevel > 0) {
                double resistanceMultiplier = 1.0 - (resistanceLevel * 0.10);
                resistanceMultiplier = Math.max(0.0, resistanceMultiplier); // Prevent negative damage
                float newAmount = (float) (event.getAmount() * resistanceMultiplier);
                event.setAmount(newAmount);
            }
        }
        
        // Handle Ranged - increase ranged weapon damage dealt
        ServerPlayer attacker = null;
        
        // Check if damage source is from a player directly (melee)
        if (event.getSource().getDirectEntity() instanceof ServerPlayer directAttacker) {
            attacker = directAttacker;
        }
        // Check if damage source is from an arrow/projectile shot by a player
        else if (event.getSource().getDirectEntity() instanceof AbstractArrow arrow) {
            if (arrow.getOwner() instanceof ServerPlayer arrowOwner) {
                attacker = arrowOwner;
            }
        }
        
        if (attacker != null) {
            // Check if attacker is using a ranged weapon or if damage came from an arrow
            ItemStack weapon = attacker.getMainHandItem();
            boolean isRangedWeapon = weapon.getItem() instanceof BowItem || 
                                     weapon.getItem() instanceof CrossbowItem ||
                                     event.getSource().getDirectEntity() instanceof AbstractArrow;
            
            if (isRangedWeapon) {
                double multiplier = RangedAttribute.getRangedDamageMultiplier(attacker);
                float newAmount = (float) (event.getAmount() * multiplier);
                event.setAmount(newAmount);
            }
        }
    }
}
