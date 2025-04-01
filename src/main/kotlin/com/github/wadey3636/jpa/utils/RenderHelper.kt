package com.github.wadey3636.jpa.utils

//import net.minecraft.client.renderer.entity.RenderManager

import com.github.wadey3636.jpa.utils.GuiUtils.deformat
import me.modcore.Core.mc
import me.modcore.ui.clickgui.util.ColorUtil
import me.modcore.ui.util.MouseUtils.isAreaHovered
import me.modcore.utils.Vec2
import me.modcore.utils.render.*
import me.modcore.utils.render.RenderUtils.bind
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.WorldRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockPos
import net.minecraft.util.MathHelper
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

object RenderHelper {
    private val beaconBeam = ResourceLocation("textures/entity/beacon_beam.png")
    //private val renderManager: RenderManager = mc.renderManager

    private fun phaseCheck(phase: Boolean) {
        if (!phase) GlStateManager.enableDepth() else GlStateManager.disableDepth()
        GlStateManager.depthMask(!phase)
    }

    private fun resetPhase() {
        GlStateManager.enableDepth()
        GlStateManager.depthMask(true)
    }


    //from Skytils
    fun getViewerPos(partialTicks: Float): Triple<Double, Double, Double> {
        val viewer = mc.renderViewEntity
        val viewerX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks
        val viewerY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks
        val viewerZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks
        return Triple(viewerX, viewerY, viewerZ)
    }


    fun argbToInt(alpha: Int, red: Int, green: Int, blue: Int): Int {
        return (alpha shl 24) or (red shl 16) or (green shl 8) or blue
    }


    fun renderTitle(text: String, scale: Int, color: Int, duration: Long) {
        title = text
        size = scale.toFloat()
        color1 = color
        time = duration
        timeStamp = System.currentTimeMillis()
    }


    fun renderTitle(text: String, scale: Float, color: Int, duration: Long) {
        title = text
        size = scale
        color1 = color
        time = duration
        timeStamp = System.currentTimeMillis()
    }

    fun drawCenteredText(text: String, scale: Float, color: Int, xPos: Float, yPos: Float) {
        val fontRenderer = mc.fontRendererObj
        val scaledWidth = fontRenderer.getStringWidth(text) * scale
        val scaledHeight = fontRenderer.FONT_HEIGHT * scale
        GlStateManager.pushMatrix()
        GlStateManager.scale(scale, scale, scale)
        fontRenderer.drawStringWithShadow(
            text,
            (xPos - (scaledWidth / 2)) / scale,
            (yPos - (scaledHeight / 2)) / scale,
            color
        )
        GlStateManager.popMatrix()
    }

    fun drawLeftAlignedText(text: String, scale: Float, color: Int, xPos: Float, yPos: Float) {
        val fontRenderer = mc.fontRendererObj
        val scaledHeight = fontRenderer.FONT_HEIGHT * scale
        GlStateManager.pushMatrix()
        GlStateManager.scale(scale, scale, scale)
        fontRenderer.drawStringWithShadow(
            text,
            (xPos) / scale,
            (yPos - (scaledHeight / 2)) / scale,
            color
        )
        GlStateManager.popMatrix()
    }

    /**
     * Taken from Skyblockcatia under MIT License
     * Modified
     * https://github.com/SteveKunG/SkyBlockcatia/blob/1.8.9/LICENSE.md
     *
     * @author SteveKunG
     */
    fun renderItem(itemStack: ItemStack?, x: Int, y: Int, scale: Float = 1f) {
        GlStateManager.pushMatrix()
        scale(scale, scale, scale)
        RenderHelper.enableGUIStandardItemLighting()
        GlStateManager.enableDepth()
        mc.renderItem.renderItemAndEffectIntoGUI(itemStack, (x / scale).roundToInt(), (y / scale).roundToInt())
        scale(1 / scale, 1 / scale, 1 / scale)
        GlStateManager.popMatrix()
    }


    fun renderItem(
        itemStack: ItemStack?,
        x: Float,
        y: Float,
        scaleX: Float = 1f,
        scaleY: Float = 1f,
        scaleZ: Float = 1f
    ) {
        GlStateManager.pushMatrix()

        GlStateManager.color(1f, 1f, 1f, 1f)
        RenderHelper.enableGUIStandardItemLighting()
        GlStateManager.enableDepth()
        scale(scaleX, scaleY, 1f)
        GlStateManager.translate(x / scaleX, y / scaleY, 0f)
        mc.renderItem.renderItemAndEffectIntoGUI(itemStack, 0, 0)
        mc.renderItem.renderItemOverlayIntoGUI(
            mc.fontRendererObj,
            itemStack,
            0,
            0,
            null
        )
        GlStateManager.popMatrix()
    }


