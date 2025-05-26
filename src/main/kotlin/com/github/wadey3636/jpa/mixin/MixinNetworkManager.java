/*package com.github.wadey3636.core.mixin;

import io.netty.channel.ChannelHandlerContext;
import com.github.wadey3636.jpa.events.impl.PacketEvent;
import com.github.wadey3636.jpa.utils.ServerUtils;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.wadey3636.jpa.utils.Utils.postAndCatch;

@Mixin(value = {NetworkManager.class}, priority = 1001)
public class MixinNetworkManager {

    @Inject(method = "channelRead0*", at = @At("HEAD"), cancellable = true)
    private void onReceivePacket(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
        if (postAndCatch(new PacketEvent.Receive(packet)) && !ci.isCancelled()) ci.cancel();
    }


    @Inject(method = {"sendPacket(Lnet/minecraft/network/Packet;)V"}, at = {@At("HEAD")}, cancellable = true)
    private void onSendPacket(Packet<?> packet, CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(new PacketEvent.Send(packet)))
            ci.cancel();
    }
}*/

package com.github.wadey3636.jpa.mixin;

import io.netty.channel.ChannelHandlerContext;
import com.github.wadey3636.jpa.events.impl.PacketEvent;
import com.github.wadey3636.jpa.utils.ServerUtils;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.wadey3636.jpa.utils.Utils.postAndCatch;

@Mixin(value = NetworkManager.class, priority = 1002)
public class MixinNetworkManager {

    @Inject(method = "channelRead0*", at = @At("HEAD"), cancellable = true)
    private void onReceivePacket(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
        if (postAndCatch(new PacketEvent.Receive(packet)) && !ci.isCancelled()) ci.cancel();
    }

    @Inject(method = {"sendPacket(Lnet/minecraft/network/Packet;)V"}, at = {@At("HEAD")}, cancellable = true)
    private void onSendPacket(Packet<?> packet, CallbackInfo ci) {
        if (!ServerUtils.handleSendPacket(packet))
            if (postAndCatch(new PacketEvent.Send(packet)) && !ci.isCancelled()) ci.cancel();
    }
}

