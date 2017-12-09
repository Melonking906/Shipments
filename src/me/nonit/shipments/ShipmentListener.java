package me.nonit.shipments;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
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

        if( !event.getPlayer().hasPermission("shipments.place") )
        {
            return;
        }

        String line0 = event.getLine(0);
        line0 = line0.replaceAll("[^\\x00-\\x7F]", "");

        String line1 = event.getLine(1);
        line1 = line1.replaceAll("[^\\x00-\\x7F]", "");

        if ( (line0.equalsIgnoreCase("[shipment]") || line0.equalsIgnoreCase("[shipments]"))
                || (line1.equalsIgnoreCase("[shipment]") || line1.equalsIgnoreCase("[shipments]")) )
        {
            event.setLine( 0, "");
            event.setLine( 1, ChatColor.GREEN + "[Shipment]" );
            event.getPlayer().sendMessage( Shipments.getPrefix() + "Shipment sign created :D" );
        }
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

        if( player.getWorld().getBlockAt( blockState.getLocation().subtract( 0.0D, 1.0D, 0.0D ) ).getType() != Material.CHEST )
        {
            player.sendMessage( Shipments.getPrefix() + ChatColor.RED + "No Chest Found!" );
            return;
        }

        Chest chest = ( Chest ) player.getWorld().getBlockAt( event.getClickedBlock().getLocation().subtract( 0.0D, 1.0D, 0.0D ) ).getState();

        PlayerInteractEvent pEvent = new PlayerInteractEvent( player, Action.RIGHT_CLICK_BLOCK, event.getItem(), chest.getBlock(), BlockFace.DOWN );
        plugin.getServer().getPluginManager().callEvent( pEvent );

        if( pEvent.isCancelled() )
        {
            return;
        }

        double x = 0.0D;

        ItemStack[] itemStacks;
        int contents = (itemStacks = chest.getInventory().getContents()).length;

        for (int i = 0; i < contents; i++)
        {
            ItemStack itemStack = itemStacks[i];
            if (itemStack != null)
            {
                String found = null;

                for (String key : plugin.getMaterialIndex().keySet())
                {
                    ShipmentItem vi = plugin.stringToShipmentItem( key );
                    if (vi != null)
                    {
                        if ((vi.getMat().toString().equals(itemStack.getType().toString())) && ( itemStack.getDurability() == vi.getData() ))
                        {
                            found = key;
                            break;
                        }
                    }
                }

                if( found != null )
                {
                    double price = plugin.getMaterialIndex().get(found) * itemStack.getAmount();
                    x += price;
                }
            }
        }

        if( x == 0.0D )
        {
            player.sendMessage( Shipments.getPrefix() + "No shippable items :(" );
        }
        else
        {
            player.sendMessage( Shipments.getPrefix() + "Total Value: " + x + ChatColor.WHITE + " do you want to ship? " + ChatColor.YELLOW + "/sh ship" );
            plugin.getAgreement().put( player.getName(), new ShipmentChest( plugin, chest ) );
        }
    }
}
