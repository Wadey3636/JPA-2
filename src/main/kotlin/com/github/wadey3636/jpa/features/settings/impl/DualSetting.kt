package com.github.wadey3636.jpa.features.settings.impl

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.github.wadey3636.jpa.features.settings.Saving
import com.github.wadey3636.jpa.features.settings.Setting

/**
 * A setting that represents a boolean, with a different style.
 *
 * @author Aton, Bonsai
 */
class DualSetting(
    name: String,
    val left: String,
    val right: String,
    override val default: Boolean = false,
    hidden: Boolean = false,
    description: String,
) : Setting<Boolean>(name, hidden, description), Saving {

    override var value: Boolean = default

    var enabled: Boolean by this::value

    override fun write(): JsonElement {
        return JsonPrimitive(enabled)
    }

    override fun read(element: JsonElement?) {
        if (element?.asBoolean != enabled) {
            enabled = !enabled
        }
    }
}