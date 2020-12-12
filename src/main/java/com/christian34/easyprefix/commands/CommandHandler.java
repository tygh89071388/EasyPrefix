package com.christian34.easyprefix.commands;

import com.christian34.easyprefix.EasyPrefix;
import com.christian34.easyprefix.commands.color.ColorCommand;
import com.christian34.easyprefix.commands.easyprefix.EasyPrefixCommand;
import com.christian34.easyprefix.commands.easyprefix.set.SetCommandListener;
import com.christian34.easyprefix.commands.tags.TagsCommand;
import com.christian34.easyprefix.utils.Debug;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * EasyPrefix 2020.
 *
 * @author Christian34
 */
public class CommandHandler implements CommandExecutor, TabCompleter {
    private final List<EasyCommand> commands;

    public CommandHandler(EasyPrefix instance) {
        this.commands = new ArrayList<>();
        commands.add(new EasyPrefixCommand(instance));
        commands.add(new TagsCommand(instance));
        commands.add(new ColorCommand(instance));

        for (EasyCommand command : commands) {
            PluginCommand pluginCommand = instance.getCommand(command.getName());
            if (pluginCommand == null) {
                throw new RuntimeException("Unknown command '" + command.getName() + "'!");
            }

            pluginCommand.setExecutor(this);
            pluginCommand.setTabCompleter(this);
        }

        Bukkit.getPluginManager().registerEvents(new SetCommandListener(instance), instance);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        for (EasyCommand easyCommand : commands) {
            if (easyCommand.getName().equalsIgnoreCase(command.getName())) {
                try {
                    easyCommand.handleCommand(sender, Arrays.asList(args));
                } catch (Exception e) {
                    Debug.captureException(e);
                }
                break;
            }
        }

        return false;
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        for (EasyCommand easyCommand : commands) {
            if (easyCommand.getName().equalsIgnoreCase(command.getName())) {
                try {
                    return easyCommand.getTabCompletion(sender, Arrays.asList(args));
                } catch (Exception e) {
                    Debug.captureException(e);
                }
            }
        }

        return null;
    }

}
