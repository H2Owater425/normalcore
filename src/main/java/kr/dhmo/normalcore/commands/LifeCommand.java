package kr.dhmo.normalcore.commands;

import kr.dhmo.normalcore.Normalcore;
import kr.dhmo.normalcore.managers.ConfigurationManager;
import kr.dhmo.normalcore.managers.PlayersManager;
import kr.dhmo.normalcore.utilities.FormatUtility;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

final public class LifeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] arguments) {
        switch(arguments.length) {
            // /life
            case 0: {
                if(commandSender instanceof Player player) {
                    commandSender.sendMessage("You have " + FormatUtility.pluralize(PlayersManager.getLife(player.getUniqueId()), "life") + " left");

                    return true;
                } else {
                    commandSender.sendMessage(Normalcore.ERROR_COLOR + "CommandSender must be player");

                    return false;
                }
            }

            // /life get /life deposit /life withdraw
            case 1: {
                if(commandSender instanceof Player player) {
                    final UUID playerUniqueId = player.getUniqueId();

                    switch(arguments[0].toLowerCase()) {
                        case "get": {
                            commandSender.sendMessage("You have " + FormatUtility.pluralize(PlayersManager.getLife(player.getUniqueId()), "life") + " left");

                            return true;
                        }

                        case "deposit": {
                            int remainingLifePrice = ConfigurationManager.getLifePrice();
                            final Inventory playerInventory = player.getInventory();
                            final ItemStack[] playerItemStacks = playerInventory.getContents();
                            final ItemStack[] itemStacks = new ItemStack[playerItemStacks.length];

                            for(int i = 0; i < playerItemStacks.length; i++) {
                                if(playerItemStacks[i] != null) {
                                    if(playerItemStacks[i].getType() == Material.ENCHANTED_GOLDEN_APPLE && remainingLifePrice != 0) {
                                        final int itemCount = playerItemStacks[i].getAmount();

                                        if(itemCount >= remainingLifePrice) {
                                            if(itemCount > remainingLifePrice) {
                                                itemStacks[i] = playerItemStacks[i].clone();

                                                itemStacks[i].setAmount(itemCount - remainingLifePrice);
                                            }

                                            remainingLifePrice = 0;
                                        } else {
                                            remainingLifePrice -= itemCount;
                                        }
                                    } else {
                                        itemStacks[i] = playerItemStacks[i];
                                    }
                                }
                            }

                            if(remainingLifePrice == 0) {
                                playerInventory.setContents(itemStacks);
                                PlayersManager.setLife(playerUniqueId, PlayersManager.getLife(playerUniqueId) + 1);
                                commandSender.sendMessage("You have " + FormatUtility.pluralize(PlayersManager.getLife(player.getUniqueId()), "life") + " now");

                                return true;
                            } else {
                                commandSender.sendMessage(Normalcore.ERROR_COLOR + "You must have " + FormatUtility.pluralize(remainingLifePrice, "more enchanted golden apple"));

                                return false;
                            }
                        }

                        case "withdraw": {
                            final int playerLife = PlayersManager.getLife(playerUniqueId) - 1;

                            if(playerLife != -1) {
                                PlayersManager.setLife(playerUniqueId, playerLife);

                                player.getInventory().addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));

                                commandSender.sendMessage("You have " + FormatUtility.pluralize(playerLife, "life") + " now");

                                return true;
                            } else {
                                commandSender.sendMessage(Normalcore.ERROR_COLOR + "You must have at least 1 life");

                                return false;
                            }
                        }

                        default: {
                            commandSender.sendMessage(Normalcore.ERROR_COLOR + "Arguments[0] must be deposit or withdraw");

                            return false;
                        }
                    }
                } else {
                    commandSender.sendMessage(Normalcore.ERROR_COLOR + "CommandSender must be player");

                    return false;
                }
            }

            // /life get <player> /life remit <player>
            case 2: {
                switch(arguments[0].toLowerCase()) {
                    case "get": {
                        final boolean isOp = commandSender.isOp();
                        if(arguments[1].equalsIgnoreCase(commandSender.getName()) || isOp) {
                            final OfflinePlayer targetPlayer = PlayersManager.getOfflinePlayer(arguments[1]);

                            if(targetPlayer != null) {
                                final UUID targetPlayerUniqueId = targetPlayer.getUniqueId();

                                commandSender.sendMessage(targetPlayer.getName() + " has " + FormatUtility.pluralize(PlayersManager.getLife(targetPlayerUniqueId), "life") + " left");

                                return true;
                            } else {
                                commandSender.sendMessage(Normalcore.ERROR_COLOR + "Argument[1] must be valid player");

                                return false;
                            }
                        } else {
                            commandSender.sendMessage(Normalcore.ERROR_COLOR + "Argument[1] must be self");

                            return false;
                        }
                    }

                    case "remit": {
                        if(commandSender instanceof Player player) {
                            final UUID playerUniqueId = player.getUniqueId();
                            final int playerLife = PlayersManager.getLife(playerUniqueId) - 1;

                            if(playerLife != -1) {
                                if(!player.getName().equalsIgnoreCase(arguments[1].toLowerCase())) {
                                    final OfflinePlayer targetPlayer = PlayersManager.getOfflinePlayer(arguments[1]);

                                    if(targetPlayer != null) {
                                        final UUID targetPlayerUniqueId = targetPlayer.getUniqueId();
                                        final int targetPlayerLife = PlayersManager.getLife(targetPlayerUniqueId) + 1;

                                        PlayersManager.setLife(playerUniqueId, playerLife);

                                        if(targetPlayer.isOnline()) {
                                            Player onlineTargetPlayer = (Player)targetPlayer;

                                            if(targetPlayerLife == 1 && PlayersManager.hasTimer(targetPlayerUniqueId)) {
                                                Normalcore.respawn(onlineTargetPlayer);

                                                onlineTargetPlayer.sendMessage(player.getName() + " remitted life");
                                            } else {
                                                PlayersManager.setLife(targetPlayerUniqueId, targetPlayerLife);

                                                onlineTargetPlayer.sendMessage(player.getName() + " remitted life and you have " + FormatUtility.pluralize(targetPlayerLife, "life") + " now");
                                            }
                                        } else {
                                            PlayersManager.setLife(targetPlayerUniqueId, targetPlayerLife);
                                        }

                                        commandSender.sendMessage("You have " + FormatUtility.pluralize(playerLife, "life") + " now");

                                        return true;
                                    } else {
                                        player.sendMessage(Normalcore.ERROR_COLOR + "Argument[1] must be valid player");

                                        return false;
                                    }
                                } else {
                                    player.sendMessage(Normalcore.ERROR_COLOR + "Argument[1] must not be yourself");

                                    return false;
                                }
                            } else {
                                commandSender.sendMessage(Normalcore.ERROR_COLOR + "You must have at least 1 life");

                                return false;
                            }
                        } else {
                            commandSender.sendMessage(Normalcore.ERROR_COLOR + "CommandSender must be player");

                            return false;
                        }
                    }

                    default: {
                        commandSender.sendMessage(Normalcore.ERROR_COLOR + "Arguments[0] must be get or remit");

                        return false;
                    }
                }
            }

            // /life set <player> <count>
            case 3: {
                if(commandSender.isOp()) {
                    final OfflinePlayer targetPlayer = PlayersManager.getOfflinePlayer(arguments[1]);

                    if(targetPlayer != null) {
                        final UUID targetPlayerUniqueId = targetPlayer.getUniqueId();

                        try {
                            final int targetPlayerLife = Integer.parseInt(arguments[2], 10);

                            if(targetPlayerLife >= 0) {
                                PlayersManager.setLife(targetPlayerUniqueId, targetPlayerLife);
                                commandSender.sendMessage(targetPlayer.getName() + " has " + FormatUtility.pluralize(targetPlayerLife, "life") + " now");

                                return true;
                            } else {
                                throw new NumberFormatException();
                            }
                        } catch(Exception exception) {
                            commandSender.sendMessage(Normalcore.ERROR_COLOR + "Arguments[2] must be positive integer or zero");

                            return false;
                        }
                    } else {
                        commandSender.sendMessage(Normalcore.ERROR_COLOR + "Argument[1] must be valid player");

                        return false;
                    }
                } else {
                    commandSender.sendMessage(Normalcore.ERROR_COLOR + "CommandSender must be op");

                    return false;
                }
            }

            default: {
                commandSender.sendMessage(Normalcore.ERROR_COLOR + "Arguments.length must between 0 and 3");

                return false;
            }
        }
    }
}
