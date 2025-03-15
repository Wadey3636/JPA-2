package com.github.wadey3636.jpa.utils

/*
    The idea for this system of room rotation and converting to and from
room coords was pretty much just yoinked out of Bloom. I originally made
the icefill solver in a chat triggers module that ran off of blooms room
system. So to make the process of porting the solver over easier, I pretty
much stole it.

Credit to Unclaimedbloom6
Their GitHub can be found here: https://github.com/UnclaimedBloom6

*/

import net.minecraft.util.BlockPos

fun playerInRoomBounds(room: RoomInfo, pos: BlockPos): Boolean {
    return (pos.x in (room.getX() - 15)..(room.getX() + 15) &&
            pos.z in (room.getZ() - 15)..(room.getZ() + 15)
            )
}

fun convertToRoomCoords(room: RoomInfo, coords: BlockPos): BlockPos {
    return rotateCoords(BlockPos(coords.x - room.getX(), coords.y, coords.z - room.getZ()), room.rotation)
}


fun convertToRealCoords(room: RoomInfo, coord: BlockPos): BlockPos {
    if (invertRotation(room.rotation) == null) return BlockPos(0, 0, 0)
    val coords = rotateCoords(coord, invertRotation(room.rotation)!!)

    return BlockPos(coords.x + room.getX(), coords.y, coords.z + room.getZ())

}

fun invertRotation(rotation: String): String? {
    when (rotation) {
        "South" -> return "South"
        "North" -> return "North"
        "West" -> return "East"
        "East" -> return "West"
    }
    return null
}


/**
 * Helper method to get the bounds of a room based on its center position and fixed room size.
 */
fun getRoomBounds(room: RoomInfo): Pair<BlockPos, BlockPos> {
    val roomSize = 15 // Half of 30x30 room (1x1 unit), adjust if necessary
    val centerX = room.getX()
    val centerZ = room.getZ()

    val min = BlockPos(centerX - roomSize, 0, centerZ - roomSize)
    val max = BlockPos(centerX + roomSize, 255, centerZ + roomSize)
    return Pair(min, max)
}


fun rotateCoords(coord: BlockPos, rotation: String): BlockPos {
    when (rotation) {
        "South" -> {
            return coord
        }

        "East" -> {
            return BlockPos(coord.z, coord.y, -coord.x)
        }

        "North" -> {
            return BlockPos(-coord.x, coord.y, -coord.z)
        }

        "West" -> {
            return BlockPos(-coord.z, coord.y, coord.x)
        }
    }
    return BlockPos(0, 0, 0)
}