    fun getSlotXY(index: Double): Vec2 {
        val slot = (index / 9).toString()
        return Vec2(slot[2].toString().toInt(), slot[0].toString().toInt())
    }

    fun getSlotXY(index: Int): Vec2 {
        return getSlotXY(index.toDouble())
    }


    fun trace(
        pos: BlockPos,
        viewerPos: Triple<Double, Double, Double>,
        color: Color,
        thickness: Float,
        phase: Boolean
    ) {
        drawLine3d(
            0.0,
            cameraHeight(),
            0.0,
            pos.x - viewerPos.first + 0.5,
            pos.y - viewerPos.second + 0.5,
            pos.z - viewerPos.third + 0.5,
            color,
            thickness,
            phase
        )
    }

    fun cameraHeight(): Double {
        return if (mc.thePlayer.isSneaking) 1.54 else 1.62
    }


    fun drawLine3d(
        x: Double,
        y: Double,
        z: Double,
        x1: Double,
        y1: Double,
        z1: Double,
        color: Color,
        thickness: Float,
        phase: Boolean,
    ) {
        val tessellator = Tessellator.getInstance()
        val worldRenderer = tessellator.worldRenderer

        GlStateManager.pushMatrix()
        phaseCheck(phase)
        GlStateManager.disableTexture2D()
        GlStateManager.disableLighting()
        GlStateManager.disableCull()
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(770, 771)
        GL11.glLineWidth(thickness)
        color.bind()

        worldRenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION)
        worldRenderer.pos(x, y, z).endVertex()
        worldRenderer.pos(x1, y1, z1).endVertex()

