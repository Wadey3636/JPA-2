package com.github.wadey3636.jpa.commands

import me.modcore.events.impl.P3StartEvent
import me.modcore.features.impl.render.ClickGUIModule
import me.modcore.utils.postAndCatch
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