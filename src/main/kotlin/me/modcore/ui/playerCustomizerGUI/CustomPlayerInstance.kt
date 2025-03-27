package me.modcore.ui.playerCustomizerGUI


import com.github.wadey3636.jpa.features.render.PlayerEntry
import com.github.wadey3636.jpa.features.render.playerEntries
import com.mojang.authlib.GameProfile
import me.modcore.features.impl.render.ClickGUIModule
import me.modcore.font.FontRenderer
import me.modcore.ui.clickgui.animations.impl.ColorAnimation
import me.modcore.ui.clickgui.animations.impl.LinearAnimation
import me.modcore.ui.clickgui.util.ColorUtil.brighter
import me.modcore.ui.clickgui.util.ColorUtil.brighterIf
import me.modcore.ui.clickgui.util.ColorUtil.buttonColor
import me.modcore.ui.clickgui.util.ColorUtil.clickGUIColor
import me.modcore.ui.clickgui.util.ColorUtil.darker
import me.modcore.ui.clickgui.util.ColorUtil.darkerIf
import me.modcore.ui.clickgui.util.ColorUtil.elementBackground
import me.modcore.ui.clickgui.util.ColorUtil.textColor
import me.modcore.ui.playerCustomizerGUI.PlayerCustomizerGUI.customPlayerInstances
import me.modcore.ui.playerCustomizerGUI.PlayerCustomizerGUI.mc
import me.modcore.ui.util.MouseUtils.isAreaHovered
import me.modcore.utils.render.*
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.renderer.GlStateManager
import kotlin.math.roundToInt


//I cooked this up at 4 AM dont judge. IK this is fucked, but I can't be asked to improve it :)
//If someone for some ungodly reason is taking example from this mod. Ignore this entire GUI.
//Look at ClickGUI instead because this is an ungodly horror that I shat out after
//consuming an entire container of advil and snorting 2 lines of coke.
//Still better than whiteshdow's coding >:)


class CustomPlayerInstance(private val playerEntry: PlayerEntry) {
    private val sliders: List<ElementSlider> = listOf(
        ElementSlider("Scale X", 0.1, 10.0, "", playerEntry, 0.1, Option.X, 612.0f, 10f, 500f, 104f),
        ElementSlider("Scale Y", 0.1, 10.0, "", playerEntry, 0.1, Option.Y, 612.0f, 10f, 500f, 104f),
        ElementSlider("Scale Z", 0.1, 10.0, "", playerEntry, 0.1, Option.Z, 612.0f, 10f, 500f, 104f)
    )


    private val boxVerticalOffset: Float = 10f
    private val switchVerticalOffset: Float = 8f


    private val colorAnimations: List<ColorAnimation> = listOf(
        ColorAnimation(250),
        ColorAnimation(250),
        ColorAnimation(250),
        ColorAnimation(250),
        ColorAnimation(250),
        ColorAnimation(250),
        ColorAnimation(250)
    )
    private val animations: List<LinearAnimation<Float>> = listOf(
        LinearAnimation<Float>(200),
        LinearAnimation<Float>(200),
        LinearAnimation<Float>(200),
        LinearAnimation<Float>(200),
        LinearAnimation<Float>(200),
        LinearAnimation<Float>(200),
        LinearAnimation<Float>(200)
    )
    val x = 60f
    val width = 1050f
    val height = 460f
    private val toggleSwitchX = 350
    private val switchOffset = -25f
    private val switchHeight = 78f
    private val nameElement: ElementTextBox = ElementTextBox("Name", playerEntry, x + toggleSwitchX, 24f, 10f, 20f)
    private val textureElement: ElementTextureBox =
        ElementTextureBox("Texture", playerEntry, x + 612.0f - 25, 200f, 10f, 20f)
    private var playerScale = 120

    fun mouseClickedAnywhere(state: Int) {
        for (slider in sliders) {
            slider.mouseClickedAnywhere(state)
        }
        nameElement.mouseClickedAnywhere(state)
        textureElement.mouseClickedAnywhere(state)
    }

