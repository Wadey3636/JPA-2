package me.modcore.ui.searchui

import com.github.wadey3636.jpa.utils.InventoryInfo
import com.github.wadey3636.jpa.utils.RenderHelper
import com.github.wadey3636.jpa.utils.Slot
import com.github.wadey3636.jpa.utils.scaledResolution
import me.modcore.utils.render.RenderUtils.loadBufferedImage
import me.modcore.utils.render.drawDynamicTexture
import net.minecraft.client.renderer.texture.DynamicTexture

class ChestPage(val inventory: InventoryInfo, val offset: Float, val y: Float) {
    private val inventoryTexture = DynamicTexture(loadBufferedImage("/assets/modcore/searchgui/chest.png"))
    private var x = 0f

    private val items = mutableListOf<Slot>()
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

    fun getSlotXY(index: Int): Pair<> {

    }



    fun draw() {

        var xOffset = 0f
        var yOffset = 0f
        drawDynamicTexture(inventoryTexture, x, 250 * y + scrollOffset, 352f, 244f)
        for (item in inventory.page) {
            RenderHelper.renderItem(item.itemStack, )
        }




    }


}