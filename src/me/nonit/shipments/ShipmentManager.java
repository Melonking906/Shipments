package me.nonit.shipments;

import org.bukkit.inventory.ItemStack;

import java.util.HashSet;

public class ShipmentManager
{
    private final Shipments plugin;
    private final HashSet<ShipmentChest> shipmentChests;
    private final HashSet<ShipmentItem> shipmentItems;

    ShipmentManager( Shipments plugin )
    {
        this.plugin = plugin;
        this.shipmentChests = new HashSet<>();
        this.shipmentItems = new HashSet<>();
    }

    public HashSet<ShipmentChest> getShipmentChests()
    {
        return shipmentChests;
    }

    public HashSet<ShipmentItem> getShipmentItems()
    {
        return shipmentItems;
    }

    public void addShipmentChest( ShipmentChest shipmentChest )
    {
        this.shipmentChests.add( shipmentChest );
    }

    public void removeShipmentChest( ShipmentChest shipmentChest )
    {
        this.shipmentChests.remove( shipmentChest );
    }

    public void addShipmenttem( ShipmentItem shipmentItem )
    {
        this.shipmentItems.add( shipmentItem );
    }

    public void removeShipmenttem( ShipmentItem shipmentItem )
    {
        this.shipmentItems.remove( shipmentItem );
    }

    public double calculateValue( ShipmentChest shipmentChest )
    {
        double value = 0;

        ItemStack[] chestItems = shipmentChest.getChest().getInventory().getContents();

        for( ItemStack itemStack : chestItems )
        {
            if( itemStack == null )
            {
                continue;
            }

            for( ShipmentItem shipmentItem : shipmentItems )
            {
                if( shipmentItem.getType().equals( itemStack.getType() ) )
                {
                    value += (shipmentItem.getValue() * itemStack.getAmount());
                    break;
                }
            }
        }

        return value;
    }
}
