package com.github.wadey3636.jpa.utils.skyblock

import com.github.wadey3636.jpa.Core.mc
import com.github.wadey3636.jpa.events.impl.PacketEvent
import com.github.wadey3636.jpa.utils.noControlCodes
import net.minecraft.network.play.server.S02PacketChat
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.math.floor

object SkyblockPlayer {
    /*
    in module there should be:
    health display current/Max
    health bar
    defense display
    mana display current/Max
    mana bar
    current speed
    current ehp
    current overflow mana
     */

    private val HEALTH_REGEX = Regex("([\\d|,]+)/([\\d|,]+)❤")
    private val MANA_REGEX = Regex("([\\d|,]+)/([\\d|,]+)✎")
    private val OVERFLOW_MANA_REGEX = Regex("([\\d|,]+)ʬ")
    private val DEFENSE_REGEX = Regex("([\\d|,]+)❈ Defense")

    val currentHealth: Int
        get() = (mc.thePlayer?.let { player -> (maxHealth * player.health / player.maxHealth).toInt() } ?: 0)
    var maxHealth: Int = 0
    var currentMana: Int = 0
    var maxMana: Int = 0
    var currentSpeed: Int = 0
    var currentDefense: Int = 0
    var overflowMana: Int = 0
    var effectiveHP: Int = 0

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onPacket(event: PacketEvent.Receive) {
        if (event.packet !is S02PacketChat || event.packet.type != 2.toByte()) return
        val msg = event.packet.chatComponent.unformattedText.noControlCodes

        HEALTH_REGEX.find(msg)?.destructured?.let { (currentHp, maxHp) ->
            maxHealth = maxHp.replace(",", "").toIntOrNull() ?: maxHealth
        }

        MANA_REGEX.find(msg)?.destructured?.let { (cMana, mMana) ->
            currentMana = cMana.replace(",", "").toIntOrNull() ?: currentMana
            maxMana = mMana.replace(",", "").toIntOrNull() ?: maxMana
        }

        OVERFLOW_MANA_REGEX.find(msg)?.groupValues?.get(1)?.let {
            overflowMana = it.replace(",", "").toIntOrNull() ?: overflowMana
        }

        DEFENSE_REGEX.find(msg)?.groupValues?.get(1)?.let {
            currentDefense = it.replace(",", "").toIntOrNull() ?: currentDefense
        }

        effectiveHP = (currentHealth * (1 + currentDefense / 100))
        currentSpeed = floor((mc.thePlayer?.capabilities?.walkSpeed ?: 0f) * 1000f).toInt()
    }
}