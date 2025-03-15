package com.github.wadey3636.jpa.utils

import net.minecraft.util.BlockPos

data class VariantInfo(
    val detectionPoints: HashSet<BlockPos>,
    val name: String,
    val plotPoints: List<BlockPos>,
    val warpPoints: List<BlockPos>,
    val tpPoint: BlockPos?
)

data class DeterminedVariant(
    val name: String,
    val plotPoints: List<BlockPos>,
    val warpPoints: List<BlockPos>,
    val tpPoint: BlockPos?
)

