package me.nonit.shipments.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class HelpCommand extends SubCommand
{
    public HelpCommand()
    {
        super( "help", "shipments.help", true );
    }

    public boolean onCommand( CommandSender sender, Command cmd, String commandLabel, String[] args )
    {
        sender.sendMessage( ChatColor.YELLOW + "--------- [Shipments Help] ---------" );
        if( sender.hasPermission( "shipments.ship" ) )
        {
            sender.sendMessage( ChatColor.GREEN + "/" + commandLabel + " ship " + ChatColor.GRAY + "- Sells items in chests." );
        }
        if( sender.hasPermission( "shipments.list" ) )
        {
            sender.sendMessage( ChatColor.GREEN + "/" + commandLabel + " list " + ChatColor.GRAY + "- Lists item values." );
        }
        if( sender.hasPermission( "shipments.reload" ) )
        {
            sender.sendMessage( ChatColor.GREEN + "/" + commandLabel + " reload " + ChatColor.GRAY + "- Reloads the config." );
        }
        if( sender.hasPermission( "shipments.set" ) )
        {
            sender.sendMessage( ChatColor.GREEN + "/" + commandLabel + " set <price> (all) " + ChatColor.GRAY + "- Adds a shipment item." );
        }
        if( sender.hasPermission( "shipments.unset" ) )
        {
            sender.sendMessage( ChatColor.GREEN + "/" + commandLabel + " unset (all) " + ChatColor.GRAY + "- Removes a shipment item." );
        }
        sender.sendMessage( ChatColor.YELLOW + "--------------------------------" );

        return true;
    }
}