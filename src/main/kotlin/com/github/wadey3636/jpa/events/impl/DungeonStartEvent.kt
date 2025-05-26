package com.github.wadey3636.jpa.events.impl

import net.minecraftforge.fml.common.eventhandler.Cancelable
import net.minecraftforge.fml.common.eventhandler.Event

@Cancelable
class DungeonStartEvent(Floor: String) : Event() {
    private var floor = Floor

    fun getFloor(): String {
        return floor
    }
}