package me.nonit.shipments;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;

public class Utils
{
    public static ShipmentItem stringToShipmentItem( String name, double value )
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

        Material type = Material.matchMaterial( name );
        if( type == null )
        {
            return null;
        }

        return new ShipmentItem( type, data, value );
    }

    public static String prettyName( String rawName )
    {
        String name = rawName.replace( '_', ' ' );
        name = WordUtils.capitalizeFully( name );
        name = name.replaceAll( "item", "" );
        return name;
    }
}
