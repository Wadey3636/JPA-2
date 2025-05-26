package com.github.wadey3636.jpa.features.dungeonfeatures

import com.github.wadey3636.jpa.events.impl.P3StartEvent
import com.github.wadey3636.jpa.events.impl.ServerTickEvent
import com.github.wadey3636.jpa.features.Category
import com.github.wadey3636.jpa.features.Module
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

var p3StartTimerticks = 0f
var p3StartTimerText = ""

object P3StartTimer : Module(
    name = "P3 Start Timer",
    description = "Displays a timer for when p3 is about to start",
    category = Category.FLOOR7
) {

    @SubscribeEvent
    fun p3StartTimer(event: P3StartEvent) {
        p3StartTimerticks = 104f
    }


    @SubscribeEvent
    fun countDown(event: ServerTickEvent) {
        --p3StartTimerticks
        if (p3StartTimerticks <= 0f) {
            p3StartTimerText = ""; return
        }
        p3StartTimerText = if (p3StartTimerticks % 2 == 0f) {
            (p3StartTimerticks / 20).toString() + "0"
        } else {
            (p3StartTimerticks / 20).toString()
        }
    }

    @SubscribeEvent
    fun reset(event: WorldEvent.Load) {
        p3StartTimerticks = 0f
    }


}

