package me.modcore.ui.searchui

import com.github.wadey3636.jpa.utils.*
import me.modcore.Core.mc
import me.modcore.utils.Vec2
import me.modcore.utils.render.RenderUtils.loadBufferedImage
import me.modcore.utils.render.drawDynamicTexture
import me.modcore.utils.render.drawTexture
import me.modcore.utils.render.scale
import me.modcore.utils.skyblock.devMessage
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import kotlin.math.roundToInt

class ChestPage(
    val inventory: InventoryInfo,
    val offset: Float,
    val yOffset: Float
) {
    private val chestTexture = ResourceLocation("textures/gui/container/chest.png")
    private var x = offset
    private val y = yOffset * 250

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
        devMessage("x:$x, y:$y")
    }





    fun draw() {
        drawTexture(
            chestTexture,
            x,
            y + scrollOffset,
            352f,
            if (inventory.size.int == 2 ) 244f else 138
        )
        for (item in inventory.page) {
            val pos = RenderHelper.getSlotXY(item.index)
            val itemX = pos.x * 36 + x + 16
            val itemY = pos.z * 36 + y + scrollOffset + 16
            RenderHelper.renderItem(item.itemStack, itemX, itemY, 2f, 2f, 100f)
        }
    }


}