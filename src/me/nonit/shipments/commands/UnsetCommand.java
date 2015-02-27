package me.nonit.shipments.commands;

import me.nonit.shipments.Shipments;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UnsetCommand extends SubCommand
{
    private final Shipments plugin;

    public UnsetCommand( Shipments plugin )
    {
        super( "unset", "shipments.unset", false );

        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    public boolean onCommand( CommandSender sender, Command cmd, String commandLabel, String[] args )
    {
        boolean useAll = false;
        if( args.length >= 2 )
        {
            if( args[1].equals( "all" ) )
            {
                useAll = true;
            }
        }

        Player player = (Player) sender;
        ItemStack item = player.getItemInHand();

        if( item.getType().equals( Material.AIR ) )
        {
            player.sendMessage( Shipments.getPrefix() + ChatColor.RED + "You're not holding an item..." );
            return true;
        }

        String name = item.getType().toString();

        int data = item.getData().getData();

        if( useAll )
        {
            plugin.getConfig().set( "blocks." + name, null );
            player.sendMessage( Shipments.getPrefix() + "Shipments no longer buys any " + ChatColor.YELLOW + Shipments.prettyName( name ) + ChatColor.GREEN + "'s." );
        }
        else
        {
            plugin.getConfig().set( "blocks." + name + "." + data, null );
            player.sendMessage( Shipments.getPrefix() + "Shipments no longer buys " + ChatColor.YELLOW + Shipments.prettyName( name ) + ":" + data + ChatColor.GREEN + "." );
        }

        plugin.saveConfig();
        plugin.reloadShipments();

        return true;
    }
}
