package com.github.wadey3636.jpa.ui

import com.github.wadey3636.jpa.features.render.ClickGUIModule
import com.github.wadey3636.jpa.font.FontRenderer
import com.github.wadey3636.jpa.ui.clickgui.util.ColorUtil.darker
import com.github.wadey3636.jpa.utils.render.*
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager

class GuiButton(
    buttonId: Int, x: Int, y: Int, width: Int, height: Int, buttonText: String?, private val textSize: Float
) : GuiButton(
    buttonId, x, y, width, height, buttonText
) {

    init {
        this.id = buttonId
        this.xPosition = (x / scaleFactor).toInt()
        this.yPosition = (y / scaleFactor).toInt()
        this.width = (width / scaleFactor).toInt()
        this.height = (height / scaleFactor).toInt()
        this.displayString = buttonText
    }

    override fun drawButton(mc: Minecraft?, mouseX: Int, mouseY: Int) {
        if (!this.visible) return
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        this.hovered =
            (mouseX >= this.xPosition) && (mouseY >= this.yPosition) && (mouseX < this.xPosition + this.width) && (mouseY < this.yPosition + this.height)
        val hoverState = this.getHoverState(this.hovered)
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        GlStateManager.blendFunc(770, 771)
        roundedRectangle(
            this.xPosition,
            this.yPosition,
            this.width,
            this.height,
            ClickGUIModule.color,
            if (hoverState == 2) Color.WHITE else ClickGUIModule.color.darker(0.3f),
            Color.BLACK,
            2f,
            4f,
            4f,
            4f,
            4f,
            1f
        )
        text(
            this.displayString,
            this.xPosition + this.width / 2f,
            this.yPosition + height / 2f,
            Color.WHITE,
            textSize / scaleFactor,
            FontRenderer.REGULAR,
            TextAlign.Middle,
            TextPos.Middle,
            true
        )
    }

}