package com.github.wadey3636.jpa.commands

import com.github.wadey3636.jpa.events.impl.P3StartEvent
import com.github.wadey3636.jpa.features.render.ClickGUIModule
import com.github.wadey3636.jpa.utils.postAndCatch
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender

class SimulateP3Command : CommandBase() {
    override fun getCommandName(): String {
        return "simP3Start"
    }

    override fun getCommandUsage(sender: ICommandSender?): String {
        return ""
    }

    override fun processCommand(sender: ICommandSender?, args: Array<out String>?) {
        if (ClickGUIModule.devMode) P3StartEvent().postAndCatch()

    }

    override fun canCommandSenderUseCommand(sender: ICommandSender?): Boolean {
        return true
    }

    override fun getCommandAliases(): List<String> {
        return listOf()
    }

}