    fun mouseScrolled(amount: Int) {
        playerScale = ((if (amount > 0) 1.1f else 0.9f) * playerScale).roundToInt()
        playerScale.coerceAtLeast(1)

    }


    fun mouseClicked(y: Float) {
        for (slider in sliders) {
            slider.mouseClicked()
        }
        nameElement.mouseClicked()
        textureElement.mouseClicked()
        if (
            if (!ClickGUIModule.switchType) isAreaHovered(
                x + toggleSwitchX + switchOffset,
                y + 34 + switchHeight * 0.75f,
                42f,
                40f
            )
            else isAreaHovered(x + switchOffset + toggleSwitchX - 10, y + 38f + switchHeight * 0.75f, 78f, 40f)
        ) {
            if (colorAnimations[0].start()) {
                animations[0].start()
                playerEntry.toggle = !playerEntry.toggle
                return
            }

        }
        if (
            if (!ClickGUIModule.switchType) isAreaHovered(
                x + toggleSwitchX + switchOffset,
                y + 34 + switchHeight * 1.75f,
                42f,
                40f
            )
            else isAreaHovered(x + switchOffset + toggleSwitchX - 10, y + 38f + switchHeight * 1.75f, 78f, 40f)
        ) {
            if (colorAnimations[1].start()) {
                animations[1].start()
                playerEntry.hideHelmet = !playerEntry.hideHelmet
                return
            }

        }
        if (
            if (!ClickGUIModule.switchType) isAreaHovered(
                x + toggleSwitchX + switchOffset,
                y + 34 + switchHeight * 2.75f,
                42f,
                40f
            )
            else isAreaHovered(x + switchOffset + toggleSwitchX - 10, y + 38f + switchHeight * 2.75f, 78f, 40f)
        ) {
            if (colorAnimations[2].start()) {
                animations[2].start()
                playerEntry.hideChestplate = !playerEntry.hideChestplate
                return
            }

        }
        if (
            if (!ClickGUIModule.switchType) isAreaHovered(
                x + toggleSwitchX + switchOffset,
                y + 34 + switchHeight * 3.75f,
                42f,
                40f
            )
            else isAreaHovered(x + switchOffset + toggleSwitchX - 10, y + 38f + switchHeight * 3.75f, 78f, 40f)
        ) {
            if (colorAnimations[3].start()) {
                animations[3].start()
                playerEntry.hideLeggings = !playerEntry.hideLeggings
                return
            }

        }
        if (
            if (!ClickGUIModule.switchType) isAreaHovered(
                x + toggleSwitchX + switchOffset,
                y + 34 + switchHeight * 4.75f,
                42f,
                40f
            )
            else isAreaHovered(x + switchOffset + toggleSwitchX - 10, y + 38f + switchHeight * 4.75f, 78f, 40f)
        ) {
            if (colorAnimations[4].start()) {
                animations[4].start()
                playerEntry.hideBoots = !playerEntry.hideBoots
                return
            }

        }
        if (
            if (!ClickGUIModule.switchType) isAreaHovered(x + 832.0f, y + 238, 42f, 40f)
            else isAreaHovered(x + 832.0f - 10, y + 242, 78f, 40f)
        ) {
            if (colorAnimations[5].start()) {
                animations[5].start()
                playerEntry.toggleTexture = !playerEntry.toggleTexture
                return
            }

        }
        if (
            if (!ClickGUIModule.switchType) isAreaHovered(x + 832.0f, y + 276 + switchHeight, 42f, 40f)
            else isAreaHovered(x + 822.0f, y + 280 + switchHeight, 78f, 40f)
        ) {
            if (colorAnimations[6].start()) {
                animations[6].start()
                playerEntry.dinnerBone = !playerEntry.dinnerBone
                return
            }

        }



        if (isAreaHovered(x + width - 40, y + height - 40, 40f, 40f)) {
            playerEntries.remove(playerEntry)
            customPlayerInstances.remove(this)
        }
        if (isAreaHovered(x + 560, y + 325, 170f, 85f)) {
            playerEntry.entryX = 1.0
            playerEntry.entryY = 1.0
            playerEntry.entryZ = 1.0
            if (!playerEntry.toggle) {
                if (colorAnimations[0].start()) {
                    animations[0].start()
                    playerEntry.toggle = true
                }
            }
            if (playerEntry.toggleTexture) {
                if (colorAnimations[5].start()) {
                    animations[5].start()
                    playerEntry.toggleTexture = false
                }
            }
            if (playerEntry.dinnerBone) {
                if (colorAnimations[6].start()) {
                    animations[6].start()
                    playerEntry.dinnerBone = false
                }
            }
            if (playerEntry.hideBoots) {
                if (colorAnimations[4].start()) {
                    animations[4].start()
                    playerEntry.hideBoots = false
                }
            }
            if (playerEntry.hideHelmet) {
                if (colorAnimations[1].start()) {
                    animations[1].start()
                    playerEntry.hideHelmet = false
                }
            }
            if (playerEntry.hideLeggings) {
                if (colorAnimations[3].start()) {
                    animations[3].start()
                    playerEntry.hideLeggings = false
                }
            }
            if (playerEntry.hideChestplate) {
                if (colorAnimations[2].start()) {
                    animations[2].start()
                    playerEntry.hideChestplate = false
                }
            }
            playerEntry.texture = null
            playerEntry.profile = GameProfile(null, "Player")
            for (slider in sliders) {
                slider.updateSlider()
            }
            playerEntry.name = "Player"
            //refreshUI()
        }


    }


