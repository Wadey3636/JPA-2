package me.modcore.ui.playerCustomizerGUI

import com.github.wadey3636.jpa.features.render.PlayerEntry
import me.modcore.Core.logger
import me.modcore.features.settings.impl.NumberSetting
import me.modcore.font.FontRenderer
import me.modcore.ui.clickgui.ClickGUI.TEXTOFFSET
import me.modcore.ui.clickgui.animations.impl.ColorAnimation
import me.modcore.ui.clickgui.elements.Element
import me.modcore.ui.clickgui.elements.ElementType
import me.modcore.ui.clickgui.elements.ModuleButton
import me.modcore.ui.clickgui.util.ColorUtil.brighter
import me.modcore.ui.clickgui.util.ColorUtil.buttonColor
import me.modcore.ui.clickgui.util.ColorUtil.clickGUIColor
import me.modcore.ui.clickgui.util.ColorUtil.darkerIf
import me.modcore.ui.clickgui.util.ColorUtil.elementBackground
import me.modcore.ui.clickgui.util.ColorUtil.textColor
import me.modcore.ui.clickgui.util.HoverHandler
import me.modcore.ui.util.MouseUtils.isAreaHovered
import me.modcore.ui.util.MouseUtils.mouseX
import me.modcore.utils.floor
import me.modcore.utils.render.*
import me.modcore.utils.skyblock.devMessage
import me.modcore.utils.skyblock.modMessage
import org.lwjgl.input.Keyboard
import kotlin.math.roundToInt

/**
 * Renders all the modules.
 *
 * Backend made by Aton, with some changes
 * Design mostly made by Stivais
 *
 * @author Stivais, Aton
 * @see [Element]
 */
class ElementTextBox(val name: String, var entry: PlayerEntry, val x: Float, var y: Float, val w: Float, val h: Float) {
    private var listeningText = false

    private val isHoveredBox: Boolean
        get() = isAreaHovered(x + w - TEXTOFFSET - 30 - getTextWidth(getDisplay(), 12f), y  + 5f, 16f + getTextWidth(getDisplay(), 12f), 21.5f)

    private val colorAnim = ColorAnimation(100)

    private fun getDisplay(): String {
        if (listeningText) {
            return listeningTextField.ifEmpty { " " }
        }
        return entry.name
    }

    fun draw() {
        val textWidth = getTextWidth(getDisplay(), 12f)

        roundedRectangle(x + w - TEXTOFFSET - 30 - textWidth, y  + 5f, 16f + textWidth, 21.5f, buttonColor, 4f, edgeSoftness = 1f)
        rectangleOutline(x + w - TEXTOFFSET - 30 - textWidth, y  + 5f, 16f + textWidth, 21.5f, colorAnim.get(buttonColor.darkerIf(isHoveredBox, 0.8f), clickGUIColor.darkerIf(isHoveredBox, 0.8f), !listeningText), 4f, 3f)
        text(name, x - 22, y + 22, textColor, 20f, FontRenderer.REGULAR)
        text(getDisplay(), x + w - TEXTOFFSET - 22, y + 17.75f + 22 , textColor.darkerIf(isHoveredBox), 12f, FontRenderer.REGULAR, TextAlign.Right)


    }

    fun mouseClicked(){
        if (isHoveredBox) {
            if (listeningText) {
                textUnlisten()
                return
            }
            listeningText = true
            listeningTextField = entry.name
            return
        }
        if (listeningText) {
            textUnlisten()
            listeningText = false
            return
        }
    }



    private fun textUnlisten() {
        if (listeningTextField.isEmpty()) {
            entry.name = ""
            listeningText = false
            return
        }
        entry.name = listeningTextField
        listeningText = false
    }

    private fun handleText(number: String): String {
        if (number.isEmpty()) return ""
        if (number.last() == '.' && number.count { it == '.' } >= 2) {
            return number.dropLast(1)
        }
        return number
    }
    private var listeningTextField: String = ""

    fun mouseClickedAnywhere(mouseButton: Int): Boolean {
        if (mouseButton == 0 && listeningText && !isHoveredBox) {
            textUnlisten()
            return true
        }
        return false
    }



    fun keyTyped(typedChar: Char, keyCode: Int): Boolean {
        if (listeningText) {
            var text = listeningTextField
            when (keyCode) {
                Keyboard.KEY_ESCAPE, Keyboard.KEY_NUMPADENTER, Keyboard.KEY_RETURN -> {
                    textUnlisten()
                    return true
                }
                Keyboard.KEY_PERIOD -> {
                    if (listeningTextField.contains('.')) return true
                    listeningTextField += '.'
                    return true
                }
                Keyboard.KEY_DELETE -> {
                    listeningTextField = handleText(text.dropLast(1))
                    return true
                }

                Keyboard.KEY_BACK -> {
                    listeningTextField = if (Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                        ""
                    } else {
                        handleText(text.dropLast(1))
                    }
                    return true
                }
                in keyWhiteList -> {
                    text += typedChar.toString()
                    listeningTextField = handleText(text)
                    return true
                }
            }
        }
        return false
    }

    private companion object {
        val sliderBGColor = Color(-0xefeff0)
    }
    private val keyWhiteList = listOf(
        Keyboard.KEY_0,
        Keyboard.KEY_1,
        Keyboard.KEY_2,
        Keyboard.KEY_3,
        Keyboard.KEY_4,
        Keyboard.KEY_5,
        Keyboard.KEY_6,
        Keyboard.KEY_7,
        Keyboard.KEY_8,
        Keyboard.KEY_9,
        Keyboard.KEY_NUMPAD0,
        Keyboard.KEY_NUMPAD1,
        Keyboard.KEY_NUMPAD2,
        Keyboard.KEY_NUMPAD3,
        Keyboard.KEY_NUMPAD4,
        Keyboard.KEY_NUMPAD5,
        Keyboard.KEY_NUMPAD6,
        Keyboard.KEY_NUMPAD7,
        Keyboard.KEY_NUMPAD8,
        Keyboard.KEY_NUMPAD9
    )
}