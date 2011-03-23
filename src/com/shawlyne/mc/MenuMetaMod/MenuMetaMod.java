package com.shawlyne.mc.MenuMetaMod;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

/**
 * MenuMetaMod for Bukkit
 *
 * @author GarethNZ
 */
public class MenuMetaMod extends JavaPlugin {
    private final static MenuMetaModPlayerManager playerManager = new MenuMetaModPlayerManager();

    public void onEnable() {
        // Register our events
        PluginManager pm = getServer().getPluginManager();
       
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerManager, Priority.High, this);
        pm.registerEvent(Event.Type.PLAYER_CHAT, playerManager, Priority.High, this);

        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }
    public void onDisable() {
        // NOTE: All registered events are automatically unregistered when a plugin is disabled
    	
    	playerManager.empty();
    }
    
    public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args) {
    		
    		if (command.getName().equalsIgnoreCase("menu") )
    		{
    			if( !(sender instanceof Player) )
    	    		return false;
    			Player player = (Player)sender;
    		
    			if( args.length >= 1)
    			{
    				try{
    					int response = Integer.parseInt(args[0]); // inputs 1-9,0
    					playerManager.onPlayerResponse(player, response);
    					
    					return true;
		    		}
		    		catch(NumberFormatException e)
		    		{} 
    			}    			
    			player.sendMessage("Error in command format. Should be /menu <integer>");
    		}
    		return false;
    }
    
    public static void sendMenu(Player p, MetaModMenu menu)
    {
    	playerManager.sendMenu(p, menu);
    }
}

