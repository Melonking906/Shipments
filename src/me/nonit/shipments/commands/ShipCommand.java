package me.nonit.shipments.commands;

import me.nonit.shipments.Shipments;
import me.nonit.shipments.ShipmentItem;
import org.bukkit.ChatColor;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShipCommand extends SubCommand
{
    private final Shipments plugin;

    public ShipCommand( Shipments plugin )
    {
        super( "ship", "shipments.ship", false );

        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    public boolean onCommand( CommandSender sender, Command cmd, String commandLabel, String[] args )
    {
        Player player = ( Player ) sender;
        String pn = player.getName();
        if( this.plugin.getAgreement().containsKey( pn ) )
        {
            Chest chest = this.plugin.getAgreement().get( pn ).getHandle();
            if( chest == null )
            {
                player.sendMessage( Shipments.getPrefix() + ChatColor.RED + "Internal error: chest == null!" );
                return true;
            }

            ItemStack[] itemStacks;
            int contents = ( itemStacks = chest.getInventory().getContents() ).length;

            for( int i = 0; i < contents; i++ )
            {
                ItemStack itemStack = itemStacks[i];
                if( itemStack != null )
                {
                    String found = null;

                    for( String key : this.plugin.getMaterialIndex().keySet() )
                    {
                        ShipmentItem vi = this.plugin.stringToShipmentItem( key );
                        if( vi != null )
                        {
                            if( ( vi.getMat().toString().equals( itemStack.getType().toString() ) ) && ( itemStack.getDurability() == vi.getData() ) )
                            {
                                found = key;
                                break;
                            }
                        }
                    }

                    if( found != null )
                    {
                        double price = this.plugin.getMaterialIndex().get( found ) * itemStack.getAmount();
                        this.plugin.getEconomy().depositPlayer( player.getName(), price );
                        chest.getInventory().remove( itemStack );
                    }
                }
            }

            this.plugin.getAgreement().remove( pn );
            player.sendMessage( Shipments.getPrefix() + "Your items have been shipped!" );
        }
        else
        {
            player.sendMessage( Shipments.getPrefix() + "No shipping box selected." );
        }

        return true;
    }
}