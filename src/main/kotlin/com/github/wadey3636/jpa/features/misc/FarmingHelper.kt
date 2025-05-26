package com.github.wadey3636.jpa.features.misc

import com.github.wadey3636.jpa.events.impl.InputEvent
import com.github.wadey3636.jpa.features.Category
import com.github.wadey3636.jpa.features.Module
import com.github.wadey3636.jpa.features.settings.impl.BooleanSetting
import com.github.wadey3636.jpa.utils.KeyManager
import com.github.wadey3636.jpa.utils.Scheduler
import com.github.wadey3636.jpa.utils.skyblock.devMessage
import net.minecraft.client.settings.KeyBinding
import net.minecraft.util.MouseHelper
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard
import kotlin.random.Random
import kotlin.random.Random.Default.nextInt

object FarmingHelper : Module("Farming Helper", description = "Keeps keys pressed down", category = Category.MISC) {
    val holdW by BooleanSetting("Hold Forward", default = false, description = "Holds Forward key down")
    val holdS by BooleanSetting("Hold Backward", default = false, description = "Holds Backward key down")
    val holdAttack by BooleanSetting("Hold Attack", default = false, description = "Holds Attack key down")


    enum class Key {
        A, D,
    }
    var keyPressed: Key? = null
    val keyBindings = listOf(
        mc.gameSettings.keyBindForward,
        mc.gameSettings.keyBindLeft,
        mc.gameSettings.keyBindRight,
        mc.gameSettings.keyBindBack
    )
    fun unPressKey(keycode: Int) {
        Keyboard.enableRepeatEvents(false)
        KeyBinding.setKeyBindState(keycode, false)
        KeyManager.removeForcedKey(keycode)
    }
    fun pressKey(keycode: Int) {
        KeyBinding.setKeyBindState(keycode, true)
        KeyManager.addForcedKey(keycode)
    }

    @SubscribeEvent
    fun onKeyPressed(event: InputEvent.Keyboard){
        when (event.keycode) {
            mc.gameSettings.keyBindLeft.keyCode -> {
                unPressKey(mc.gameSettings.keyBindRight.keyCode)
                pressKey(mc.gameSettings.keyBindLeft.keyCode)
                keyPressed = Key.A
            }
            mc.gameSettings.keyBindRight.keyCode -> {
                unPressKey(mc.gameSettings.keyBindLeft.keyCode)
                pressKey(mc.gameSettings.keyBindRight.keyCode)
                keyPressed = Key.D
            }
        }
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.END) return
        if (holdW) {
            pressKey(mc.gameSettings.keyBindForward.keyCode)
        }
        if (holdS) {
            pressKey(mc.gameSettings.keyBindBack.keyCode)
        }
        if (holdAttack) {
            pressKey(mc.gameSettings.keyBindAttack.keyCode)
        }
    }

    override fun onDisable() {
        keyBindings.forEach {
            val keycode = it.keyCode
            Scheduler.schedulePostTickTask(nextInt(0, 4)) {
                if (Keyboard.isKeyDown(keycode)) {
                    KeyManager.removeForcedKey(keycode)
                } else {
                    unPressKey(keycode)
                }
            }

        }
        Scheduler.schedulePostTickTask(nextInt(0, 4)) {
            KeyManager.removeForcedKey(mc.gameSettings.keyBindAttack.keyCode)
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.keyCode, false)
            mc.playerController.resetBlockRemoving()
        }
        keyPressed = null
        super.onDisable()
    }

    override fun onEnable() {
        super.onEnable()
        keyPressed = null
    }

}