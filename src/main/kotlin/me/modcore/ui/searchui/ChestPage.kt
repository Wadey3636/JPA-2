package me.modcore.ui.searchui

import com.github.wadey3636.jpa.utils.ChestSize
import com.github.wadey3636.jpa.utils.GuiUtils.deformat
import com.github.wadey3636.jpa.utils.InventoryInfo
import com.github.wadey3636.jpa.utils.RenderHelper
import com.github.wadey3636.jpa.utils.scaledResolution
import gg.essential.universal.UGraphics
import me.modcore.Core.mc
import me.modcore.ui.clickgui.util.ColorUtil
import me.modcore.ui.util.MouseUtils.isAreaHovered
import me.modcore.utils.render.*
import me.modcore.utils.skyblock.devMessage
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11

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


    fun draw(): Triple<String, Float, Float>? {
        var hoveredItem: Triple<String, Float, Float>? = null
        drawChest(x, y + scrollOffset - 16, 2f, inventory.size)

        for (item in inventory.page) {
            val pos = RenderHelper.getSlotXY(item.index)
            val itemX = pos.x * 36 + x + 16
            val itemY = pos.z * 36 + y + scrollOffset + 20

            RenderHelper.renderItem(item.itemStack, itemX, itemY, 2f, 2f, 100f)

            if (isAreaHovered(itemX, itemY, 32f, 32f)) {
                hoveredItem = Triple(item.itemStack.displayName, itemX, itemY)
            }

        }
        GlStateManager.disableLighting()
        return hoveredItem
    }
    fun mouseClicked() {
        if (isAreaHovered(
                x, y + scrollOffset - 16, 176 * 2f,
                if (inventory.size == ChestSize.Double) 264f else 156f))
        {


        }
    }
}