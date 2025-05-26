package com.github.wadey3636.jpa.features.dungeonfeatures


import com.github.wadey3636.jpa.utils.RenderHelper
import com.github.wadey3636.jpa.events.impl.ChatPacketEvent
import com.github.wadey3636.jpa.features.Category
import com.github.wadey3636.jpa.features.Module
import com.github.wadey3636.jpa.features.settings.impl.ColorSetting
import com.github.wadey3636.jpa.features.settings.impl.NumberSetting
import com.github.wadey3636.jpa.utils.render.Color
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


object WishNotification : Module(
    name = "Wish Notification",
    description = "Displays a healer wish notification only while you are the healer class",
    category = Category.FLOOR7
) {
    private var color = 0
    private val wishNotificationSize by NumberSetting(
        name = "Scale",
        description = "The scale of the alert",
        min = 1,
        max = 5,
        increment = 1,
        default = 1
    )
    private val healerWishNotificationColor by ColorSetting(
        name = "Color",
        default = Color.PURPLE,
        description = "The color of the alert"
    )


    @SubscribeEvent
    fun onChat(event: ChatPacketEvent) {
        when (event.message) {
            "⚠ Maxor is enraged! ⚠" -> {
                RenderHelper.renderTitle(
                    "Wish",
                    wishNotificationSize,
                    healerWishNotificationColor.argb,
                    3000
                )
            }

            "[BOSS] Goldor: You have done it, you destroyed the factory…" -> {
                RenderHelper.renderTitle(
                    "Wish",
                    wishNotificationSize,
                    healerWishNotificationColor.argb,
                    3000
                )
            }
        }

    }


}