    fun draw(y: Float) {

        roundedRectangle(x, y, width, height, buttonColor, radius = 10)
        roundedRectangle(
            x,
            y,
            width * 0.3,
            height,
            elementBackground,
            elementBackground,
            elementBackground,
            0,
            10,
            0,
            10,
            0,
            0.5
        )
        roundedRectangle(x + width * 0.3, y, 2, height, clickGUIColor.brighter(1.3f), radius = 0)

        roundedRectangle(x + 560, y + 325, 170, 85, elementBackground, radius = 10)
        text(
            "Reset",
            x + 645,
            y + 367.5,
            textColor.darkerIf(isAreaHovered(x + 560, y + 325, 170f, 85f)),
            32f,
            align = TextAlign.Middle
        )

        roundedRectangle(
            x + width - 40,
            y + height - 40,
            40,
            40,
            Color.TRANSPARENT,
            clickGUIColor,
            Color.TRANSPARENT,
            3,
            5,
            0,
            0,
            10,
            0.5f
        )
        drawX(
            x + width - 19,
            y + height - 22,
            1.5f,
            textColor.darkerIf(isAreaHovered(x + width - 40, y + height - 40, 40f, 40f), 0.7f)
        )


        drawSwitch("Toggle", x + toggleSwitchX, y + 34 + switchHeight * 0.75f, 28f, enabled = playerEntry.toggle, 0)
        drawSwitch(
            "Hide Helmet",
            x + toggleSwitchX,
            y + 34 + switchHeight * 1.75f,
            28f,
            enabled = playerEntry.hideHelmet,
            1
        )
        drawSwitch(
            "Hide Chestplate",
            x + toggleSwitchX,
            y + 34 + switchHeight * 2.75f,
            28f,
            enabled = playerEntry.hideChestplate,
            2
        )
        drawSwitch(
            "Hide Leggings",
            x + toggleSwitchX,
            y + 34 + switchHeight * 3.75f,
            28f,
            enabled = playerEntry.hideLeggings,
            3
        )
        drawSwitch(
            "Hide Boots",
            x + toggleSwitchX,
            y + 34 + switchHeight * 4.75f,
            28f,
            enabled = playerEntry.hideBoots,
            4
        )
        drawSwitch("Toggle Texture", x + 832.0f, y + 242, 28f, enabled = playerEntry.toggleTexture, 5)
        drawSwitch("Dinner Bone", x + 832.0f, y + 280 + switchHeight, 28f, enabled = playerEntry.dinnerBone, 6)
        var y2 = 10
        for (slider in sliders) {
            slider.y = y + y2
            slider.draw()
            y2 += 72
        }
        nameElement.y = y
        nameElement.draw()
        textureElement.y = y + 220
        textureElement.draw()

        val playerEntity = EntityOtherPlayerMP(mc.theWorld, playerEntry.profile)
        val entityX = (x + width * 0.15).roundToInt()
        val entityY = (y + height * 0.95).roundToInt()

        val scissorBox = scissor(x, y, width * 0.3, height)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GuiInventory.drawEntityOnScreen(
            entityX,
            entityY,
            playerScale,
            0f,
            0f,
            playerEntity
        )
        resetScissor(scissorBox)


    }

