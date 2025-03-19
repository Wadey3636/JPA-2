package com.github.wadey3636.jpa.utils


import com.github.wadey3636.jpa.utils.RenderHelper.drawCenteredText
import me.modcore.Core.mc
import me.modcore.utils.render.Color
import net.minecraft.client.gui.ScaledResolution
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


var title = ""
var size = 0f
var color1 = Color(0f, 0f, 0f, 0f).argb
var time = 0L
var timeStamp = System.currentTimeMillis()
val scaledResolution: ScaledResolution
    get() = ScaledResolution(mc)
var centerX = (scaledResolution.scaledWidth / 2f)
var centerY = (scaledResolution.scaledHeight / 2f)


object TitleRenderer {


    @SubscribeEvent
    fun worldLoadEvent(event: WorldEvent.Load) {
        centerX = (scaledResolution.scaledWidth / 2f)
        centerY = (scaledResolution.scaledHeight / 2f)
    }

    @SubscribeEvent
    fun titleRenderer(event: RenderGameOverlayEvent.Post) {
        if (System.currentTimeMillis() - timeStamp < time) drawCenteredText(title, size, color1, centerX, centerY)
    }


}