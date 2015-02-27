package me.nonit.shipments;

import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;

/*
 * This class is to store chests in a save way. Storing the chests
 * directly is not allowed!
 */

public class ShipmentChest
{
    private final Shipments plugin;
    private final String world;
    private final int x;
    private final int y;
    private final int z;

    public ShipmentChest( Shipments plugin, Chest chest )
    {
        this.plugin = plugin;
        world = chest.getWorld().getName();
        x = chest.getX();
        y = chest.getY();
        z = chest.getZ();
    }

    public Chest getHandle()
    {
        World world = plugin.getServer().getWorld( this.world );
        if( world == null )
        {
            return null;
        }

        BlockState blockState = world.getBlockAt( x, y, z ).getState();
        if( !( blockState instanceof Chest ) )
        {
            return null;
        }

        return ( Chest ) blockState;
    }
}