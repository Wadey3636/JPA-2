package com.github.wadey3636.jpa.mixin;


import com.github.wadey3636.jpa.events.impl.ClickEvent;
import com.github.wadey3636.jpa.events.impl.InputEvent;
import com.github.wadey3636.jpa.utils.KeyManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.github.wadey3636.jpa.utils.KeybindHelper;

import static com.github.wadey3636.jpa.utils.Utils.postAndCatch;


@Mixin(value = {Minecraft.class}, priority = 800)
public class MixinMinecraft {

    @Inject(method = {"runTick"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;dispatchKeypresses()V")})
    public void keyPresses_jpa(CallbackInfo ci) {
        if (Keyboard.getEventKeyState())
            postAndCatch(new InputEvent.Keyboard((Keyboard.getEventKey() == 0) ? (Keyboard.getEventCharacter() + 256) : Keyboard.getEventKey()));
    }

    @Inject(method = {"runTick"}, at = {@At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventButton()I", remap = false)})
    public void mouseKeyPresses_jpa(CallbackInfo ci) {
        if (Mouse.getEventButtonState()) postAndCatch(new InputEvent.Mouse(Mouse.getEventButton()));
    }

    @Inject(method = "rightClickMouse", at = @At("HEAD"), cancellable = true)
    private void rightClickMouse_jpa(CallbackInfo ci) {
        if (postAndCatch(new ClickEvent.Right())) ci.cancel();

    }

    @Inject(method = "clickMouse", at = @At("HEAD"), cancellable = true)
    private void clickMouse_jpa(CallbackInfo ci) {
        if (postAndCatch(new ClickEvent.Left())) ci.cancel();
    }



}