package com.github.wadey3636.jpa.events


import net.minecraft.inventory.ContainerChest
import net.minecraft.inventory.IInventory
import net.minecraftforge.fml.common.eventhandler.Event


class OpenGuiEvent(val name: String, val gui: ContainerChest, val inventory: IInventory) : Event()