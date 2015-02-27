package me.nonit.shipments;

import java.io.IOException;
import java.util.*;

import net.milkbowl.vault.economy.Economy;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfigurationOptions;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Shipments extends JavaPlugin
{
    private static final String PREFIX = ChatColor.YELLOW + "[Shipments]" + ChatColor.GREEN + " ";

    private Economy economy;
    private HashMap<String, Double> materialIndex = new HashMap<String, Double>();
    private HashMap<String, ShipmentChest> agreement = new HashMap<String, ShipmentChest>();
    private Configuration fileConfiguration;

    public void onEnable()
    {
        if( ! setupEconomy() )
        {
            log( "Vault Error :(" );
            getServer().getPluginManager().disablePlugin( this );
            return;
        }

        getServer().getPluginManager().registerEvents( new ShipmentListener( this ), this );

        fileConfiguration = getConfig();
        loadConfigs();

        getCommand( "shipments" ).setExecutor( new CommandHandler( this ) );

        try
        {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        }
        catch (IOException e)
        {
            // Failed to submit the stats :-(
        }
    }

    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    public void loadConfigs()
    {
        ((YamlConfigurationOptions ) fileConfiguration.options()).header("Shipments Config " + this.getDescription().getVersion() + " - loyloy.io" );
        ConfigurationSection configurationSection;

        if ( fileConfiguration.isConfigurationSection("blocks") )
        {
            configurationSection = fileConfiguration.getConfigurationSection("blocks");
        }
        else
        {
            configurationSection = fileConfiguration.createSection("blocks");
        }

        Iterator iterator = configurationSection.getValues(false).keySet().iterator();
        while (iterator.hasNext())
        {
            String name = (String)iterator.next();

            if( fileConfiguration.getConfigurationSection( "blocks." + name ) != null && getConfig().getConfigurationSection( "blocks." + name ).getKeys( false ).isEmpty() )
            {
                fileConfiguration.set( "blocks." + name, null );
                saveConfig();
                continue;
            }

            if ( stringToShipmentItem( name ) == null)
            {
                log("Ignoring invalid block from config: " + name);
                iterator.remove();
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

        saveConfig();
    }

    public ShipmentItem stringToShipmentItem( String name )
    {
        short data = 0;

        if( name.contains( ":" ) )
        {
            String[] split = name.split( ":" );
            name = split[0];
            try
            {
                data = Short.parseShort( split[1] );
            }
            catch( NumberFormatException e )
            {
                return null;
            }
        }

        Material mat = Material.matchMaterial( name );
        if( mat == null )
        {
            return null;
        }

        return new ShipmentItem( mat, data );
    }

    public static String prettyName( String rawName )
    {
        String name = rawName.replace( '_', ' ' );
        name = WordUtils.capitalizeFully( name );
        name = name.replaceAll( "Harvestcraft ", "" );
        name = name.replaceAll( "Mocreatures ", "" );
        name = name.replaceAll( "item", "" );
        return name;
    }

    public void reloadShipments()
    {
        reloadConfig();
        fileConfiguration = getConfig();
        materialIndex.clear();
        loadConfigs();
    }

    public void log( String message )
    {
        getLogger().info( message );
    }

    public static String getPrefix()
    {
        return PREFIX;
    }

    public Economy getEconomy() { return economy; }

    public HashMap<String, Double> getMaterialIndex() { return materialIndex; }

    public HashMap<String, ShipmentChest> getAgreement() { return agreement; }
}