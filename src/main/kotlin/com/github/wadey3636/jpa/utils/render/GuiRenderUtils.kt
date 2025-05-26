package com.github.wadey3636.jpa.utils.render


import com.github.wadey3636.jpa.utils.ChestSize
import gg.essential.universal.UMatrixStack
import com.github.wadey3636.jpa.Core.mc
import com.github.wadey3636.jpa.font.FontRenderer
import com.github.wadey3636.jpa.ui.clickgui.util.ColorUtil
import com.github.wadey3636.jpa.ui.util.MouseUtils.isAreaHovered
import com.github.wadey3636.jpa.ui.util.shader.RoundedRect
import com.github.wadey3636.jpa.utils.*
import com.github.wadey3636.jpa.utils.render.RenderUtils.drawTexturedModalRect
import com.github.wadey3636.jpa.utils.render.RenderUtils.loadBufferedImage
import com.github.wadey3636.jpa.utils.render.TextAlign.Left
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.WorldRenderer
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.GL11

val matrix = UMatrixStack.Compat
val scaleFactor get() = ScaledResolution(mc).scaleFactor.toFloat()
private val arrowIcon = DynamicTexture(loadBufferedImage("/assets/modcore/clickgui/arrow.png"))
private val arrowGrayIcon = DynamicTexture(loadBufferedImage("/assets/modcore/clickgui/arrowGray.png"))
private val chestTexture = ResourceLocation("textures/gui/container/generic_54.png")

data class Box(var x: Number, var y: Number, var w: Number, var h: Number)
data class BoxWithClass<T : Number>(var x: T, var y: T, var w: T, var h: T)

fun Box.expand(factor: Number): Box = Box(this.x - factor, this.y - factor, this.w + factor * 2, this.h + factor * 2)
fun Box.isPointWithin(x: Number, y: Number): Boolean {
    return x.toDouble() >= this.x.toDouble() &&
            y.toDouble() >= this.y.toDouble() &&
            x.toDouble() <= (this.x.toDouble() + this.w.toDouble()) &&
            y.toDouble() <= (this.y.toDouble() + this.h.toDouble())
}

fun roundedRectangle(
    x: Number, y: Number, w: Number, h: Number,
    color: Color, borderColor: Color, shadowColor: Color,
    borderThickness: Number, topL: Number, topR: Number, botL: Number, botR: Number, edgeSoftness: Number,
    color2: Color = color, gradientDir: Int = 0, shadowSoftness: Float = 0f
) {
    matrix.runLegacyMethod(matrix.get()) {
        RoundedRect.drawRectangle(
            matrix.get(),
            x.toFloat(),
            y.toFloat(),
            w.toFloat(),
            h.toFloat(),
            color,
            borderColor,
            shadowColor,
            borderThickness.toFloat(),
            topL.toFloat(),
            topR.toFloat(),
            botL.toFloat(),
            botR.toFloat(),
            edgeSoftness.toFloat(),
            color2,
            gradientDir,
            shadowSoftness
        )
    }
}

fun roundedRectangle(
    x: Number,
    y: Number,
    w: Number,
    h: Number,
    color: Color,
    radius: Number = 0f,
    edgeSoftness: Number = 0.5f
) =
    roundedRectangle(
        x.toFloat(), y.toFloat(), w.toFloat(), h.toFloat(), color, color, color,
        0f, radius.toFloat(), radius.toFloat(), radius.toFloat(), radius.toFloat(), edgeSoftness
    )

fun roundedRectangle(box: Box, color: Color, radius: Number = 0f, edgeSoftness: Number = .5f) =
    roundedRectangle(box.x, box.y, box.w, box.h, color, radius, edgeSoftness)

fun <T : Number> roundedRectangle(box: BoxWithClass<T>, color: Color, radius: Number = 0f, edgeSoftness: Number = .5f) =
    roundedRectangle(box.x, box.y, box.w, box.h, color, radius, edgeSoftness)


fun rectangleOutline(
    x: Number,
    y: Number,
    w: Number,
    h: Number,
    color: Color,
    radius: Number = 0f,
    thickness: Number,
    edgeSoftness: Number = 1f
) {
    roundedRectangle(
        x,
        y,
        w,
        h,
        Color.TRANSPARENT,
        color,
        Color.TRANSPARENT,
        thickness,
        radius,
        radius,
        radius,
        radius,
        edgeSoftness
    )
}

