package me.modcore.ui.searchui

import com.github.wadey3636.jpa.features.misc.InventoryLogger.chestEntries
import me.modcore.features.impl.render.ClickGUIModule
import me.modcore.ui.Screen
import me.modcore.utils.render.scale
import me.modcore.utils.render.scaleFactor
import me.modcore.utils.skyblock.modError
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import kotlin.math.sign

var scrollOffset = 0

object SearchGui : Screen() {
    private val chests: MutableList<ChestPage> = mutableListOf()

    override fun draw() {
        GlStateManager.pushMatrix()
        scale(1f / scaleFactor, 1f / scaleFactor, 1f)
        for (chest in chests) {
            chest.draw()
        }
        //chests.forEach { it.draw() }
        scale(scaleFactor, scaleFactor, 1f)
        GlStateManager.popMatrix()
    }


    override fun initGui() {
        if (OpenGlHelper.shadersSupported && mc.renderViewEntity is EntityPlayer && ClickGUIModule.blur) {
            mc.entityRenderer.stopUseShader()
            mc.entityRenderer.loadShader(ResourceLocation("shaders/post/blur.json"))
        }
        var y = 0f
        var y1 = 0f
        var y2 = 0f
        var x = 1f
        chests.clear()
        for (entry in chestEntries) {
            val gap = if (entry.size.int == 2) 244f / 250f + 0.1f else 138f / 250f + 0.1f
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

    override fun onGuiClosed() {
        mc.entityRenderer.stopUseShader()
    }


    override fun onScroll(amount: Int) {
        val actualAmount = amount.sign * 16
        scrollOffset += actualAmount
    }


}