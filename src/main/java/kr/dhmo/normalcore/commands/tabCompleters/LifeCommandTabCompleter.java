package kr.dhmo.normalcore.commands.tabCompleters;

import kr.dhmo.normalcore.managers.PlayersManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LifeCommandTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] arguments) {
        switch (arguments.length) {
            case 1: {
                if(commandSender.isOp()) {
                    return List.of("get", "deposit", "withdraw", "remit", "set");
                } else {
                    return List.of("get", "deposit", "withdraw", "remit");
                }
            }

            case 2: {
                switch(arguments[0].toLowerCase()) {
                    case "get": {
                        if(commandSender.isOp()) {
                            return PlayersManager.getOfflinePlayerNames();
                        } else {
                            return List.of(commandSender.getName());
                        }
                    }
                    case "set": {
                        if(commandSender.isOp()) {
                            return PlayersManager.getOfflinePlayerNames();
                        } else {
                            return null;
                        }
                    }

                    case "remit": {
                        return List.of(commandSender.getName());
                    }
                }
            }

            default: {
                return null;
            }
        }
    }
}
