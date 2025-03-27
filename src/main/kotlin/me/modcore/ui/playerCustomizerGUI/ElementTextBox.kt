package me.modcore.ui.playerCustomizerGUI

import com.github.wadey3636.jpa.features.render.PlayerEntry
import com.mojang.authlib.GameProfile
import me.modcore.font.FontRenderer
import me.modcore.ui.clickgui.ClickGUI.TEXTOFFSET
import me.modcore.ui.clickgui.animations.impl.ColorAnimation
import me.modcore.ui.clickgui.elements.Element
import me.modcore.ui.clickgui.util.ColorUtil.buttonColor
import me.modcore.ui.clickgui.util.ColorUtil.clickGUIColor
import me.modcore.ui.clickgui.util.ColorUtil.darkerIf
import me.modcore.ui.clickgui.util.ColorUtil.textColor
import me.modcore.ui.playerCustomizerGUI.PlayerCustomizerGUI.refreshUI
import me.modcore.ui.util.MouseUtils.isAreaHovered
import me.modcore.utils.render.*
import org.lwjgl.input.Keyboard

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
        get() = isAreaHovered(x + w - TEXTOFFSET - 28, y + 36f, 16f + getTextWidth(getDisplay(), 16f), 22.5f)

    private val colorAnim = ColorAnimation(100)

    private fun getDisplay(): String {
        if (listeningText) {
            return listeningTextField.ifEmpty { " " }
        }
        return entry.name
    }

    fun draw() {
        val textWidth = getTextWidth(getDisplay(), 16f)
        roundedRectangle(x + w - TEXTOFFSET - 28, y + 36f, 16f + textWidth, 22.5f, buttonColor, 4f, edgeSoftness = 1f)
        rectangleOutline(
            x + w - TEXTOFFSET - 28,
            y + 36f,
            16f + textWidth,
            22.5f,
            colorAnim.get(
                buttonColor.darkerIf(isHoveredBox, 0.8f),
                clickGUIColor.darkerIf(isHoveredBox, 0.8f),
                !listeningText
            ),
            4f,
            3f
        )
        text(name, x - 22, y + 20, textColor, 20f, FontRenderer.REGULAR)
        text(
            getDisplay(),
            x - 22,
            y + 17.75f + 22 + 10f,
            textColor.darkerIf(isHoveredBox),
            16f,
            FontRenderer.REGULAR,
            TextAlign.Left
        )
    }

    fun mouseClicked() {
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
            entry.profile = GameProfile(entry.profile?.id, "")
            entry.name = ""
            listeningText = false
            return
        }
        entry.name = listeningTextField
        entry.profile = GameProfile(entry.profile?.id, listeningTextField)
        listeningText = false
        refreshUI()

    }

    private fun handleText(number: String): String {
        if (number.isEmpty()) return ""
        if (number.last() == '.' && number.count { it == '.' } >= 2) {
            return number.dropLast(1)
        }
        return number
    }

    private var listeningTextField: String = ""

    fun mouseClickedAnywhere(mouseButton: Int) {
        if (mouseButton == 0 && listeningText && !isHoveredBox) {
            textUnlisten()
            return
        }

    }


    fun keyTyped(typedChar: Char, keyCode: Int) {
        if (listeningText) {
            var text = listeningTextField
            when (keyCode) {
                Keyboard.KEY_ESCAPE, Keyboard.KEY_NUMPADENTER, Keyboard.KEY_RETURN -> {
                    textUnlisten()
                    return
                }

                Keyboard.KEY_DELETE -> {
                    listeningTextField = handleText(text.dropLast(1))
                    return
                }

                Keyboard.KEY_BACK -> {
                    listeningTextField =
                        if (Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                            ""
                        } else {
                            handleText(text.dropLast(1))
                        }
                    return
                }

                in keyWhiteList -> {
                    if (listeningTextField.length >= 16) return
                    text += typedChar.toString()
                    listeningTextField = handleText(text)
                    return
                }
            }
        }
        return
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
        Keyboard.KEY_NUMPAD9,
        Keyboard.KEY_A,
        Keyboard.KEY_B,
        Keyboard.KEY_C,
        Keyboard.KEY_D,
        Keyboard.KEY_E,
        Keyboard.KEY_F,
        Keyboard.KEY_G,
        Keyboard.KEY_H,
        Keyboard.KEY_I,
        Keyboard.KEY_J,
        Keyboard.KEY_K,
        Keyboard.KEY_L,
        Keyboard.KEY_M,
        Keyboard.KEY_N,
        Keyboard.KEY_O,
        Keyboard.KEY_P,
        Keyboard.KEY_Q,
        Keyboard.KEY_R,
        Keyboard.KEY_S,
        Keyboard.KEY_T,
        Keyboard.KEY_U,
        Keyboard.KEY_V,
        Keyboard.KEY_W,
        Keyboard.KEY_X,
        Keyboard.KEY_Y,
        Keyboard.KEY_Z,
        Keyboard.KEY_UNDERLINE,
        Keyboard.KEY_MINUS


    )
}