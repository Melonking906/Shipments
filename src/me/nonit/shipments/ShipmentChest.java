package me.nonit.shipments;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;

import java.util.UUID;

public class ShipmentChest
{
    private final World world;
    private final int x;
    private final int y;
    private final int z;
    private final UUID ownerUUID;

    ShipmentChest( World world, int x, int y, int z )
    {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    ShipmentChest( Chest chest )
    {
        world = chest.getWorld();
        x = chest.getX();
        y = chest.getY();
        z = chest.getZ();
    }

    public boolean verifyChest()
    {
        Block chestBlock = world.getBlockAt( x, y, z );
        if( !chestBlock.getType().equals( Material.CHEST ) )
        {
            return false;
        }

        if( getOwner() == null )
        {
            return false;
        }

        return true;
    }

    public OfflinePlayer getOwner()
    {
        Block ownerSignBlock = world.getBlockAt( x, y + 1, z );
        if( !ownerSignBlock.getType().equals( Material.WALL_SIGN ) || !ownerSignBlock.getType().equals( Material.SIGN ) )
        {
            return null;
        }

        Sign ownerSign = (Sign) ownerSignBlock.getState();
        String name = ownerSign.getLine( 2 );
        OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer( name );

        if( offlinePlayer == null || !offlinePlayer.hasPlayedBefore() )
        {
            return null;
        }

        return offlinePlayer;
    }

    public Chest getChest()
    {
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