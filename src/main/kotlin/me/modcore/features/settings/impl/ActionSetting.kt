package me.modcore.features.settings.impl

import me.modcore.features.settings.Setting

/**
 * Setting that gets ran when clicked.
 *
 * @author Aton
 */
class ActionSetting(
    name: String,
    hidden: Boolean = false,
    description: String,
    override val default: () -> Unit = {}
) : Setting<() -> Unit>(name, hidden, description) {

    override var value: () -> Unit = default

    var action: () -> Unit by this::value
}