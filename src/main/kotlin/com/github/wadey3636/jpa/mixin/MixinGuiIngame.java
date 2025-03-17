package com.github.wadey3636.jpa.mixin;


import me.modcore.ui.clickgui.ClickGUI;
import me.modcore.ui.playerCustomizerGUI.PlayerCustomizerGUI;
import me.modcore.ui.valuegui.ValueGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



@Mixin(GuiIngame.class)
public class MixinGuiIngame {
    @Inject(method = "renderGameOverlay", at = @At("HEAD"), cancellable = true)
    private void onRenderGameOverlay(float partialTicks, CallbackInfo ci) {
        System.out.println("poop");
        if (
                Minecraft.getMinecraft().currentScreen instanceof PlayerCustomizerGUI ||
                Minecraft.getMinecraft().currentScreen instanceof ClickGUI ||
                Minecraft.getMinecraft().currentScreen instanceof ValueGUI
        ) {
            System.out.println("Cancelling");
            ci.cancel();
        }
    }
}
