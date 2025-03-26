package com.github.wadey3636.jpa.commands

import me.modcore.Core
import me.modcore.Core.display
import me.modcore.ui.searchui.SearchGui
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender

class SearchGuiCommand : CommandBase() {
    override fun getCommandName(): String {
        return "search"
    }

    override fun getCommandUsage(sender: ICommandSender?): String {
        return "Opens Search Gui"
    }

    override fun processCommand(sender: ICommandSender?, args: Array<out String>?) {
        Core.logger.info("Attempting GUI Open!")
        display = SearchGui
    }

    override fun canCommandSenderUseCommand(sender: ICommandSender?): Boolean {
        return true
    }

    override fun getCommandAliases(): List<String> {
        return listOf("s", "find")
    }

}