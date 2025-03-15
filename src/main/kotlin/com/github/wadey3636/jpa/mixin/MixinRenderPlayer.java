package com.github.wadey3636.jpa.mixin;


import com.github.wadey3636.jpa.features.render.PlayerRenderer;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderPlayer.class)
public class MixinRenderPlayer {
    @Inject(method = "preRenderCallback(Lnet/minecraft/client/entity/AbstractClientPlayer;F)V", at = @At("TAIL"))
    private void onPreRenderCallback(AbstractClientPlayer entitylivingbaseIn, float partialTickTime, CallbackInfo ci) {
        PlayerRenderer.INSTANCE.preRenderCallbackScaleHook(entitylivingbaseIn);
    }
    @Inject(method = "getEntityTexture(Lnet/minecraft/client/entity/AbstractClientPlayer;)Lnet/minecraft/util/ResourceLocation;", at = @At("HEAD"))
    private void onGetEntityTexture(AbstractClientPlayer player, CallbackInfoReturnable<ResourceLocation> cir)  {
        cir.setReturnValue(PlayerRenderer.INSTANCE.injectCustomSkin(player));
    }

}