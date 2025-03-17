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
class ElementSlider(val name: String, val min: Double, val max: Double, val unit: String, var entry: PlayerEntry, private val increment: Double, val option: Option, val x: Float, var y: Float, val w: Float, val h: Float) {
    private var listeningText = false
    private var listening: Boolean = false
    private val valueDouble: Double get() = if (option == Option.X) entry.entryX else if (option == Option.Y) entry.entryY else entry.entryZ
//55
    private val isHovered: Boolean
        get() = isAreaHovered(x, y + 21.5f, w - 15f, 33.5f)

    private val isHoveredBox: Boolean
        get() = isAreaHovered(x + w - TEXTOFFSET - 30 - getTextWidth(getDisplay(), 16f), y  + 5f, 16f + getTextWidth(getDisplay(), 16f), 21.5f)

    private val handler = HoverHandler(0, 150)
    private val colorAnim = ColorAnimation(100)

    /** Used to make slider smoother and not jittery (doesn't change value.) */
    private var sliderPercentage: Float = ((valueDouble - min) / (max - min)).toFloat().coerceAtMost(1f)

    private inline val color: Color
        get() = clickGUIColor.brighter(1 + handler.percent() / 200f)

    private fun setValue(value: Double){
        when (option) {
            Option.Z -> entry.entryZ = value
            Option.X -> entry.entryX = value
            Option.Y -> entry.entryY = value
        }
    }

    private fun getDisplay(): String {
        if (listeningText) {
            return listeningTextField.ifEmpty { " " }
        }
        return "${valueDouble}${unit}"
    }

    fun draw() {
        handler.handle(x, y + 21.5f, w - 15f, 33.5f)
        val textWidth = getTextWidth(getDisplay(), 16f)

        roundedRectangle(x + w - TEXTOFFSET - 30 - textWidth, y , 16f + textWidth, 26.5f, buttonColor, 4f, edgeSoftness = 1f)
        rectangleOutline(x + w - TEXTOFFSET - 30 - textWidth, y , 16f + textWidth, 26.5f, colorAnim.get(buttonColor.darkerIf(isHoveredBox, 0.8f), clickGUIColor.darkerIf(isHoveredBox, 0.8f), !listeningText), 4f, 3f)

        if (listening) {
            sliderPercentage = ((mouseX - (x + TEXTOFFSET)) / (w - 15f)).coerceIn(0f, 1f)
            val diff = max - min
            val newVal = min + ((mouseX - (x + TEXTOFFSET)) / (w - 15f)).coerceIn(0f, 1f) * diff
            setValue(newVal)
        }
        //roundedRectangle(x + w - 4, y, 2, h, clickGUIColor.brighter(1.6f), 0f, edgeSoftness = 0)

        text(name, x + TEXTOFFSET, y + 17.75f, textColor, 20f, FontRenderer.REGULAR)
        text(getDisplay(), x + w - TEXTOFFSET - 22, y + 15.75f, textColor.darkerIf(isHoveredBox), 16f, FontRenderer.REGULAR, TextAlign.Right)

        //draw slider
        roundedRectangle(x + TEXTOFFSET, y + 37f, w - 30f, 7f, sliderBGColor, 3f)
        roundedRectangle(x + TEXTOFFSET, y + 37f, sliderPercentage * (w - 30f), 7f, color, 3f)

    }

    fun mouseClicked(){
        if (isHoveredBox) {
            if (listeningText) {
                textUnlisten()
                return
            }
            listeningText = true
            listeningTextField = valueDouble.toString()
            return
        }
        if (listeningText) {
            textUnlisten()
            listeningText = false
            if (isHovered) {
                listening = true
            }
            return
        }
        if (isHovered) {
            listening = true
            return
        }
    }

    fun mouseReleased(state: Int) {
        listening = false
    }
    fun updateSlider() {
        sliderPercentage = ((valueDouble - min) / (max - min)).toFloat().coerceAtMost(1f)
    }


    private fun textUnlisten() {
        if (listeningTextField.isEmpty()) {
            setValue(min)
            sliderPercentage = ((valueDouble - min) / (max - min)).toFloat().coerceAtMost(1f)
            listeningText = false
            return
        }
        if (listeningTextField.last() == '.') listeningTextField.dropLast(1)
        setValue(
            try {
                listeningTextField.toDouble()
            } catch (e: NumberFormatException) {
                modMessage("Invalid number! Defaulting to previous value.")
                logger.error(listeningTextField, e)
                valueDouble
            }
        )
        sliderPercentage = ((valueDouble - min) / (max - min)).toFloat().coerceAtMost(1f)
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
        if (mouseButton == 0 && listeningText && !isHovered && !isHoveredBox) {
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
        if (isHovered) {
            val amount = when (keyCode) {
                Keyboard.KEY_RIGHT -> increment
                Keyboard.KEY_LEFT -> -increment
                else -> return false
            }
            setValue(amount + valueDouble)
            return true
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