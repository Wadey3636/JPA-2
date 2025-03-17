package com.github.wadey3636.jpa.utils.dungeon

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.modcore.Core.mc
import me.modcore.Core.scope
import me.modcore.events.impl.PacketEvent
import me.modcore.utils.equalsOneOf
import me.modcore.utils.noControlCodes

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.network.play.server.S38PacketPlayerListItem
import net.minecraft.network.play.server.S3EPacketTeams
import net.minecraft.network.play.server.S47PacketPlayerListHeaderFooter
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import com.github.wadey3636.jpa.utils.dungeon.DungeonClass
import me.modcore.utils.romanToInt
import me.modcore.utils.skyblock.PlayerUtils.posX
import me.modcore.utils.skyblock.PlayerUtils.posZ

// could add some system to look back at previous runs.
class Dungeon(val floor: Floor) {

    private var expectingBloodUpdate: Boolean = false

    var paul = false
    val inBoss: Boolean get() = getBoss()
    var dungeonTeammates: ArrayList<DungeonPlayer> = ArrayList(5)
    var dungeonTeammatesNoSelf: ArrayList<DungeonPlayer> = ArrayList(4)
    var leapTeammates: ArrayList<DungeonPlayer> = ArrayList(4)

    private fun getBoss(): Boolean {
        return when (floor.floorNumber) {
            1 -> posX > -71 && posZ > -39
            in 2..4 -> posX > -39 && posZ > -39
            in 5..6 -> posX > -39 && posZ > -7
            7 -> posX > -7 && posZ > -7
            else -> false
        }
    }



    fun onPacket(event: PacketEvent.Receive) {
        when (event.packet) {
            is S38PacketPlayerListItem -> handleTabListPacket(event.packet)
        }
    }

    fun onWorldLoad() {
        dungeonTeammates = ArrayList()
        dungeonTeammatesNoSelf = ArrayList()
        leapTeammates = ArrayList()
        Blessing.entries.forEach { it.current = 0 }
    }




    private fun handleTabListPacket(packet: S38PacketPlayerListItem) {
        if (!packet.action.equalsOneOf(S38PacketPlayerListItem.Action.UPDATE_DISPLAY_NAME, S38PacketPlayerListItem.Action.ADD_PLAYER)) return
        updateDungeonTeammates(packet.entries)

    }

    private val partyMessageRegex = Regex("Party > .*?: (.+)\$")

    private val tablistRegex = Regex("^\\[(\\d+)] (?:\\[\\w+] )*(\\w+) .*?\\((\\w+)(?: (\\w+))*\\)$")

    private fun updateDungeonTeammates(tabList: List<S38PacketPlayerListItem.AddPlayerData>) {
        dungeonTeammates = getDungeonTeammates(dungeonTeammates, tabList)
        dungeonTeammatesNoSelf = ArrayList(dungeonTeammates.filter { it.entity != mc.thePlayer })
    }

    private fun getDungeonTeammates(previousTeammates: ArrayList<DungeonPlayer>, tabList: List<S38PacketPlayerListItem.AddPlayerData>): ArrayList<DungeonPlayer> {
        for (line in tabList) {
            val displayName = line.displayName?.unformattedText?.noControlCodes ?: continue
            val (_, name, clazz, clazzLevel) = tablistRegex.find(displayName)?.destructured ?: continue

            previousTeammates.find { it.name == name }?.let { player -> player.isDead = clazz == "DEAD" } ?:
            previousTeammates.add(DungeonPlayer(name, DungeonClass.entries.find { it.name == clazz } ?: continue, clazzLvl = romanToInt(clazzLevel), mc.netHandler?.getPlayerInfo(name)?.locationSkin ?: continue, mc.theWorld?.getPlayerEntityByName(name), false))
        }
        return previousTeammates
    }
}