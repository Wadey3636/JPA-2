package me.modcore.ui.searchui

import com.github.wadey3636.jpa.utils.InventoryInfo
import com.github.wadey3636.jpa.utils.RenderHelper
import com.github.wadey3636.jpa.utils.scaledResolution
import me.modcore.utils.render.drawChest

class ChestPage(
    val inventory: InventoryInfo,
    val offset: Float,
    val yOffset: Float
) {

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
    }


    fun draw() {
        drawChest(x, y, 2f)
        for (item in inventory.page) {
            val pos = RenderHelper.getSlotXY(item.index)
            val itemX = pos.x * 36 + x + 16
            val itemY = pos.z * 36 + y + scrollOffset + 16
            RenderHelper.renderItem(item.itemStack, itemX, itemY, 2f, 2f, 100f)
        }
    }
}