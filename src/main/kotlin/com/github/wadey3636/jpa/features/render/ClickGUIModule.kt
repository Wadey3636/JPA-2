package com.github.wadey3636.jpa.features.render

import com.github.wadey3636.jpa.config.Config
import com.github.wadey3636.jpa.features.Category
import com.github.wadey3636.jpa.features.Module
import com.github.wadey3636.jpa.Core
import com.github.wadey3636.jpa.features.settings.AlwaysActive
import com.github.wadey3636.jpa.features.settings.impl.ActionSetting
import com.github.wadey3636.jpa.features.settings.impl.BooleanSetting
import com.github.wadey3636.jpa.features.settings.impl.ColorSetting
import com.github.wadey3636.jpa.features.settings.impl.NumberSetting
import com.github.wadey3636.jpa.features.settings.impl.StringSetting
import com.github.wadey3636.jpa.ui.clickgui.ClickGUI
import com.github.wadey3636.jpa.ui.hud.EditHUDGui
import com.github.wadey3636.jpa.utils.render.Color
import org.lwjgl.input.Keyboard

@AlwaysActive
object ClickGUIModule : Module(
    name = "Click Gui",
    Keyboard.KEY_NONE,
    category = Category.RENDER,
    description = "Allows you to customize the GUI."
) {
    val blur by BooleanSetting(
        "Blur",
        true,
        description = "Toggles the background blur for the gui. Requires the menu to be reopened"
    )
    val enableNotification by BooleanSetting(
        "Enable notifications",
        true,
        description = "Shows you a notification in chat when you toggle an option with a keybind."
    )
    val color by ColorSetting(
        "Gui Color",
        Color(35, 213, 155),
        allowAlpha = false,
        description = "Color theme in the gui."
    )
    val devMode by BooleanSetting(
        "Dev Mode",
        default = false,
        description = "Activates dev mode. Very inconvenient for normal use"
    )

    val switchType by BooleanSetting("Switch Type", true, description = "Switches the type of the settings in the gui.")
    val forceHypixel by BooleanSetting(
        "Force Hypixel",
        false,
        description = "Forces the hypixel check to be on (not recommended)."
    )


    val action by ActionSetting(
        "Open Example Hud",
        description = "Opens an example hud to allow configuration of huds."
    ) {
        Core.display = EditHUDGui
    }

    private var joined by BooleanSetting("First join", false, hidden = true, "")
    var lastSeenVersion: String by StringSetting("Last seen version", "1.0.0", hidden = true, description = "")
    var firstTimeOnVersion = false

    val panelX = mutableMapOf<Category, NumberSetting<Float>>()
    val panelY = mutableMapOf<Category, NumberSetting<Float>>()
    val panelExtended = mutableMapOf<Category, BooleanSetting>()

    init {
        execute(250) {
            if (joined) destroyExecutor()
            joined = true
            Config.save()
        }
        resetPositions()
    }

    fun resetPositions() {
        Category.entries.forEach {
            val incr = 10f + 260f * it.ordinal
            panelX.getOrPut(it) {
                +NumberSetting(
                    it.name + ",x",
                    default = incr,
                    hidden = true,
                    description = ""
                )
            }.value = incr
            panelY.getOrPut(it) {
                +NumberSetting(
                    it.name + ",y",
                    default = 10f,
                    hidden = true,
                    description = ""
                )
            }.value = 10f
            panelExtended.getOrPut(it) {
                +BooleanSetting(
                    it.name + ",extended",
                    default = true,
                    hidden = true,
                    description = ""
                )
            }.enabled = true
        }
    }

    override fun onKeybind() {
        this.toggle()
    }

    override fun onEnable() {
        Core.display = ClickGUI
        super.onEnable()
        toggle()
    }
}