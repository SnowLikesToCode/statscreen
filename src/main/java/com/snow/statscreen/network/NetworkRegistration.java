package com.snow.statscreen.network;

import com.snow.statscreen.StatScreen;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

@Mod.EventBusSubscriber(modid = StatScreen.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NetworkRegistration {
    
    @SubscribeEvent
    public static void register(RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar(StatScreen.MODID);
        registrar.play(ChangeMaxHealthPacket.ID, ChangeMaxHealthPacket::new, handler -> handler
            .server(ChangeMaxHealthPacket::handle));
    }
}