fun gradientRect(
    x: Float,
    y: Float,
    w: Float,
    h: Float,
    color1: Color,
    color2: Color,
    radius: Float,
    direction: GradientDirection = GradientDirection.Right,
    borderColor: Color = Color.TRANSPARENT,
    borderThickness: Number = 0f
) {
    if (color1.isTransparent && color2.isTransparent) return
    roundedRectangle(
        x,
        y,
        w,
        h,
        color1.coerceAlpha(.1f, 1f),
        borderColor,
        Color.TRANSPARENT,
        borderThickness,
        radius,
        radius,
        radius,
        radius,
        3,
        color2.coerceAlpha(.1f, 1f),
        direction.ordinal
    )
}

fun drawHSBBox(x: Float, y: Float, w: Float, h: Float, color: Color) {
    matrix.runLegacyMethod(matrix.get()) {
        RoundedRect.drawHSBBox(
            matrix.get(),
            x,
            y,
            w,
            h,
            color,
        )
    }
    rectangleOutline(x - 1, y - 1, w + 2, h + 2, Color(38, 38, 38), 3f, 2f)
}

fun circle(
    x: Number,
    y: Number,
    radius: Number,
    color: Color,
    borderColor: Color = color,
    borderThickness: Number = 0f
) {
    matrix.runLegacyMethod(matrix.get()) {
        RoundedRect.drawCircle(
            matrix.get(),
            x.toFloat(),
            y.toFloat(),
            radius.toFloat(),
            color,
            borderColor,
            borderThickness.toFloat()
        )
    }
}

fun text(
    text: String,
    x: Number,
    y: Number,
    color: Color,
    size: Number,
    type: Int = FontRenderer.REGULAR,
    align: TextAlign = Left,
    verticalAlign: TextPos = TextPos.Middle,
    shadow: Boolean = false
) {
    FontRenderer.text(text, x.toFloat(), y.toFloat(), color, size.toFloat(), align, verticalAlign, shadow, type)
}

fun mcText(
    text: String,
    x: Number,
    y: Number,
    scale: Number,
    color: Color,
    shadow: Boolean = true,
    center: Boolean = true
) {
    RenderUtils.drawText("$text§r", x.toFloat(), y.toFloat(), scale.toDouble(), color, shadow, center)
}

fun textAndWidth(
    text: String,
    x: Float,
    y: Float,
    color: Color,
    size: Float,
    type: Int = FontRenderer.REGULAR,
    align: TextAlign = Left,
    verticalAlign: TextPos = TextPos.Middle,
    shadow: Boolean = false
): Float {
    text(text, x, y, color, size, type, align, verticalAlign, shadow)
    return getTextWidth(text, size).toFloat()
}

fun mcTextAndWidth(
    text: String,
    x: Number,
    y: Number,
    scale: Number,
    color: Color,
    shadow: Boolean = true,
    center: Boolean = true
): Float {
    mcText(text, x, y, scale, color, shadow, center)
    return getMCTextWidth(text).toFloat()
}

fun getMCTextWidth(text: String) = mc.fontRendererObj.getStringWidth(text)

fun getTextWidth(text: String, size: Float) = FontRenderer.getTextWidth(text, size) / 8

fun getMCTextHeight() = mc.fontRendererObj.FONT_HEIGHT

fun getTextHeight(size: Float) = FontRenderer.getTextHeight(size) / 8

fun translate(x: Number, y: Number, z: Number = 1f) = GlStateManager.translate(x.toDouble(), y.toDouble(), z.toDouble())

fun rotate(degrees: Float, xPos: Float, yPos: Float, zPos: Float, xAxis: Float, yAxis: Float, zAxis: Float) {
    translate(xPos, yPos, zPos)
    GlStateManager.rotate(degrees, xAxis, yAxis, zAxis)
    translate(-xPos, -yPos, -zPos)
}

fun scale(x: Number, y: Number, z: Number = 1f) = GlStateManager.scale(x.toDouble(), y.toDouble(), z.toDouble())

