package com.snow.statscreen.events;

import com.snow.statscreen.PlayerAttributes.PlayerAttributes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
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
        
        // Handle delayed health bonus application after login
        Integer ticksLeft = loginDelay.get(player.getUUID());
        if (ticksLeft != null) {
            if (ticksLeft <= 1) {
                PlayerAttributes.loadAndApplyHealthBonus(player);
                PlayerAttributes.syncHealthBonusToNBT(player);
                loginDelay.remove(player.getUUID());
            } else {
                loginDelay.put(player.getUUID(), ticksLeft - 1);
            }
        }
        
        // Handle regeneration fix
        PlayerAttributes.ensureHealthRegeneration(player);
        
        // Periodic sync to NBT (every 5 seconds)
        if (player.tickCount % 100 == 0) {
            PlayerAttributes.syncHealthBonusToNBT(player);
        }
    }
}
