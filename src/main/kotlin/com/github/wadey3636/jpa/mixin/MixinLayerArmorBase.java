package com.github.wadey3636.jpa.mixin;


import com.github.wadey3636.jpa.features.render.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayerArmorBase.class)
public class MixinLayerArmorBase {

    //@Inject(method = "renderLayer", at = @At("HEAD"), cancellable = true)
    private void onRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177182_2_, float p_177182_3_, float partialTicks, float p_177182_5_, float p_177182_6_, float p_177182_7_, float scale, int armorSlot, CallbackInfo ci) {
        //if (PlayerRenderer.INSTANCE.renderLayer(entitylivingbaseIn, armorSlot)) ci.cancel();

    }
}
