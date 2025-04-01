package me.modcore.events

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.modcore.Core.scope
import me.modcore.events.impl.*
import me.modcore.utils.name
import me.modcore.utils.noControlCodes
import me.modcore.utils.postAndCatch
import me.modcore.utils.waitUntilLastItem
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.ContainerChest
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.network.play.server.S32PacketConfirmTransaction
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import kotlin.coroutines.EmptyCoroutineContext

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


    private var lastConfigOpen: Boolean = false
    private var lastGui: GuiScreen? = null
    private var lastTimeQuarter = System.currentTimeMillis()
    private var lastTimeSecond = System.currentTimeMillis()
    private val serverTicked by lazy { ServerTickEvent() }


    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {

        if (event.phase != TickEvent.Phase.START) return
        if (System.currentTimeMillis() - lastTimeQuarter > 250) {
            lastTimeQuarter = System.currentTimeMillis()
            QuarterSecondEvent().postAndCatch()
        }
        if (System.currentTimeMillis() - lastTimeSecond > 1000) {
            lastTimeSecond = System.currentTimeMillis()
            SecondEvent().postAndCatch()
        }
    }

    @SubscribeEvent
    fun onChat(event: ChatPacketEvent) {
        when (event.message) {
            "[BOSS] Storm: I should have known that I stood no chance." -> {
                P3StartEvent().postAndCatch()
            }

            else -> return
        }
    }

}
