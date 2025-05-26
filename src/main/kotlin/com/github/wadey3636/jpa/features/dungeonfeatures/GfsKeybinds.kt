package com.github.wadey3636.jpa.features.dungeonfeatures


import com.github.wadey3636.jpa.utils.GuiUtils.getInventory
import com.github.wadey3636.jpa.utils.GuiUtils.getItemCount
import com.github.wadey3636.jpa.features.Category
import com.github.wadey3636.jpa.features.Module
import com.github.wadey3636.jpa.features.settings.impl.KeybindSetting
import com.github.wadey3636.jpa.features.settings.impl.Keybinding
import org.lwjgl.input.Keyboard

object GfsKeybinds : Module(
    name = "GFS Keybinds",
    description = "Keybindings that extract items from your sack. Only draws what you need to complete a stack",
    category = Category.DUNGEONS
) {

    private val pearlKey by KeybindSetting(
        name = "Pearl GFS",
        default = Keybinding(Keyboard.KEY_NONE),
        description = "Gets pearls wow!"
    ).onPress { gfsPearl() }

    private val superboomKey by KeybindSetting(
        name = "Superboom GFS",
        default = Keybinding(Keyboard.KEY_NONE),
        description = "Gets superboom wow!"
    ).onPress { gfsSuperboom() }

    private val spiritLeapKey by KeybindSetting(
        name = "Spirit Leap GFS",
        default = Keybinding(Keyboard.KEY_NONE),
        description = "Gets Spirit Leaps wow!"
    ).onPress { gfsSpiritleap() }


    private fun gfsPearl() {
        if (!enabled) {
            return
        }
        val count = 16 - (getItemCount("ENDER_PEARL", getInventory()))
        if (count != 0) mc.thePlayer?.sendChatMessage("/gfs ender_pearl $count")
    }

    private fun gfsSuperboom() {
        if (!enabled) {
            return
        }
        val count = 64 - (getItemCount("SUPERBOOM_TNT", getInventory()))
        if (count != 0) mc.thePlayer?.sendChatMessage("/gfs SUPERBOOM_TNT $count")
    }

    private fun gfsSpiritleap() {
        if (!enabled) {
            return
        }
        val count = 16 - (getItemCount("SPIRIT_LEAP", getInventory()))
        if (count != 0) mc.thePlayer?.sendChatMessage("/gfs SPIRIT_LEAP $count")
    }
}

