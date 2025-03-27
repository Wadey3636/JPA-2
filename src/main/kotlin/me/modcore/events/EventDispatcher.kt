package me.modcore.events

import kotlinx.coroutines.launch
import me.modcore.Core.scope
import me.modcore.events.impl.ChatPacketEvent
import me.modcore.events.impl.GuiEvent
import me.modcore.events.impl.PacketEvent
import me.modcore.events.impl.ServerTickEvent
import me.modcore.utils.name
import me.modcore.utils.noControlCodes
import me.modcore.utils.postAndCatch
import me.modcore.utils.waitUntilLastItem
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.ContainerChest
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.network.play.server.S32PacketConfirmTransaction
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object EventDispatcher {


    /**
     * Dispatches [ChatPacketEvent], [ServerTickEvent]
     */
    @SubscribeEvent
    fun onPacket(event: PacketEvent.Receive) {

        if (event.packet is S32PacketConfirmTransaction) ServerTickEvent().postAndCatch()

        if (event.packet !is S02PacketChat || !ChatPacketEvent(event.packet.chatComponent.unformattedText.noControlCodes).postAndCatch()) return
        event.isCanceled = true
    }


    /**
     * Dispatches [GuiEvent.Loaded]
     */
    @SubscribeEvent
    fun onGuiOpen(event: GuiOpenEvent) = scope.launch {
        if (event.gui !is GuiChest) return@launch
        val container = (event.gui as GuiChest).inventorySlots

        if (container !is ContainerChest) return@launch

        val deferred = waitUntilLastItem(container)
        try {
            deferred.await()
        } catch (_: Exception) {
            return@launch
        } // Wait until the last item in the chest isn't null

        GuiEvent.Loaded(container, container.name).postAndCatch()
    }
}
