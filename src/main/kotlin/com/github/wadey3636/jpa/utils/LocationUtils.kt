package com.github.wadey3636.jpa.utils

import me.modcore.Core.mc
import me.modcore.utils.cleanSB
import me.modcore.utils.clock.Executor
import me.modcore.utils.clock.Executor.Companion.register
import me.modcore.utils.sidebarLines
import me.modcore.utils.skyblock.modMessage
import me.modcore.utils.startsWithOneOf
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


var inSkyBlock = false
var inDungeon = false
var inGarden = false

object LocationUtils {
    var isOnHypixel: Boolean = false
        private set
    var isInSkyblock: Boolean = false
        private set
    var currentArea: Location = Location.UNKNOWN
        private set

    init {
        Executor(500, "LocationUtils") {
            if (!isInSkyblock)
                isInSkyblock = isOnHypixel && mc.theWorld?.scoreboard?.getObjectiveInDisplaySlot(1)?.let { cleanSB(it.displayName).contains("SKYBLOCK") } == true

            if (currentArea.isArea(Location.UNKNOWN)) currentArea = getArea()

            //if ((DungeonUtils.inDungeons || currentArea.isArea(Location.SINGLEPLAYER)) && currentDungeon == null) currentDungeon = Dungeon(getFloor() ?: return@Executor)

        }.register()
    }

    /**
     * Returns the current area from the tab list info.
     * If no info can be found, return Location.UNKNOWN.
     */
    private fun getArea(): Location {
        if (mc.isSingleplayer) return Location.SINGLEPLAYER
        if (!isInSkyblock) return Location.UNKNOWN

        val area = mc.thePlayer?.sendQueue?.playerInfoMap?.find { it?.displayName?.unformattedText?.startsWithOneOf("Area: ", "Dungeon: ") == true }?.displayName?.formattedText ?: return Location.UNKNOWN

        return Location.entries.firstOrNull { area.contains(it.name, true) } ?: Location.UNKNOWN
    }

    @SubscribeEvent
    fun worldLoad(event: WorldEvent.Unload) {
        inDungeon = false
        inGarden = false
        inSkyBlock = false
    }
    fun getLocation(): Location? {
        return Location.UNKNOWN
    }
/*
    fun getFloor(): Floor? {
        if (currentArea.isArea(Location.SINGLEPLAYER)) return Floor.E
        for (i in sidebarLines) {
            return Floor.valueOf(Regex("The Catacombs \\((\\w+)\\)\$").find(cleanSB(i))?.groupValues?.get(1) ?: continue)
        }
        return null
    }

 */
}

enum class Location(displayName: String) {
    UNKNOWN("unknown"),
    PRIVATEISLAND("Private Island"),
    DUNGEON("Dungeon"),
    SINGLEPLAYER("Single Player");

    fun isArea(area: Location): Boolean {
        if (this == SINGLEPLAYER) return true
        return this == area
    }

    fun isArea(vararg areas: Location): Boolean {
        if (this == SINGLEPLAYER) return true
        return this in areas
    }
}

//Hub
//Dungeon Hub
//Garden
//The Farming Islands
//Dwarven Mines
//Crystal Hollows
//The End
//Spider\u0027s Den