package com.github.wadey3636.jpa.events.impl

import net.minecraftforge.fml.common.eventhandler.Event

abstract class InputEvent(val keycode: Int) : Event() {
    class Keyboard(keycode: Int) : InputEvent(keycode)
    class Mouse(button: Int) : InputEvent(button)
}