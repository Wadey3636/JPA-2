package com.github.wadey3636.jpa.events.impl

import net.minecraftforge.fml.common.eventhandler.Event

data class RenderOverlayNoCaching(val partialTicks: Float) : Event()