package com.snow.statscreen.network;

import com.snow.statscreen.StatScreen;
import com.snow.statscreen.PlayerAttributes.PlayerAttributes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record ChangeMaxHealthPacket(boolean increase) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(StatScreen.MODID, "change_max_health");

    public ChangeMaxHealthPacket(FriendlyByteBuf buffer) {
        this(buffer.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBoolean(increase);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static void handle(ChangeMaxHealthPacket packet, PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> {
            if (context.player().orElse(null) instanceof ServerPlayer serverPlayer) {
                if (packet.increase) {
                    PlayerAttributes.increaseMaxHealth(serverPlayer);
                } else {
                    PlayerAttributes.decreaseMaxHealth(serverPlayer);
                }
            }
        });
    }
}

