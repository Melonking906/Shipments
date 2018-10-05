package me.nonit.shipments;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ShipmentListener implements Listener
{
    private final Shipments plugin;

    private static final String MAC_JUNK = "[^\\x00-\\x7F]";

    ShipmentListener( Shipments plugin )
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSignChange( SignChangeEvent event )
    {
        if( event.isCancelled() )
        {
            return;
        }

        if( event.getPlayer() == null )
        {
            return;
        }

        Player player = event.getPlayer();

        if( !player.hasPermission("shipments.place") )
        {
            return;
        }

        String line0 = event.getLine(0);
        line0 = line0.replaceAll( MAC_JUNK, "" );

        String line1 = event.getLine(1);
        line1 = line1.replaceAll( MAC_JUNK, "" );

        if ( !(line0.equalsIgnoreCase("[shipment]") || line0.equalsIgnoreCase("[shipments]"))
                || !(line1.equalsIgnoreCase("[shipment]") || line1.equalsIgnoreCase("[shipments]")) )
        {
            return;
        }

        Block chestBlock = getChestBlockBelow( event.getBlock() );
        if( chestBlock == null )
        {
            event.setCancelled( true );
            player.sendMessage( Shipments.getPrefix() + ChatColor.RED + "No chest found, place one first!" );
            return;
        }

        if( !canPlayerAccessBlock( player, chestBlock ) )
        {
            event.setCancelled( true );
            player.sendMessage( Shipments.getPrefix() + ChatColor.RED + "That chest is locked by someone else!" );
            return;
        }

        event.setLine( 0, "");
        event.setLine( 1, ChatColor.GREEN + "[Shipment]" );
        event.setLine( 2, player.getName() );

        plugin.getShipmentManager().addShipmentChest( new ShipmentChest( chestBlock.getWorld(), chestBlock.getX(), chestBlock.getY(), chestBlock.getZ() ) );

        player.sendMessage( Shipments.getPrefix() + "Shipment chest created :D" );
    }

    @EventHandler
    public void onPlayerInteract( PlayerInteractEvent event )
    {
        if( event.getAction() != Action.RIGHT_CLICK_BLOCK )
        {
            return;
        }

        BlockState blockState = event.getClickedBlock().getState();

        if( !( blockState instanceof Sign ) )
        {
            return;
        }

        Player player = event.getPlayer();
        Sign sign = ( Sign ) blockState;

        if( ! sign.getLine( 1 ).equals( ChatColor.GREEN + "[Shipment]" ) )
        {
            return;
        }

        if ( ! player.hasPermission("shipments.ship") )
        {
            player.sendMessage( Shipments.getPrefix() + ChatColor.RED + "No permission!" );
            return;
        }

        Block chestBlock = getChestBlockBelow( sign.getBlock() );

        if( chestBlock == null )
        {
            player.sendMessage( Shipments.getPrefix() + ChatColor.RED + "No Chest Found!" );
            return;
        }

        ShipmentChest shipmentChest = new ShipmentChest( chestBlock.getWorld(), chestBlock.getX(), chestBlock.getY(), chestBlock.getZ() );

        if( !player.getUniqueId().equals( shipmentChest.getOwner().getUniqueId() ) )
        {
            player.sendMessage( Shipments.getPrefix() + ChatColor.RED + "Its a shipment chest, but its not yours!" );
            return;
        }

        if( !canPlayerAccessBlock( player, chestBlock ) )
        {
            player.sendMessage( Shipments.getPrefix() + ChatColor.RED + "Chest is locked by someone else!" );
            return;
        }

        double value = plugin.getShipmentManager().calculateValue( shipmentChest );

        if( value == 0)
        {
            player.sendMessage( Shipments.getPrefix() + "No shippable items :(" );
        }
        else
        {
            player.sendMessage( Shipments.getPrefix() + "Items worth " + ChatColor.WHITE + plugin.getEconomy().format( value ) + ChatColor.GREEN + " will be collected later today!" );
        }
    }

    private Block getChestBlockBelow( Block block )
    {
        Block blockBelow = block.getWorld().getBlockAt( block.getLocation().subtract( 0, 1, 0 ) );
        if( !blockBelow.getType().equals( Material.CHEST ) )
        {
            return null;
        }

        return blockBelow;
    }

    private boolean canPlayerAccessBlock( Player player, Block block )
    {
        PlayerInteractEvent lockedChestEvent = new PlayerInteractEvent( player, Action.RIGHT_CLICK_BLOCK, new ItemStack( Material.DIRT ), block, BlockFace.DOWN );
        plugin.getServer().getPluginManager().callEvent( lockedChestEvent );
        return !lockedChestEvent.isCancelled();
    }
}
