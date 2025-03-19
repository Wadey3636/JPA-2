package com.github.wadey3636.jpa.utils.dungeon


import com.github.wadey3636.jpa.events.DungeonStartEvent
import com.github.wadey3636.jpa.utils.WorldUtils.getSidebarLines
import com.github.wadey3636.jpa.utils.location.Island
import com.github.wadey3636.jpa.utils.location.LocationUtils
import me.modcore.events.impl.ChatPacketEvent
import me.modcore.utils.postAndCatch
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


var scoreboard: Array<String> = arrayOf()
var dungeonFloor = ""
val players: HashSet<DungeonPlayerInfo> = hashSetOf()


class DungeonUtils {
    //[www.hypixel.net,                , Auto-closing in: 1:52,              , [M] Wadey36 [Lv37], [B] BearSleeping_ [Lv11], [A] Skeldonarmy [Lv25], [H] Garduuk [Lv12], [M] GoldCezar [Lv11],        ,   Ironman,   The Catacombs (F2),  10:20pm,  Early Winter 13th, 10/29/24 m141A]


    //val testvalue = arrayOf("www.hypixel.ne🎂t","            🎉"," Auto-closing in:🎁 1:57","          👹"," ","        ⚽","  ♲ Ironman🍭","  ⏣ The Catac👽ombs (F7),  2:40pm👾","  Early Winter 13🐍th"," 🔮10/29/24 m14👽1A")

    @SubscribeEvent
    fun dungeonInformation(event: ChatPacketEvent) {
        if (event.message == "[NPC] Mort: Here, I found this map when I first entered the dungeon.") {
            players.clear()
            val sidebar = getSidebarLines()
            sidebar.forEach { scoreboardLine ->
                Regex("\\[([A-Z])]([0-9a-zA-Z-_]+)").find(scoreboardLine)?.value?.let {
                    players.add(DungeonPlayerInfo(it.removeRange(IntRange(0, 2)), it[1].toString()))
                    return@forEach

                }

                Regex("\\((?<Type>[FM])(?<Floor>\\d)\\)").find(scoreboardLine)?.groupValues?.let {
                    DungeonStartEvent("${it[1]}${it[2]}").postAndCatch()
                    return@forEach
                }
            }
        }
    }

    companion object {
        inline val inDungeons: Boolean
            get() = LocationUtils.currentArea.isArea(Island.Dungeon)

    }


}
//7cTheCataccombs7F7
//12