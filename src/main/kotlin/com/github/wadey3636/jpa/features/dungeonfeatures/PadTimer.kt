package com.github.wadey3636.jpa.features.dungeonfeatures


import com.github.wadey3636.jpa.events.ServerTickEvent
import com.github.wadey3636.jpa.utils.RenderHelper
import me.modcore.events.impl.ChatPacketEvent
import me.modcore.features.Category
import me.modcore.features.Module
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


var purpleTicks = 0
var stormActivated = false
var padticks = 20f
var padcolor = RenderHelper.argbToInt(255, (255 - padticks * 12.75).toInt(), (0 + padticks * 12.75).toInt(), 0)
    //PolyColor(

object PadTimer : Module(
    name = "Pad Ticks",
    description = "A timer for the crusher cycles",
    category = Category.FLOOR7
) {


    @SubscribeEvent
    fun reset(event: WorldEvent.Load) {
        stormActivated = false
    }

    @SubscribeEvent
    fun stormPhaseStart(event: ChatPacketEvent) {
        if (event.message == "[BOSS] Storm: Pathetic Maxor, just like expected.") {
            padticks = 20f
            purpleTicks = 650
            stormActivated = true
        } else if (event.message == "[BOSS] Storm: I should have known that I stood no chance.") {
            stormActivated = false
        }
    }

    @SubscribeEvent
    fun tickTimer(event: ServerTickEvent) {
        if (stormActivated) {
            if (padticks > 1) {
                --padticks
            } else {
                padticks = 20f
            }

            if (purpleTicks > 0) --purpleTicks

            padcolor = RenderHelper.argbToInt(255, (255 - padticks * 12.75).toInt(), (0 + padticks * 12.75).toInt(), 0)
        }
    }


}