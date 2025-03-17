package com.github.wadey3636.jpa.features.dungeonfeatures


import com.github.wadey3636.jpa.events.ServerTickEvent
import me.modcore.events.impl.ChatPacketEvent
import me.modcore.features.Category
import me.modcore.features.Module
import me.modcore.features.settings.impl.HudSetting
import me.modcore.ui.hud.HudElement
import me.modcore.utils.render.Color
import me.modcore.utils.render.mcText
import me.modcore.utils.render.text
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


var purpleTicks = 0
var stormActivated = false
var padticks = 20f
var padcolor = Color(0, (255 - padticks * 12.75).toInt(), (0 + padticks * 12.75).toInt(), 1f)
    //PolyColor(

object PadTimer : Module(
    name = "Pad Ticks",
    description = "A timer for the crusher cycles",
    category = Category.FLOOR7
) {

    private val padTickHud: HudElement by HudSetting("Pad Tick Hud", 10f, 10f, 1f, true) {
        if (!stormActivated) return@HudSetting 0f to 0f
        text(if (padticks % 2 != 1f) (padticks / 20).toString() + "0" else (padticks / 20).toString(), 0, 0, padcolor, 1f )
        40f to 10f

    }


    @SubscribeEvent
    fun reset(event: WorldEvent.Load) {

        stormActivated = false
    }

    @SubscribeEvent
    fun stormPhaseStart(event: ChatPacketEvent) {
        if (event.message == "[BOSS] Storm: Pathetic Maxor, just like expected.") {
            padticks = 20f
            purpleTicks = 650
            stormActivated = true
        } else if (event.message == "[BOSS] Storm: I should have known that I stood no chance.") {
            stormActivated = false
        }
    }

    @SubscribeEvent
    fun tickTimer(event: ServerTickEvent) {
        if (stormActivated) {
            if (padticks > 1) {
                --padticks
            } else {
                padticks = 20f
            }

            if (purpleTicks > 0) --purpleTicks

            padcolor = Color(0, (255 - padticks * 12.75).toInt(), (0 + padticks * 12.75).toInt(), 1f)
        }
    }


}