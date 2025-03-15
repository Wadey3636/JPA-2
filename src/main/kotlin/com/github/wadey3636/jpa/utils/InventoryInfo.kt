package com.github.wadey3636.jpa.utils

import com.github.wadey3636.jpa.utils.Location
import com.google.gson.JsonObject
import net.minecraft.inventory.Container
import net.minecraft.util.BlockPos

data class InventoryInfo(val location: Location, val pos: List<BlockPos>?, val page: Container) {
}
