package me.nonit.shipments;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Chest;
import org.bukkit.scheduler.BukkitRunnable;

public class ShipmentRunnable extends BukkitRunnable
{
    private Shipments plugin;
    private ShipmentManager shipmentManager;

    public ShipmentRunnable( Shipments plugin )
    {
        this.plugin = plugin;
        this.shipmentManager = plugin.getShipmentManager();
    }

    @Override
    public void run()
    {
        Bukkit.broadcastMessage( Shipments.getPrefix() + ChatColor.GOLD + "Shipments for the day have been collected!" );

        for( ShipmentChest shipmentChest : shipmentManager.getShipmentChests() )
        {
            if( !shipmentChest.verifyChest() )
            {
                continue;
            }

            Chest chest = shipmentChest.getChest();
            OfflinePlayer owner = shipmentChest.getOwner();

            double earnings = shipmentManager.calculateValue( shipmentChest );

            plugin.getEconomy().depositPlayer( owner, earnings ); //Apply player earnings
            chest.getBlockInventory().clear(); //Empty the chest

            if( owner.isOnline() && earnings > 0 )
            {
                owner.getPlayer().sendMessage( Shipments.getPrefix() + "You earned " + plugin.getEconomy().format( earnings ) );
            }
        }
    }
}
