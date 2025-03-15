package com.github.wadey3636.jpa.mixin;



import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

import static com.github.wadey3636.jpa.features.dungeonfeatures.ProfitCalculatorKt.toggleProfitHud;
import static com.github.wadey3636.jpa.features.dungeonfeatures.ProfitCalculatorKt.highlightSlots;

@Mixin(value = GuiContainer.class, priority = 500)
public abstract class MixinContainerGui extends GuiScreen {
    @Inject(method = "drawSlot", at = @At("HEAD"), cancellable = true)
    public void drawSlot(Slot slot, CallbackInfo ci) {
        if (!toggleProfitHud || !slot.inventory.getDisplayName().getUnformattedText().contains("The Catacombs")
                || !highlightSlots.contains(slot.getSlotIndex())

        ) return;
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 100 + Minecraft.getMinecraft().getRenderItem().zLevel);
        GlStateManager.depthMask(false);
        Gui.drawRect(slot.xDisplayPosition, slot.yDisplayPosition,
                slot.xDisplayPosition + 16, slot.yDisplayPosition + 16, new Color(0, 255, 0, 255).getRGB()
        );
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
    }

}