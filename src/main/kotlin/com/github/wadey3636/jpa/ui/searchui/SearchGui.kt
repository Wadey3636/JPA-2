package com.github.wadey3636.jpa.ui.searchui

import com.github.wadey3636.jpa.features.misc.InventoryLogger.chestEntries
import com.github.wadey3636.jpa.utils.GuiUtils.deformat
import com.github.wadey3636.jpa.features.render.ClickGUIModule
import com.github.wadey3636.jpa.ui.Screen
import com.github.wadey3636.jpa.ui.clickgui.util.ColorUtil
import com.github.wadey3636.jpa.ui.clickgui.util.ColorUtil.withAlpha
import com.github.wadey3636.jpa.utils.render.*
import com.github.wadey3636.jpa.utils.skyblock.modError
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import kotlin.math.sign

var scrollOffset = 0

object SearchGui : Screen() {
    private val chests: MutableList<ChestPage> = mutableListOf()

    override fun draw() {
        var hoveredItem: Triple<String, Float, Float>? = null
        GlStateManager.pushMatrix()
        scale(1f / scaleFactor, 1f / scaleFactor, 1f)
        for (chest in chests) {
            val itemInfo = chest.draw()
            if (itemInfo != null) hoveredItem = itemInfo

        }

        if (hoveredItem != null) {
            GlStateManager.pushMatrix()
            translate(hoveredItem.second, hoveredItem.third, 1f)

            GlStateManager.color(1f, 1f, 1f, 1f)
            GlStateManager.disableDepth()
            GL11.glDepthFunc(GL11.GL_ALWAYS)
            roundedRectangle(0,0, 32, 32, ColorUtil.moduleButtonColor)
            roundedRectangle(40, 0, getTextWidth(hoveredItem.first.deformat, 12f) + 16f, getTextHeight(24f), ColorUtil.titlePanelColor.withAlpha(0.9f), radius = 3f)
            text(hoveredItem.first, 48, 0 + getTextHeight(12f), ColorUtil.textColor, 12f)
            GL11.glDepthFunc(GL11.GL_LEQUAL)
            GlStateManager.enableDepth()
            GlStateManager.popMatrix()
        }

        scale(scaleFactor, scaleFactor, 1f)
        GlStateManager.popMatrix()
    }


    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        chests.forEach { it.mouseClicked() }
    }

    fun createChests(){
        var y = 0.5f
        var y1 = 0.5f
        var y2 = 0.5f
        var x = 1f
        chests.clear()
        for (entry in chestEntries) {
            if (Searchbar.searchText != "" && !entry.page.any { it.itemStack.displayName.contains(Searchbar.searchText, true) }) {
                continue
            }
            val gap = if (entry.size.int == 2) 244f / 250f + 0.2f else 138f / 250f + 0.2f
            when (x) {
                1f -> {
                    chests.add(ChestPage(entry, x, y))
                    y += gap
                    x++
                }

                2f -> {
                    chests.add(ChestPage(entry, x, y1))
                    x++
                    y1 += gap
                }

                3f -> {
                    chests.add(ChestPage(entry, x, y2))
                    x = 1f
                    y2 += gap
                }

                else -> {
                    modError("Error Occurred Adding Chests")
                }
            }
        }
    }


    override fun initGui() {
        if (OpenGlHelper.shadersSupported && mc.renderViewEntity is EntityPlayer && ClickGUIModule.blur) {
            mc.entityRenderer.stopUseShader()
            mc.entityRenderer.loadShader(ResourceLocation("shaders/post/blur.json"))
        }
        createChests()

    }

    override fun onGuiClosed() {
        mc.entityRenderer.stopUseShader()
    }


    override fun onScroll(amount: Int) {
        val actualAmount = amount.sign * 16
        scrollOffset += actualAmount
    }


}