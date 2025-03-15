package com.github.wadey3636.jpa.utils

data class RoomInfo(val name: String, val center: IntArray, val rotation: String) {
    fun getX(): Int {
        return center[0]
    }

    fun getZ(): Int {
        return center[1]
    }
}


