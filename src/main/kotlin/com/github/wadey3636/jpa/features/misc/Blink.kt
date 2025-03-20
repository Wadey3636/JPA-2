package com.github.wadey3636.jpa.features.misc

import me.modcore.features.Category
import me.modcore.features.Module
import me.modcore.utils.render.Color
import me.modcore.utils.render.roundedRectangle
import me.modcore.utils.skyblock.devMessage
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.math.abs

object Blink : Module("Blink", description = "Blinks", category = Category.MISC) {
    private var markedTime: Long = 0


    override fun onEnable() {
        super.onEnable()
        markedTime = System.currentTimeMillis()
    }


    @SubscribeEvent
    fun onRender(event: RenderGameOverlayEvent.Post) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return
        val currentTime = System.currentTimeMillis()
        val blink = (currentTime - markedTime) * 4
        //devMessage(blink)
        if (blink >= 1000) {
            devMessage(1000 - blink)
            roundedRectangle(0, 0, 1000, 1000 - blink, Color.BLACK)
        } else {
            devMessage(blink)
            roundedRectangle(0, 0, 1000, blink, Color.BLACK)
        }


        //roundedRectangle(0, blink, 1000, 1000, Color.BLACK)
        if (System.currentTimeMillis() - markedTime >= 500) {
            markedTime = 0
            toggle()
        }

    }










}