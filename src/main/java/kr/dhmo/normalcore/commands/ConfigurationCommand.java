package kr.dhmo.normalcore.commands;

import kr.dhmo.normalcore.Normalcore;
import kr.dhmo.normalcore.managers.ConfigurationManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

final public class ConfigurationCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] arguments) {
        if(commandSender.isOp()) {
            switch(arguments.length) {
                // /configuration
                case 0: {
                    commandSender.sendMessage("respawnDelayTime: " + ConfigurationManager.getRespawnDelayTime() + "\nrespawnPenaltyTime: " + ConfigurationManager.getRespawnPenaltyTime() + "\nlifePrice: " + ConfigurationManager.getLifePrice());

                    return true;
                }

                // /configuration save /configuration load
                case 1: {
                    switch(arguments[0].toLowerCase()) {
                        case "save": {
                            try {
                                ConfigurationManager.save();

                                commandSender.sendMessage("Save complete");
                            } catch (Exception exception) {
                                commandSender.sendMessage(exception.getMessage());

                                return false;
                            }
                        }

                        case "reload": {
                            ConfigurationManager.reload();

                            commandSender.sendMessage("Reload complete");

                            return true;
                        }

                        default: {
                            commandSender.sendMessage(Normalcore.errorColor + "Arguments[0] must be save or reload");

                            return false;
                        }
                    }
                }

                // /configuration get <name>
                case 2: {
                    if(arguments[0].equalsIgnoreCase("get")) {
                        return switch (arguments[1].toLowerCase()) {
                            case "respawndelaytime" -> {
                                commandSender.sendMessage("respawnDelayTime: " + ConfigurationManager.getRespawnDelayTime());

                                yield true;
                            }
                            case "respawnpenaltytime" -> {
                                commandSender.sendMessage("respawnPenaltyTime: " + ConfigurationManager.getRespawnPenaltyTime());

                                yield true;
                            }
                            case "lifeprice" -> {
                                commandSender.sendMessage("lifePrice: " + ConfigurationManager.getLifePrice());

                                yield true;
                            }
                            default -> {
                                commandSender.sendMessage(Normalcore.errorColor + "Arguments[1] must be one of respawnDelayTime, respawnPenaltyTime, lifePrice");

                                yield false;
                            }
                        };
                    } else {
                        commandSender.sendMessage(Normalcore.errorColor + "Arguments[0] must be get");

                        return false;
                    }
                }

                // /configuration set <name> <value>
                case 3: {
                    if(arguments[0].equalsIgnoreCase("set")) {
                        switch(arguments[1].toLowerCase()) {
                            case "respawndelaytime": {
                                try {
                                    final long respawnDelayTime = Long.parseLong(arguments[2], 10);

                                    if(respawnDelayTime >= 0) {
                                        ConfigurationManager.setRespawnDelayTime(respawnDelayTime);
                                        commandSender.sendMessage("respawnDelayTime: " + respawnDelayTime);

                                        return true;
                                    } else {
                                        throw new NumberFormatException();
                                    }
                                } catch(Exception exception) {
                                    commandSender.sendMessage(Normalcore.errorColor + "Arguments[2] must be positive long or zero");

                                    return false;
                                }
                            }

                            case "respawnpenaltytime": {
                                try {
                                    final int respawnPenaltyTime = Integer.parseInt(arguments[2], 10);

                                    if(respawnPenaltyTime >= 0) {
                                        ConfigurationManager.setRespawnPenaltyTime(respawnPenaltyTime);
                                        commandSender.sendMessage("respawnPenaltyTime: " + respawnPenaltyTime);

                                        return true;
                                    } else {
                                        throw new NumberFormatException();
                                    }
                                } catch(Exception exception) {
                                    commandSender.sendMessage(Normalcore.errorColor + "Arguments[2] must be positive integer or zero");

                                    return false;
                                }
                            }

                            case "lifeprice": {
                                try {
                                    final int lifePrice = Integer.parseInt(arguments[2], 10);

                                    if(lifePrice >= 0) {
                                        ConfigurationManager.setLifePrice(lifePrice);
                                        commandSender.sendMessage("lifePrice: " + lifePrice);

                                        return true;
                                    } else {
                                        throw new NumberFormatException();
                                    }
                                } catch(Exception exception) {
                                    commandSender.sendMessage(Normalcore.errorColor + "Arguments[2] must be positive integer or zero");

                                    return false;
                                }
                            }

                            default: {
                                commandSender.sendMessage(Normalcore.errorColor + "Arguments[1] must be one of respawnDelayTime, respawnPenaltyTime, lifePrice");

                                return false;
                            }
                        }
                    } else {
                        commandSender.sendMessage(Normalcore.errorColor + "Arguments[1] must be set");

                        return false;
                    }
                }

                default: {
                    commandSender.sendMessage(Normalcore.errorColor + "Arguments.length must between 0 and 3");

                    return false;
                }
            }
        } else {
            commandSender.sendMessage(Normalcore.errorColor + "CommandSender must be op");

            return false;
        }
    }
}
