package me.nonit.shipments;

import me.nonit.shipments.commands.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler implements CommandExecutor
{
    private final List<SubCommand> commands;

    public CommandHandler( Shipments plugin )
    {
        commands = new ArrayList<SubCommand>();

        commands.add( new HelpCommand() );
        commands.add( new ListCommand( plugin ) );
        commands.add( new ShipCommand( plugin ) );
        commands.add( new ReloadCommand( plugin ) );
        commands.add( new SetCommand( plugin ) );
        commands.add( new UnsetCommand( plugin ) );
    }

    public boolean onCommand( CommandSender sender, Command arg1, String arg2, String args[] )
    {
        String subCommand = "help";

        if( args.length > 0 )
        {
            subCommand = args[0];
        }

        for( SubCommand command : commands )
        {
            if( command.getName().equalsIgnoreCase( subCommand ) )
            {
                if( ! (sender instanceof Player) && ! command.isConsoleSafe() )
                {
                    sender.sendMessage( "This command is only for players!" );
                    return true;
                }

                if( ! sender.hasPermission( command.getPermission() ) )
                {
                    sender.sendMessage( Shipments.getPrefix() + "Sorry you don't have permission to do that!" );
                    return true;
                }

                command.onCommand( sender, arg1, arg2, args );
            }
        }

        return true;
    }
}