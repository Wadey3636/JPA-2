package com.github.wadey3636.jpa.events.impl

import net.minecraftforge.fml.common.eventhandler.Cancelable
import net.minecraftforge.fml.common.eventhandler.Event

abstract class ClickEvent : Event() {
    @Cancelable
    class Left : ClickEvent()

    @Cancelable
    class Right : ClickEvent()
}
