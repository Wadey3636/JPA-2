package com.github.wadey3636.jpa.commands

import com.github.wadey3636.jpa.Core
import com.github.wadey3636.jpa.Core.display
import com.github.wadey3636.jpa.ui.searchui.SearchGui
import com.github.wadey3636.jpa.ui.searchui.Searchbar
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
        if (args != null && args.isNotEmpty()) {Searchbar.searchText = args[0]} else {Searchbar.searchText = ""}

    }

    override fun canCommandSenderUseCommand(sender: ICommandSender?): Boolean {
        return true
    }

    override fun getCommandAliases(): List<String> {
        return listOf("s", "find")
    }

}