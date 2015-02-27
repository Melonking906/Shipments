package me.nonit.shipments.commands;

import me.nonit.shipments.Shipments;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SetCommand extends SubCommand
{
    private final Shipments plugin;

    public SetCommand( Shipments plugin )
    {
        super( "set", "shipments.set", false );

        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    public boolean onCommand( CommandSender sender, Command cmd, String commandLabel, String[] args )
    {
        if( args.length < 2 )
        {
            sender.sendMessage( Shipments.getPrefix() + ChatColor.RED + "You need to enter a price! " + ChatColor.GRAY + "/sh set <price>" );
            return true;
        }

        Double price;
        try
        {
            price = Double.parseDouble( args[1] );
        }
        catch (NumberFormatException e)
        {
            sender.sendMessage( Shipments.getPrefix() + ChatColor.RED + "Price must be a number!" );
            return true;
        }

        boolean useAll = false;
        if( args.length >= 3 )
        {
            if( args[2].equals( "all" ) )
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
            plugin.getConfig().set( "blocks." + name, price );
            player.sendMessage( Shipments.getPrefix() + "Shipments now buys all " + ChatColor.YELLOW + Shipments.prettyName( name ) + ChatColor.GREEN + "'s for " + ChatColor.YELLOW + price + ChatColor.GREEN + "." );
        }
        else
        {
            plugin.getConfig().set( "blocks." + name + "." + data, price );
            player.sendMessage( Shipments.getPrefix() + "Shipments now buys " + ChatColor.YELLOW + Shipments.prettyName( name ) + ":" + data + ChatColor.GREEN + " for " + ChatColor.YELLOW + price + ChatColor.GREEN + "." );
        }

        plugin.saveConfig();
        plugin.reloadShipments();

        return true;
    }
}
