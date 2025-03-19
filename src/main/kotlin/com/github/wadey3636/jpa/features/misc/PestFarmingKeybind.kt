package com.github.wadey3636.jpa.features.misc


import com.github.wadey3636.jpa.utils.WorldUtils.isBlock
import com.github.wadey3636.jpa.utils.location.Island
import com.github.wadey3636.jpa.utils.location.LocationUtils
import me.modcore.features.Category
import me.modcore.features.Module
import me.modcore.ui.clickgui.ClickGUI
import me.modcore.utils.skyblock.modMessage
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos

object PestFarmingKeybind : Module(
    name = "Pest Keybind",
    description = "Sets your spawn if you are inside sugar cane. Warps you to your garden otherwise.",
    category = Category.MISC
) {

    override fun onEnable() {
        if (mc.currentScreen == ClickGUI) {
            modMessage("Trigger with keybind")
        }
        toggle()
    }

    override fun onKeybind() {
        pestFarmingKeybind()
    }

    private fun pestFarmingKeybind() {
        if (LocationUtils.currentArea != Island.Garden || !enabled) return

        if (
            isBlock(
                BlockPos(mc.thePlayer.position.x, mc.thePlayer.position.y, mc.thePlayer.position.z),
                Blocks.reeds
            ) ||
            isBlock(
                BlockPos(mc.thePlayer.position.x - 1, mc.thePlayer.position.y, mc.thePlayer.position.z),
                Blocks.reeds
            ) ||
            isBlock(
                BlockPos(mc.thePlayer.position.x, mc.thePlayer.position.y, mc.thePlayer.position.z - 1),
                Blocks.reeds
            ) ||
            isBlock(
                BlockPos(mc.thePlayer.position.x - 1, mc.thePlayer.position.y, mc.thePlayer.position.z - 1),
                Blocks.reeds
            )
        ) {
            mc.thePlayer?.sendChatMessage("/setspawn")
        } else {
            mc.thePlayer?.sendChatMessage("/warp garden")
        }


    }

}

