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
       
        pm.registerEvent(Event.Type.PLAYER_CHAT, playerManager, Priority.Monitor, this);

        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }
    public void onDisable() {
        // NOTE: All registered events are automatically unregistered when a plugin is disabled
    	
    	MenuMetaModPlayerManager.empty();
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
    				if( args[0].equalsIgnoreCase("modinstalled") )
    				{
		    			playerManager.setClientMod(player,true);
		    			return true;
    				}
    				try{
    					int response = Integer.parseInt(args[0]); 
    					playerManager.onPlayerResponse(player, response);
    					
    					return true;
		    		}
		    		catch(NumberFormatException e)
		    		{
		    			
		    		} 
		    		
    			}    			
    			player.sendMessage("Error in command format. Should be /menu <integer>");
    		}
    		return false;
    }
    
    /**
     * Wraps MenuMetaModPlayerManager.sendMenu so we can have a nicer API
     * TODO: Move function to here cause of static vars anyway?
     * Description: Will show the first page if there are > 1 page.
     * Additional pages handled within MenuMetaModPlayerManager
     * @param p - Player to send to
     * @param menu - The MetaModMenu to show 
     */
    public static void sendMenu(Player p, MetaModMenu menu)
    {
    	MenuMetaModPlayerManager.sendMenu(p, menu);
    }
}

