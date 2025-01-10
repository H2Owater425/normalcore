package kr.dhmo.normalcore.commands.tabCompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConfigurationCommandTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] arguments) {
        if(commandSender.isOp()) {
            return switch (arguments.length) {
                case 1 -> List.of("save", "reload", "get", "set");
                case 2 -> switch (arguments[0]) {
                    case "get", "set" -> List.of("respawnDelayTime", "respawnPenaltyTime", "lifePrice");
                    default -> null;
                };
                default -> null;
            };
        } else {
            return null;
        }
    }
}
