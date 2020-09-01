package com.focess.mixins;


import com.focess.SayHello;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.thread.ThreadExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin({net.minecraft.network.NetworkThreadUtils.class})
public class MixinNetworkThreadUtil
{
    @Inject(method = {"forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V"}, at = {@At("RETURN")}, cancellable = true)
    private static <T extends net.minecraft.network.listener.PacketListener> void afterForceMainThread(Packet<T> packet, T packetListener, ThreadExecutor<?> threadExecutor, CallbackInfo ci) {
        if (threadExecutor instanceof MinecraftServer) {
            List<ServerPlayerEntity> playerList = ((MinecraftServer) threadExecutor).getPlayerManager().getPlayerList();
            if (packet instanceof ChatMessageC2SPacket) {
                String message = ((ChatMessageC2SPacket) packet).getChatMessage();
                for (ServerPlayerEntity player : playerList)
                    if (player.networkHandler.equals(packetListener))
                        SayHello.onChat(player, message);
            }
        }
    }
}

