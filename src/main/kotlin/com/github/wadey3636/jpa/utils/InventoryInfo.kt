package com.github.wadey3636.jpa.utils


import com.github.wadey3636.jpa.utils.location.Island
import net.minecraft.util.BlockPos

data class InventoryInfo(
    val location: Island,
    val pos: List<BlockPos>?,
    val page: MutableList<Slot>,
    val size: ChestSize
)


enum class ChestSize(val int: Int) {
    Single(1),
    Double(2);


}