fun dropShadow(
    x: Number,
    y: Number,
    w: Number,
    h: Number,
    shadowColor: Color,
    shadowSoftness: Number,
    topL: Number,
    topR: Number,
    botL: Number,
    botR: Number
) {
    return //removed for now
    translate(0f, 0f, -100f)

    matrix.runLegacyMethod(matrix.get()) {
        RoundedRect.drawDropShadow(
            matrix.get(),
            (x - shadowSoftness / 2).toFloat(),
            (y - shadowSoftness / 2).toFloat(),
            (w + shadowSoftness).toFloat(),
            (h + shadowSoftness).toFloat(),
            shadowColor,
            topL.toFloat(),
            topR.toFloat(),
            botL.toFloat(),
            botR.toFloat(),
            shadowSoftness.toFloat()
        )
    }

    translate(0f, 0f, 100f)
}

fun dropShadow(
    x: Number,
    y: Number,
    w: Number,
    h: Number,
    radius: Number,
    shadowSoftness: Number = 1f,
    shadowColor: Color = ColorUtil.moduleButtonColor
) {
    dropShadow(x, y, w, h, shadowColor, shadowSoftness, radius, radius, radius, radius)
}

fun dropShadow(
    box: Box,
    radius: Number,
    shadowSoftness: Number = 1f,
    shadowColor: Color = ColorUtil.moduleButtonColor
) =
    dropShadow(box.x, box.y, box.w, box.h, radius, shadowSoftness, shadowColor)

data class Scissor(val x: Number, val y: Number, val w: Number, val h: Number, val context: Int)

private val scissorList = mutableListOf(Scissor(0, 0, 16000, 16000, 0))

fun scissor(x: Number, y: Number, w: Number, h: Number): Scissor {
    GL11.glEnable(GL11.GL_SCISSOR_TEST)
    GL11.glScissor(x.toInt(), Display.getHeight() - y.toInt() - h.toInt(), w.toInt(), h.toInt())
    val scissor = Scissor(x, y, w, h, scissorList.size)
    scissorList.add(scissor)
    return scissor
}

fun resetScissor(scissor: Scissor) {
    val nextScissor = scissorList[scissor.context - 1]
    GL11.glScissor(nextScissor.x.toInt(), nextScissor.y.toInt(), nextScissor.w.toInt(), nextScissor.h.toInt())
    GL11.glDisable(GL11.GL_SCISSOR_TEST)
    scissorList.removeLast()
}

fun drawArrow(xpos: Float, ypos: Float, rotation: Float = 90f, scale: Float = 1f) {
    GlStateManager.pushMatrix()
    GlStateManager.translate(xpos, ypos, 0f)
    GlStateManager.rotate(rotation, 0f, 0f, 1f)
    GlStateManager.scale(scale, scale, 1f)
    GlStateManager.translate(-xpos, -ypos, 0f)
    drawDynamicTexture(
        if (
            isAreaHovered(
                xpos - 25 / 2 * scale,
                ypos - 25 / 2 * scale,
                25 * scale, 25 * scale
            )
        ) arrowGrayIcon else arrowIcon,
        xpos - 25 / 2 * scale,
        ypos - 25 / 2 * scale,
        25 * scale,
        25 * scale
    )
    GlStateManager.popMatrix()

}

fun drawX(x: Float, y: Float, scale: Float, color: Color) {
    GlStateManager.pushMatrix()
    GlStateManager.translate(x, y, 0f)
    GlStateManager.rotate(45f, 0f, 0f, 1f)
    GlStateManager.scale(scale, scale, 1f)
    GlStateManager.translate(-x, -y, 0f)
    roundedRectangle(x, y - 8.5, 3, 20, color)
    roundedRectangle(x - 8.5, y, 20, 3, color)
    GlStateManager.popMatrix()
}

fun drawDynamicTexture(dynamicTexture: DynamicTexture, x: Number, y: Number, w: Number, h: Number) {

    val isBlendEnabled = GL11.glIsEnabled(GL11.GL_BLEND)
    val isAlphaEnabled = GL11.glIsEnabled(GL11.GL_ALPHA_TEST)
    GlStateManager.pushMatrix()

    if (!isBlendEnabled) GlStateManager.enableBlend()
    if (!isAlphaEnabled) GlStateManager.enableAlpha()

    dynamicTexture.updateDynamicTexture()
    GlStateManager.bindTexture(dynamicTexture.glTextureId)
    drawTexturedModalRect(x.toInt(), y.toInt(), w.toInt(), h.toInt())

    if (!isBlendEnabled) GlStateManager.disableBlend()
    if (!isAlphaEnabled) GlStateManager.disableAlpha()

    GlStateManager.popMatrix()
}


