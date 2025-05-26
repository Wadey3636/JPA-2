package com.github.wadey3636.jpa.features.dungeonfeatures


import com.github.wadey3636.jpa.events.impl.SecondEvent
import com.github.wadey3636.jpa.utils.RenderHelper.renderTitle
import com.github.wadey3636.jpa.events.impl.ChatPacketEvent
import com.github.wadey3636.jpa.features.Category
import com.github.wadey3636.jpa.features.Module
import com.github.wadey3636.jpa.features.settings.impl.ColorSetting
import com.github.wadey3636.jpa.features.settings.impl.NumberSetting
import com.github.wadey3636.jpa.features.settings.impl.StringSetting
import com.github.wadey3636.jpa.utils.render.Color
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


object MilestoneReminder : Module(
    name = "Milestone Reminder",
    category = Category.DUNGEONS,
    description = "Displays a reminder to get your milestones. (For skill issue)"
) {

    private var timeStamp = System.currentTimeMillis()
    private var renderReminder = false
    private val interval by NumberSetting(
        name = "Interval",
        description = "The interval between reminders in seconds",
        min = 5,
        max = 500,
        default = 60
    )
    private val scale by NumberSetting(
        name = "Scale",
        description = "The scale of the reminder",
        min = 0,
        max = 5,
        increment = 1,
        default = 1
    )
    private val reminderText by StringSetting(
        name = "Reminder Text",
        description = "The text that will be displayed as the reminder",
        default = "Get Milestones!"
    )
    private val mileStone3ReminderColor by ColorSetting(
        "Color",
        description = "The Color of the Reminder",
        default = Color.MAGENTA
    )


    @SubscribeEvent
    fun checker(event: ChatPacketEvent) {
        if (event.message == "[NPC] Mort: Here, I found this map when I first entered the dungeon.") {
            //UChat.chat("[JPA] reminder enabled.")
            renderReminder = true
            timeStamp = System.currentTimeMillis()

        }
        if (event.message.contains("Milestone ❸")) {
            renderReminder = false //UChat.chat("[JPA] Reminder False")
        }

    }

    @SubscribeEvent
    fun secondEvent(event: SecondEvent) {
        if (renderReminder && (interval * 1000) > System.currentTimeMillis() - timeStamp) {

            renderTitle(reminderText, scale, mileStone3ReminderColor.rgba, 3000L)
            renderReminder = false
        }
    }
}
