package me.modcore.events.impl

import net.minecraftforge.fml.common.eventhandler.Event

data class RenderOverlayNoCaching(val partialTicks: Float) : Event()