        tessellator.draw()
        GlStateManager.enableTexture2D()
        GlStateManager.enableCull()
        GlStateManager.disableBlend()
        GlStateManager.enableLighting()
        resetPhase()
        GlStateManager.popMatrix()
    }


    fun drawLines3dAboveBlocks(
        points: List<BlockPos>,
        color: Color,
        thickness: Float,
        phase: Boolean,
        viewerPos: Triple<Double, Double, Double>
    ) {
        if (points.size < 2) return
        val tessellator = Tessellator.getInstance()
        val worldRenderer = tessellator.worldRenderer
        GlStateManager.pushMatrix()
        phaseCheck(phase)
        GlStateManager.disableTexture2D()
        GlStateManager.disableLighting()
        GlStateManager.disableCull()
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(770, 771)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glLineWidth(thickness)
        GL11.glEnable(GL11.GL_BLEND)



        color.bind()
        worldRenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION)
        var i = 0
        while (i < points.size - 1) {
            i++
            worldRenderer.pos(
                points[i - 1].x.toDouble() + 0.5 - viewerPos.first,
                points[i - 1].y.toDouble() + 1.01 - viewerPos.second,
                points[i - 1].z.toDouble() + 0.5 - viewerPos.third
            ).endVertex()
            worldRenderer.pos(
                points[i].x.toDouble() + 0.5 - viewerPos.first,
                points[i].y.toDouble() + 1.01 - viewerPos.second,
                points[i].z.toDouble() + 0.5 - viewerPos.third
            ).endVertex()
        }
        tessellator.draw()
        GlStateManager.enableTexture2D()
        GlStateManager.enableCull()
        GlStateManager.disableBlend()
        GlStateManager.enableLighting()
        resetPhase()
        GlStateManager.popMatrix()
    }

    fun drawBox(
        pos: BlockPos,
        color: Color,
        thickness: Float,
        phase: Boolean,
        viewerPos: Triple<Double, Double, Double>
    ) {
        val tessellator = Tessellator.getInstance()
        val worldRenderer = tessellator.worldRenderer

        GlStateManager.pushMatrix()
        phaseCheck(phase)
        GlStateManager.disableTexture2D()
        GlStateManager.disableLighting()
        GlStateManager.disableCull()
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(770, 771)
        //GL11.glEnable(GL11.GL_LINE_STRIP)
        GL11.glLineWidth(thickness)
        color.bind()


        worldRenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION)
        addBoxVertices(worldRenderer, pos, viewerPos)

        tessellator.draw()

        GlStateManager.enableTexture2D()
        GlStateManager.enableCull()
        GlStateManager.disableBlend()
        GlStateManager.enableLighting()
        resetPhase()
        GlStateManager.popMatrix()
    }

    fun drawBox(
        pos: BlockPos,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float,
        thickness: Float,
        phase: Boolean,
        viewerPos: Triple<Double, Double, Double>
    ) {
        val tessellator = Tessellator.getInstance()
        val worldRenderer = tessellator.worldRenderer

        GlStateManager.pushMatrix()
        phaseCheck(phase)
        GlStateManager.disableTexture2D()
        GlStateManager.disableLighting()
        GlStateManager.disableCull()
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(770, 771)
        GL11.glEnable(GL11.GL_LINE_STRIP)
        GL11.glLineWidth(thickness)
        GL11.glEnable(GL11.GL_BLEND)
        GlStateManager.color(red, green, blue, alpha)

        worldRenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION)
        addBoxVertices(worldRenderer, pos, viewerPos)

        tessellator.draw()

        GlStateManager.enableTexture2D()
        GlStateManager.enableCull()
        GlStateManager.disableBlend()
        GlStateManager.enableLighting()
        resetPhase()
        GlStateManager.popMatrix()
    }

    private fun addBoxVertices(worldRenderer: WorldRenderer, pos: BlockPos, viewerPos: Triple<Double, Double, Double>) {
        val x = pos.x.toDouble() - viewerPos.first
        val y = pos.y.toDouble() - viewerPos.second
        val z = pos.z.toDouble() - viewerPos.third
        val x1 = x + 1
        val y1 = y + 1
        val z1 = z + 1
        worldRenderer.pos(x, y, z).endVertex()
        worldRenderer.pos(x, y, z1).endVertex()
        worldRenderer.pos(x1, y, z1).endVertex()
        worldRenderer.pos(x1, y, z).endVertex()
        worldRenderer.pos(x, y, z).endVertex()

        worldRenderer.pos(x, y1, z).endVertex()
        worldRenderer.pos(x, y1, z1).endVertex()
        worldRenderer.pos(x1, y1, z1).endVertex()
        worldRenderer.pos(x1, y1, z).endVertex()
        worldRenderer.pos(x, y1, z).endVertex()

        worldRenderer.pos(x, y1, z1).endVertex()
        worldRenderer.pos(x, y, z1).endVertex()
        worldRenderer.pos(x1, y, z1).endVertex()
        worldRenderer.pos(x1, y1, z1).endVertex()
        worldRenderer.pos(x1, y1, z).endVertex()
        worldRenderer.pos(x1, y, z).endVertex()
    }

    /**
     * Taken from NotEnoughUpdates under Creative Commons Attribution-NonCommercial 3.0
     * https://github.com/Moulberry/NotEnoughUpdates/blob/master/LICENSE
     * @author Moulberry
     * @author Mojang
     */

    fun renderBeaconBeam(x: Double, y: Double, z: Double, rgb: Int, alphaMultiplier: Float, partialTicks: Float) {
        val height = 300
        val bottomOffset = 0
        val topOffset = bottomOffset + height
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        mc.textureManager.bindTexture(beaconBeam)
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0f)
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0f)
        GlStateManager.disableLighting()
        GlStateManager.enableCull()
        GlStateManager.enableTexture2D()
        GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0)
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        val time = mc.theWorld.totalWorldTime + partialTicks.toDouble()
        val d1 = MathHelper.func_181162_h(
            -time * 0.2 - MathHelper.floor_double(-time * 0.1)
                .toDouble()
        )
        val r = (rgb shr 16 and 0xFF) / 255f
        val g = (rgb shr 8 and 0xFF) / 255f
        val b = (rgb and 0xFF) / 255f
        val d2 = time * 0.025 * -1.5
        val d4 = 0.5 + cos(d2 + 2.356194490192345) * 0.2
        val d5 = 0.5 + sin(d2 + 2.356194490192345) * 0.2
        val d6 = 0.5 + cos(d2 + Math.PI / 4.0) * 0.2
        val d7 = 0.5 + sin(d2 + Math.PI / 4.0) * 0.2
        val d8 = 0.5 + cos(d2 + 3.9269908169872414) * 0.2
        val d9 = 0.5 + sin(d2 + 3.9269908169872414) * 0.2
        val d10 = 0.5 + cos(d2 + 5.497787143782138) * 0.2
        val d11 = 0.5 + sin(d2 + 5.497787143782138) * 0.2
        val d14 = -1.0 + d1
        val d15 = height.toDouble() * 2.5 + d14
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR)
        worldrenderer.pos(x + d4, y + topOffset, z + d5).tex(1.0, d15).color(r, g, b, 1.0f * alphaMultiplier)
            .endVertex()
        worldrenderer.pos(x + d4, y + bottomOffset, z + d5).tex(1.0, d14).color(r, g, b, 1.0f).endVertex()
        worldrenderer.pos(x + d6, y + bottomOffset, z + d7).tex(0.0, d14).color(r, g, b, 1.0f).endVertex()
        worldrenderer.pos(x + d6, y + topOffset, z + d7).tex(0.0, d15).color(r, g, b, 1.0f * alphaMultiplier)
            .endVertex()
        worldrenderer.pos(x + d10, y + topOffset, z + d11).tex(1.0, d15).color(r, g, b, 1.0f * alphaMultiplier)
            .endVertex()
        worldrenderer.pos(x + d10, y + bottomOffset, z + d11).tex(1.0, d14).color(r, g, b, 1.0f).endVertex()
        worldrenderer.pos(x + d8, y + bottomOffset, z + d9).tex(0.0, d14).color(r, g, b, 1.0f).endVertex()
        worldrenderer.pos(x + d8, y + topOffset, z + d9).tex(0.0, d15).color(r, g, b, 1.0f * alphaMultiplier)
            .endVertex()
        worldrenderer.pos(x + d6, y + topOffset, z + d7).tex(1.0, d15).color(r, g, b, 1.0f * alphaMultiplier)
            .endVertex()
        worldrenderer.pos(x + d6, y + bottomOffset, z + d7).tex(1.0, d14).color(r, g, b, 1.0f).endVertex()
        worldrenderer.pos(x + d10, y + bottomOffset, z + d11).tex(0.0, d14).color(r, g, b, 1.0f).endVertex()
        worldrenderer.pos(x + d10, y + topOffset, z + d11).tex(0.0, d15).color(r, g, b, 1.0f * alphaMultiplier)
            .endVertex()
        worldrenderer.pos(x + d8, y + topOffset, z + d9).tex(1.0, d15).color(r, g, b, 1.0f * alphaMultiplier)
            .endVertex()
        worldrenderer.pos(x + d8, y + bottomOffset, z + d9).tex(1.0, d14).color(r, g, b, 1.0f).endVertex()
        worldrenderer.pos(x + d4, y + bottomOffset, z + d5).tex(0.0, d14).color(r, g, b, 1.0f).endVertex()
        worldrenderer.pos(x + d4, y + topOffset, z + d5).tex(0.0, d15).color(r, g, b, 1.0f * alphaMultiplier)
            .endVertex()
        tessellator.draw()
        GlStateManager.disableCull()
        val d12 = -1.0 + d1
        val d13 = height + d12
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR)
        worldrenderer.pos(x + 0.2, y + topOffset, z + 0.2).tex(1.0, d13).color(r, g, b, 0.25f * alphaMultiplier)
            .endVertex()
        worldrenderer.pos(x + 0.2, y + bottomOffset, z + 0.2).tex(1.0, d12).color(r, g, b, 0.25f).endVertex()
        worldrenderer.pos(x + 0.8, y + bottomOffset, z + 0.2).tex(0.0, d12).color(r, g, b, 0.25f).endVertex()
        worldrenderer.pos(x + 0.8, y + topOffset, z + 0.2).tex(0.0, d13).color(r, g, b, 0.25f * alphaMultiplier)
            .endVertex()
        worldrenderer.pos(x + 0.8, y + topOffset, z + 0.8).tex(1.0, d13).color(r, g, b, 0.25f * alphaMultiplier)
            .endVertex()
        worldrenderer.pos(x + 0.8, y + bottomOffset, z + 0.8).tex(1.0, d12).color(r, g, b, 0.25f).endVertex()
        worldrenderer.pos(x + 0.2, y + bottomOffset, z + 0.8).tex(0.0, d12).color(r, g, b, 0.25f).endVertex()
        worldrenderer.pos(x + 0.2, y + topOffset, z + 0.8).tex(0.0, d13).color(r, g, b, 0.25f * alphaMultiplier)
            .endVertex()
        worldrenderer.pos(x + 0.8, y + topOffset, z + 0.2).tex(1.0, d13).color(r, g, b, 0.25f * alphaMultiplier)
            .endVertex()
        worldrenderer.pos(x + 0.8, y + bottomOffset, z + 0.2).tex(1.0, d12).color(r, g, b, 0.25f).endVertex()
        worldrenderer.pos(x + 0.8, y + bottomOffset, z + 0.8).tex(0.0, d12).color(r, g, b, 0.25f).endVertex()
        worldrenderer.pos(x + 0.8, y + topOffset, z + 0.8).tex(0.0, d13).color(r, g, b, 0.25f * alphaMultiplier)
            .endVertex()
        worldrenderer.pos(x + 0.2, y + topOffset, z + 0.8).tex(1.0, d13).color(r, g, b, 0.25f * alphaMultiplier)
            .endVertex()
        worldrenderer.pos(x + 0.2, y + bottomOffset, z + 0.8).tex(1.0, d12).color(r, g, b, 0.25f).endVertex()
        worldrenderer.pos(x + 0.2, y + bottomOffset, z + 0.2).tex(0.0, d12).color(r, g, b, 0.25f).endVertex()
        worldrenderer.pos(x + 0.2, y + topOffset, z + 0.2).tex(0.0, d13).color(r, g, b, 0.25f * alphaMultiplier)
            .endVertex()
        tessellator.draw()
    }


}