fun drawChest(x: Float, y: Float, scale: Float, chestSize: ChestSize) {
    GlStateManager.pushMatrix()
    scale(scale, scale, 1f)
    translate(x / scale, y / scale, 1f)
    GlStateManager.color(1f, 1f, 1f, 1f)
    if (chestSize == ChestSize.Double) {
        drawCustomTexturedRect(
            chestTexture,
            0f, 0f, 176f, 125f, 0f, 0f, 176f, 125f, 256f, 256f
        )

        drawCustomTexturedRect(
            chestTexture,
            0f, 125f, 176f, 7f,
            0f, 215f,
            176f, 7f,
            256f, 256f
        )
    } else {
        drawCustomTexturedRect(
            chestTexture,
            0f, 0f, 176f, 71f,
            0f, 0f,
            176f, 71f,
            256f, 256f
        )

        drawCustomTexturedRect(
            chestTexture,
            0f, 71f, 176f, 7f,
            0f, 215f,
            176f, 7f,
            256f, 256f
        )
    }


    GlStateManager.popMatrix()
}


fun drawCustomTexturedRect(
    texture: ResourceLocation,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    u: Float,
    v: Float,
    uWidth: Float,
    vHeight: Float,
    textureWidth: Float,
    textureHeight: Float
) {
    mc.textureManager.bindTexture(texture)
    val tessellator = Tessellator.getInstance()
    val worldRenderer = tessellator.worldRenderer
    val minU = u / textureWidth
    val maxU = (u + uWidth) / textureWidth
    val minV = v / textureHeight
    val maxV = (v + vHeight) / textureHeight
    fun worldRenderer(block: WorldRenderer.() -> Unit) {
        worldRenderer.block()
    }
    worldRenderer {
        begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        pos(x.toDouble(), (y + height).toDouble(), 0.0)
            .tex(minU.toDouble(), maxV.toDouble())
            .endVertex()
        pos((x + width).toDouble(), (y + height).toDouble(), 0.0)
            .tex(maxU.toDouble(), maxV.toDouble())
            .endVertex()
        pos((x + width).toDouble(), y.toDouble(), 0.0)
            .tex(maxU.toDouble(), minV.toDouble())
            .endVertex()
        pos(x.toDouble(), y.toDouble(), 0.0)
            .tex(minU.toDouble(), minV.toDouble())
            .endVertex()
    }
    tessellator.draw()
}


fun drawTexture(texture: ResourceLocation, x: Number, y: Number, w: Number, h: Number) {

    val isBlendEnabled = GL11.glIsEnabled(GL11.GL_BLEND)
    val isAlphaEnabled = GL11.glIsEnabled(GL11.GL_ALPHA_TEST)
    GlStateManager.pushMatrix()
    if (!isBlendEnabled) GlStateManager.enableBlend()
    if (!isAlphaEnabled) GlStateManager.enableAlpha()
    mc.textureManager.bindTexture(texture)
    drawTexturedModalRect(x.toInt(), y.toInt(), w.toInt(), h.toInt())
    if (!isBlendEnabled) GlStateManager.disableBlend()
    if (!isAlphaEnabled) GlStateManager.disableAlpha()

    GlStateManager.popMatrix()
}

fun wrappedText(
    text: String,
    x: Float,
    y: Float,
    w: Float,
    color: Color,
    size: Float,
    type: Int = FontRenderer.REGULAR,
    shadow: Boolean = false
) {
    FontRenderer.wrappedText(text, x, y, w, color, size, type, shadow = shadow)
}

fun wrappedTextBounds(text: String, width: Float, size: Float): Pair<Float, Float> {
    return FontRenderer.wrappedTextBounds(text, width, size)
}

enum class TextAlign {
    Left, Middle, Right
}

enum class TextPos {
    Top, Bottom, Middle
}

enum class GradientDirection {
    Right, Down, Left, Up
}
