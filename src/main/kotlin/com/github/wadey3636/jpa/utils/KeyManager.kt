package com.github.wadey3636.jpa.utils

import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.input.Keyboard

object KeyManager {
    val forcedKeys = mutableSetOf<Int>()

    fun addForcedKey(key: Int) {
        forcedKeys.add(key)
    }

    fun removeForcedKey(key: Int) {
        forcedKeys.remove(key)
    }

    fun clearForcedKeys() {
        forcedKeys.clear()
    }

    fun isForcedKey(key: Int): Boolean {
        return forcedKeys.contains(key)
    }
    @SubscribeEvent
    fun onKeyInput(event: net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent) {
        val keycode = Keyboard.getEventKey()
        if (forcedKeys.contains(keycode)) {
            KeyBinding.setKeyBindState(keycode, true)
        }
    }
}