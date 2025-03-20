package com.github.wadey3636.jpa.features.misc

import me.modcore.features.Category
import me.modcore.features.Module
import me.modcore.utils.render.Color
import me.modcore.utils.render.roundedRectangle
import me.modcore.utils.render.scale
import me.modcore.utils.render.scaleFactor
import me.modcore.utils.skyblock.devMessage
import net.minecraft.client.gui.ScaledResolution
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
        scale(1f / scaleFactor, 1f / scaleFactor, 200f)
        val currentTime = System.currentTimeMillis()
        val blink = (currentTime - markedTime) * 1.5
        devMessage(blink)
        if (blink >= 750) {
            roundedRectangle(0, 0, 3000, 1250 - blink, Color.BLACK)
            roundedRectangle(0, mc.displayHeight + blink - 1250, 3000, 1300, Color.BLACK)
        } else if (blink >= 500) {
          roundedRectangle(0, 0, mc.displayWidth, mc.displayHeight, Color.BLACK)
        } else {
            roundedRectangle(0, 0, 3000, blink, Color.BLACK)
            roundedRectangle(0, mc.displayHeight - blink, 3000, 1300, Color.BLACK)
        }


        if (System.currentTimeMillis() - markedTime >= 850) {
            markedTime = 0
            toggle()
        }
        scale(scaleFactor, scaleFactor, 1 / 200f)
    }


    override fun onKeybind() {
        toggle()
    }








}