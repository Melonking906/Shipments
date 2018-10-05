package me.nonit.shipments.commands;

import me.nonit.shipments.Shipments;
import me.nonit.shipments.ShipmentItem;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Iterator;
import java.util.Map;

public class ListCommand extends SubCommand
{
    private final Shipments plugin;

    public ListCommand( Shipments plugin )
    {
        super( "list", "shipments.list", true );

        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args )
    {
        Iterator iter = this.plugin.getMaterialIndex().entrySet().iterator();

        int page;

        if( args.length > 1 )
        {
            try
            {
                page = Integer.parseInt( args[1] );
            }
            catch( NumberFormatException e )
            {
                sender.sendMessage( Shipments.getPrefix() + ChatColor.RED + "Invalid page number: " + args[1] );
                return true;
            }

            int end = ( page - 1 ) * 8;

            for( int i = 0; i < end; i++ )
            {
                if( !iter.hasNext() )
                {
                    sender.sendMessage( Shipments.getPrefix() + "NA" );
                    return true;
                }
                iter.next();
            }
        }
        else
        {
            page = 1;
        }

        sender.sendMessage( ChatColor.YELLOW + "--------- [Shipments Page " + page + "] ---------" );

        for( int i = 0; i < 18; i++ )
        {
            if( !iter.hasNext() )
            {
                break;
            }

            Map.Entry e = ( Map.Entry ) iter.next();
            double price = (Double) e.getValue();

            String rawName = ( String ) e.getKey();

            String name = Shipments.prettyName( rawName );

            StringBuilder sb = new StringBuilder( ChatColor.GREEN + name );
            sb.append( ChatColor.GRAY ).append( " - Item: " ).append( ChatColor.GREEN ).append( plugin.getEconomy().format( price ).replace( ".00 ", " " ) ).append( ChatColor.GRAY ).append( " - Stack: " ).append( ChatColor.GREEN );

            ShipmentItem vi = plugin.stringToShipmentItem( rawName );
            if( vi == null )
            {
                sb.append( "N/A" );
            }
            else
            {
                sb.append( plugin.getEconomy().format( price * vi.getType().getMaxStackSize() ).replace( ".00 ", " " ) );
            }

            sender.sendMessage( sb.toString() );
        }

        if( !iter.hasNext() )
        {
            sender.sendMessage( ChatColor.YELLOW + "----------- [End of List] -----------" );
        }
        else
        {
            sender.sendMessage( ChatColor.YELLOW + "--------- [Do /sh list " + ++page + " for more] ---------" );
        }

        return true;
    }
}
