package me.nonit.shipments;

import java.util.*;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfigurationOptions;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Shipments extends JavaPlugin
{
    private static final String PREFIX = ChatColor.YELLOW + "[Shipments]" + ChatColor.GREEN + " ";

    private Economy economy;
    private Configuration fileConfiguration;

    private final ShipmentManager shipmentManager;

    public Shipments()
    {
        this.shipmentManager = new ShipmentManager( this );
    }

    public void onEnable()
    {
        if( ! setupEconomy() )
        {
            getLogger().info( "Vault Error :(" );
            getServer().getPluginManager().disablePlugin( this );
            return;
        }

        getServer().getPluginManager().registerEvents( new ShipmentListener( this ), this );

        fileConfiguration = getConfig();
        loadConfigs();

        getCommand( "shipments" ).setExecutor( new CommandHandler( this ) );
    }

    public void loadConfigs()
    {
        //Header
        ((YamlConfigurationOptions ) fileConfiguration.options()).header("Shipments Config " + this.getDescription().getVersion() + " by Melonking" );

        //Shippable items
        ConfigurationSection blockSection;

        if ( fileConfiguration.isConfigurationSection("blocks") )
        {
            blockSection = fileConfiguration.getConfigurationSection("blocks");
        }
        else
        {
            blockSection = fileConfiguration.createSection("blocks");
        }

        Iterator blockIter = blockSection.getValues(false).keySet().iterator();
        while( blockIter.hasNext() )
        {
            String name = (String)blockIter.next();

            if( fileConfiguration.getConfigurationSection( "blocks." + name ) != null && getConfig().getConfigurationSection( "blocks." + name ).getKeys( false ).isEmpty() )
            {
                fileConfiguration.set( "blocks." + name, null );
                saveConfig();
                continue;
            }

            if ( stringToShipmentItem( name ) == null)
            {
                log("Ignoring invalid block from config: " + name);
                blockIter.remove();
            }
            else
            {
                try
                {
                    Set<String> datas = this.getConfig().getConfigurationSection( "blocks." + name ).getKeys( false );
                    for( String data : datas )
                    {
                        materialIndex.put( name + ":" + data, fileConfiguration.getDouble( "blocks." + name + "." + data ) );
                    }
                }
                catch( NullPointerException e )
                {
                    materialIndex.put( name, fileConfiguration.getDouble( "blocks." + name ) );
                }
            }
        }

        //Chests
        ConfigurationSection chestSection;

        if ( fileConfiguration.isConfigurationSection("chests") )
        {
            chestSection = fileConfiguration.getConfigurationSection("chests");
        }
        else
        {
            chestSection = fileConfiguration.createSection("chests");
        }

        List chestDatas = chestSection.getList( "chest-locations" );
        for( Object chestData : chestDatas )
        {
            String chestDataString = (String) chestData;
            String[] chestDataArray = chestDataString.split( "#" ); //world, x, y, z, owner uuid

            String worldString = chestDataArray[0];
            int x = Integer.parseInt( chestDataArray[1] );
            int y = Integer.parseInt( chestDataArray[2] );
            int z = Integer.parseInt( chestDataArray[3] );
            String ownerUUID = chestDataArray[4];

            World world = getServer().getWorld( worldString );
            if( world == null )
            {
                continue;
            }

            Block chestBlock = world.getBlockAt( x, y, z );
            if( !chestBlock.getType().equals( Material.CHEST ) )
            {
                //Add mech to remove ded chests
                continue;
            }

            shipmentManager.addShipmentChest( new ShipmentChest( world, x, y, z ) );
        }

        //Save
        saveConfig();
    }

    public void reloadShipments()
    {
        reloadConfig();
        fileConfiguration = getConfig();
        materialIndex.clear();
        loadConfigs();
    }

    public static String getPrefix()
    {
        return PREFIX;
    }

    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    public Economy getEconomy() { return economy; }

    public ShipmentManager getShipmentManager() { return shipmentManager; }
}