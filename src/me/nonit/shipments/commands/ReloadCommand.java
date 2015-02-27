package me.nonit.shipments.commands;

import me.nonit.shipments.Shipments;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends SubCommand
{
    private final Shipments plugin;

    public ReloadCommand( Shipments plugin )
    {
        super( "reload", "shipments.reload", true );

        this.plugin = plugin;
    }

    public boolean onCommand( CommandSender sender, Command cmd, String commandLabel, String[] args )
    {
        plugin.reloadShipments();

        sender.sendMessage( Shipments.getPrefix() + "Configuration reloaded!" );
        return true;
    }
}