package me.modcore.ui.searchui

import com.github.wadey3636.jpa.utils.InventoryInfo
import com.github.wadey3636.jpa.utils.Slot
import com.github.wadey3636.jpa.utils.scaledResolution
import me.modcore.utils.render.RenderUtils.loadBufferedImage
import me.modcore.utils.render.drawDynamicTexture
import net.minecraft.client.renderer.texture.DynamicTexture

class ChestPage(val inventory: InventoryInfo, val x: Float, val y: Float, var offset: Float) {
    private val inventoryTexture = DynamicTexture(loadBufferedImage("/assets/modcore/searchgui/chest.png"))
    fun draw() {
        drawDynamicTexture(inventoryTexture,  x, y, 352f, 244f)




    }


}