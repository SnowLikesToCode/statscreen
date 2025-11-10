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
        registrar.play(ChangeStrengthPacket.ID, ChangeStrengthPacket::new, handler -> handler
            .server(ChangeStrengthPacket::handle));
        registrar.play(ChangeResistancePacket.ID, ChangeResistancePacket::new, handler -> handler
            .server(ChangeResistancePacket::handle));
        registrar.play(ChangeDexterityPacket.ID, ChangeDexterityPacket::new, handler -> handler
            .server(ChangeDexterityPacket::handle));
        registrar.play(ChangeRangedPacket.ID, ChangeRangedPacket::new, handler -> handler
            .server(ChangeRangedPacket::handle));
        registrar.play(ChangeHungerPacket.ID, ChangeHungerPacket::new, handler -> handler
            .server(ChangeHungerPacket::handle));
    }
}

