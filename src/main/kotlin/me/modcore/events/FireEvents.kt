// Function copied from [https://github.com/odtheking/Odin/blob/44062aed8e0307333e45efbde24b9e384e3476ec/src/main/kotlin/me/odinmain/events/EventDispatcher.kt#L21]
// Copyright (c) 2024, odtheking
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, this
//    list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
//
// 3. Neither the name of the copyright holder nor the names of its
//    contributors may be used to endorse or promote products derived from
//    this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
// CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.


package me.modcore.events

import com.github.wadey3636.jpa.utils.waitUntilLastItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.modcore.events.impl.*
import me.modcore.utils.postAndCatch
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.ContainerChest
import net.minecraft.network.play.server.S32PacketConfirmTransaction
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import kotlin.coroutines.EmptyCoroutineContext

class FireEvents {


    private var lastConfigOpen: Boolean = false
    private var lastGui: GuiScreen? = null
    private var lastTimeQuarter = System.currentTimeMillis()
    private var lastTimeSecond = System.currentTimeMillis()
    private val serverTicked by lazy { ServerTickEvent() }

    @SubscribeEvent
    fun onServerTick(event: PacketEvent) {
        if (event.packet is S32PacketConfirmTransaction) ServerTickEvent().postAndCatch()
    }

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