package me.nonit.shipments.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class SubCommand
{
    private final String name;
    private final String permission;
    private final boolean consoleSafe;

    public SubCommand( String name, String permission, boolean consoleSafe )
    {
        this.name = name;
        this.permission = permission;
        this.consoleSafe = consoleSafe;
    }

    public String getName()
    {
        return name;
    }

    public String getPermission()
    {
        return permission;
    }

    public boolean isConsoleSafe()
    {
        return consoleSafe;
    }

    public abstract boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args);
}