    val scale = 1.6f
    private fun drawSwitch(name: String, x: Float, y: Float, h: Float, enabled: Boolean, index: Int) {
        text(name, x - 25, y + h / 2f - 24, textColor, 20f, FontRenderer.REGULAR)
        GlStateManager.pushMatrix()
        scale(scale, scale, 1f)
        val isHovered =
            if (!ClickGUIModule.switchType) isAreaHovered(
                (x + switchOffset) / scale,
                (y + boxVerticalOffset) / scale,
                21f,
                20f
            )
            else isAreaHovered((x + switchOffset) / scale, (y + switchVerticalOffset) / scale, 34f, 20f)


        val color =
            colorAnimations[index].get(
                clickGUIColor.darkerIf(isHovered, 0.7f),
                buttonColor.brighter(1.3f).brighterIf(isHovered, 1.3f),
                enabled
            )

        if (!ClickGUIModule.switchType) {
            //render check box
            dropShadow((x + switchOffset) / scale, (y + boxVerticalOffset) / scale, 21f, 20f, 10f, 0.75f)
            roundedRectangle((x + switchOffset) / scale, (y + boxVerticalOffset) / scale, 21f, 20f, color, 5f)
            rectangleOutline(
                (x + switchOffset) / scale,
                (y + boxVerticalOffset) / scale,
                21f,
                20f,
                clickGUIColor,
                5f,
                3f
            )
        } else {
            //render switch
            dropShadow((x + switchOffset) / scale, (y + switchVerticalOffset) / scale, 34f, 20f, 10f, 0.75f)
            roundedRectangle((x + switchOffset) / scale, (y + switchVerticalOffset) / scale, 34f, 20f, buttonColor, 9f)

            if (enabled || animations[index].isAnimating()) roundedRectangle(
                (x + switchOffset) / scale,
                (y + switchVerticalOffset) / scale,
                animations[index].get(34f, 9f, enabled),
                20f,
                color,
                9f
            )

            if (isHovered) rectangleOutline(
                (x + switchOffset) / scale,
                (y + switchVerticalOffset) / scale,
                34f,
                20f,
                color.darker(.85f),
                9f,
                3f
            )
            circle(
                (x + switchOffset + (42f * scale)) / scale - animations[index].get(33f, 17f, !enabled),
                (y + (14f * scale) + 2) / scale,
                6f,
                Color(220, 220, 220).darkerIf(isHovered, 0.9f)
            )
        }

        scale(1 / scale, 1 / scale, 1f)
        GlStateManager.popMatrix()
    }

    fun keyTyped(typedChar: Char, keyCode: Int) {
        for (slider in sliders) {
            slider.keyTyped(typedChar, keyCode)
        }
        nameElement.keyTyped(typedChar, keyCode)
        textureElement.keyTyped(typedChar, keyCode)
    }


    fun mouseReleased(state: Int) {
        for (slider in sliders) {
            slider.mouseReleased(state)
        }
    }


}

enum class Option {
    X,
    Y,
    Z
}