package me.modcore.ui.searchui

import com.github.wadey3636.jpa.utils.InventoryInfo
import com.github.wadey3636.jpa.utils.RenderHelper
import com.github.wadey3636.jpa.utils.Slot
import com.github.wadey3636.jpa.utils.scaledResolution
import me.modcore.Core.mc
import me.modcore.utils.Vec2
import me.modcore.utils.render.RenderUtils.loadBufferedImage
import me.modcore.utils.render.drawDynamicTexture
import me.modcore.utils.render.scale
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.DynamicTexture
import kotlin.math.roundToInt

class ChestPage(
    val inventory: InventoryInfo,
    val offset: Float,
    val yOffset: Float
) {
    private val inventoryTexture = DynamicTexture(loadBufferedImage("/assets/modcore/searchgui/chest.png"))
    private var x = offset
    private val y = yOffset.toInt() * 250

    init {
        when (offset) {
            1f -> {
                x = scaledResolution.scaledWidth / 3f
            }
            2f -> {
                x = scaledResolution.scaledWidth / 3f * 2.5f
            }
            3f -> {
                x = scaledResolution.scaledWidth / 3f * 4f
            }
        }
    }





    fun draw() {
        drawDynamicTexture(inventoryTexture, x, y + scrollOffset, 352f, 244f)
        for (item in inventory.page) {
            val pos = RenderHelper.getSlotXY(item.index)
            val itemX = pos.x * 32 + x.roundToInt()
            val itemY = pos.z * 32 + y + scrollOffset
            scale(2, 2, 200f)
            RenderHelper.renderItem(item.itemStack, itemX, itemY, 2f, 2f, 100f)
        }
    }


}