package com.shawlyne.mc.MenuMetaMod;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;

/**
 * Handle events for all Player related events
 * @author GarethNZ
 */
public class MenuMetaModPlayerManager extends PlayerListener {
    /*private final MenuMetaMod plugin;

    public MenuMetaModPlayerManager(MenuMetaMod instance) {
        plugin = instance;
    }*/
	HashMap<Player,MetaModMenu> playerMenus = new HashMap<Player,MetaModMenu>();
	
	// For testing
	String[] options = {"Cancel", "This One", "That Two", "A Three", "Extra Four"};
	String[] commands = {"?", "Command 1", "Command 2", "Command 3", "Command 4"};
	MetaModMenu TestMenu = new MetaModMenu( 
					options,
					commands
			);
	
    public void onPlayerJoin(PlayerEvent pe)
    {
    	//MenuMetaMod.sendMenu(pe.getPlayer(), TestMenu);
    }
    
    // End for testing
    
    /**
     * Handle response.. if it is a response to a menu
     * else ignore
     * TODO: Handle MCMenu response???
     */
    public void onPlayerChat(PlayerChatEvent pe)
    {
    	MetaModMenu menu = null;
    	if( (menu = playerMenus.get(pe.getPlayer())) != null ) // if player has menu
    	{
    		// Maybe a response
    		try{
    			int response = Integer.parseInt(pe.getMessage());
    			if( !menu.validResponse(response) )
    			{
    				pe.getPlayer().sendMessage(response +" is not a valid option");
    				// Resend menu?
    				// skip for now (menu still active)
    			}
    			else
    			{
    				playerMenus.remove(pe.getPlayer()); // menu finished
    				pe.getPlayer().sendMessage("PerformCommand: " + menu.commands[response]);
    				pe.getPlayer().performCommand(menu.commands[response]);
    			}
    			pe.setCancelled(true);
    			
    		}
    		catch(NumberFormatException e)
    		{
    			// not for me
    		}
    	}
    	
    	// For testing
    	if( pe.getMessage().equals("?") )
    	{
    		sendMenu(pe.getPlayer(),TestMenu);
    		pe.setCancelled(true);
    	}
    	// end testing
    }

    /**
     * Sends a menu to a player
     * TODO: If they have the MCMenu plugin.. send in format usable with that
     * Returns value selected
     * @param ItemValues
     * @return Option Selected. '1' -> array index 0 etc
     */
    public void sendMenu(Player p, MetaModMenu menu)
    {
    	playerMenus.put(p, menu);
    	p.sendMessage("Options: ");
    	for(int o = 0; o < menu.options.length; o++)
    	{
    		p.sendMessage((o)+". "+menu.options[o]);
    	}
    }

	public void empty() {
		playerMenus.clear();
	}
}

