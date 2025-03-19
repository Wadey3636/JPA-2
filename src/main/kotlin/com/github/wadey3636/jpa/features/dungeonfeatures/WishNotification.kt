package com.github.wadey3636.jpa.features.dungeonfeatures


import com.github.wadey3636.jpa.utils.RenderHelper
import me.modcore.events.impl.ChatPacketEvent
import me.modcore.features.Category
import me.modcore.features.Module
import me.modcore.features.settings.impl.ColorSetting
import me.modcore.features.settings.impl.NumberSetting
import me.modcore.utils.render.Color
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