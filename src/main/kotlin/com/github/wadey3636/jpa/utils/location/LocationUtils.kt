package com.github.wadey3636.jpa.utils.location


import com.github.wadey3636.jpa.utils.dungeon.Dungeon
import com.github.wadey3636.jpa.utils.dungeon.DungeonUtils
import com.github.wadey3636.jpa.utils.dungeon.Floor
import me.modcore.Core.mc
import me.modcore.events.impl.PacketEvent
import me.modcore.features.impl.render.ClickGUIModule
import me.modcore.utils.cleanLine
import me.modcore.utils.cleanSB
import me.modcore.utils.clock.Executor
import me.modcore.utils.clock.Executor.Companion.register
import me.modcore.utils.sidebarLines
import me.modcore.utils.startsWithOneOf
import net.minecraft.network.play.server.S3FPacketCustomPayload
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent


object LocationUtils {
    var isOnHypixel: Boolean = false
        private set
    var isInSkyblock: Boolean = false
        private set
    var currentDungeon: Dungeon? = null
        private set
    var currentArea: Island = Island.Unknown
        private set
    var kuudraTier: Int = 0
        private set

    init {
        Executor(500, "LocationUtils") {
            if (!isInSkyblock)
                isInSkyblock = isOnHypixel && mc.theWorld?.scoreboard?.getObjectiveInDisplaySlot(1)
                    ?.let { cleanSB(it.displayName).contains("SKYBLOCK") } == true

            if (currentArea.isArea(Island.Unknown)) currentArea = getArea()

            if ((DungeonUtils.inDungeons || currentArea.isArea(Island.SinglePlayer)) && currentDungeon == null) currentDungeon =
                Dungeon(getFloor() ?: return@Executor)



            if (currentArea.isArea(Island.Kuudra) && kuudraTier == 0)
                sidebarLines.find { cleanLine(it).contains("Kuudra's Hollow (") }?.let {
                    kuudraTier = it.substringBefore(")").lastOrNull()?.digitToIntOrNull() ?: 0
                }
        }.register()
    }

    @SubscribeEvent
    fun onDisconnect(event: FMLNetworkEvent.ClientDisconnectionFromServerEvent) {
        isOnHypixel = false
        isInSkyblock = false
        currentArea = Island.Unknown
        kuudraTier = 0
        currentDungeon = null
    }

    @SubscribeEvent
    fun onWorldChange(event: WorldEvent.Unload) {
        currentDungeon = null
        isInSkyblock = false
        kuudraTier = 0
        currentArea = Island.Unknown
    }

    /**
     * Taken from [SBC](https://github.com/Harry282/Skyblock-Client/blob/main/src/main/kotlin/skyblockclient/utils/LocationUtils.kt)
     *
     * @author Harry282
     */
    @SubscribeEvent
    fun onConnect(event: FMLNetworkEvent.ClientConnectedToServerEvent) {
        isOnHypixel = if (ClickGUIModule.forceHypixel) true else mc.runCatching {
            !event.isLocal && ((thePlayer?.clientBrand?.contains("hypixel", true)
                ?: currentServerData?.serverIP?.contains("hypixel", true)) == true)
        }.getOrDefault(false)
    }

    @SubscribeEvent
    fun onPacket(event: PacketEvent.Receive) {
        if (isOnHypixel || event.packet !is S3FPacketCustomPayload || event.packet.channelName != "MC|Brand") return
        if (event.packet.bufferData?.readStringFromBuffer(Short.MAX_VALUE.toInt())
                ?.contains("hypixel", true) == true
        ) isOnHypixel = true
    }

    /**
     * Returns the current area from the tab list info.
     * If no info can be found, return Island.Unknown.
     */
    private fun getArea(): Island {
        if (mc.isSingleplayer) return Island.SinglePlayer
        if (!isInSkyblock) return Island.Unknown

        val area = mc.thePlayer?.sendQueue?.playerInfoMap?.find {
            it?.displayName?.unformattedText?.startsWithOneOf(
                "Area: ",
                "Dungeon: "
            ) == true
        }?.displayName?.formattedText ?: return Island.Unknown

        return Island.entries.firstOrNull { area.contains(it.displayName, true) } ?: Island.Unknown
    }

    fun getFloor(): Floor? {
        if (currentArea.isArea(Island.SinglePlayer)) return Floor.E
        for (i in sidebarLines) {
            return Floor.valueOf(
                Regex("The Catacombs \\((\\w+)\\)\$").find(cleanSB(i))?.groupValues?.get(1) ?: continue
            )
        }
        return null
    }
}