package me.modcore.ui.searchui

import com.github.wadey3636.jpa.features.misc.InventoryLogger.chestEntries
import com.github.wadey3636.jpa.utils.centerX
import com.github.wadey3636.jpa.utils.centerY
import com.github.wadey3636.jpa.utils.scaledResolution
import me.modcore.features.impl.render.ClickGUIModule
import me.modcore.ui.Screen
import me.modcore.utils.render.RenderUtils.loadBufferedImage
import me.modcore.utils.render.drawDynamicTexture
import me.modcore.utils.render.scale
import me.modcore.utils.render.scaleFactor
import me.modcore.utils.skyblock.devMessage
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.entity.RenderItem
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import kotlin.math.sign

var scrollOffset = 0
object SearchGui: Screen() {
    private val chests: MutableList<ChestPage> = mutableListOf()

    override fun draw() {
        GlStateManager.pushMatrix()
        scale(1f / scaleFactor, 1f / scaleFactor, 1f)
        chests.forEach { it.draw() }





/*
        drawDynamicTexture(inventoryTexture, scaledResolution.scaledWidth  / 3f, 200f, 352f, 244f)
        drawDynamicTexture(inventoryTexture, scaledResolution.scaledWidth / 3 * 2.5, 200f, 352f, 244f)
        drawDynamicTexture(inventoryTexture, scaledResolution.scaledWidth / 3 * 4, 200f, 352f, 244f)

 */

        if (chestEntries[0].page.isNotEmpty()) {
            renderItemStack(chestEntries[0].page[0].itemStack, 400, 400)
        }




        scale(scaleFactor, scaleFactor, 1f)
        GlStateManager.popMatrix()
    }


    override fun initGui() {
        if (OpenGlHelper.shadersSupported && mc.renderViewEntity is EntityPlayer && ClickGUIModule.blur) {
            mc.entityRenderer.stopUseShader()
            mc.entityRenderer.loadShader(ResourceLocation("shaders/post/blur.json"))
        }
        var y = 1f
        var x = 1f
        for (entry in chestEntries) {
            when (x) {
                1f -> {
                    chests.add(ChestPage(entry, x, y))
                    x++
                }
                2f -> {
                    chests.add(ChestPage(entry, x, y))
                    x++
                }
                3f -> {
                    chests.add(ChestPage(entry, x, y))
                    x = 1f
                    y++
                }
                else -> {
                    devMessage("Error Occurred Adding Chests")
                }
            }

        }
    }

    override fun onGuiClosed() {
        mc.entityRenderer.stopUseShader()
    }
    private fun renderItemStack(stack: ItemStack, x: Int, y: Int) {
        val itemRender = mc.renderItem
        val fontRenderer: FontRenderer = mc.fontRendererObj

        GlStateManager.pushMatrix()
        GlStateManager.enableRescaleNormal()
        RenderHelper.enableGUIStandardItemLighting()
        itemRender.renderItemAndEffectIntoGUI(stack, x, y)
        itemRender.renderItemOverlayIntoGUI(fontRenderer, stack, x, y, null)
        RenderHelper.disableStandardItemLighting()
        GlStateManager.disableRescaleNormal()
        GlStateManager.popMatrix()
    }


    override fun onScroll(amount: Int) {
        val actualAmount = amount.sign * 16
        scrollOffset += actualAmount
    }


}