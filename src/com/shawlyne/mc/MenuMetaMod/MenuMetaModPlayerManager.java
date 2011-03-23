package com.shawlyne.mc.MenuMetaMod;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;

/**
 * Handle events for all Player related events
 * @author GarethNZ
 */

/**
 * Response Handling:
 * 	Inputs are 1-9,0
 *  Corresponding to options 1-10
 * 
 * Multipage Handling:
 *  Next Page is always option 10
 *  Prev Page is always option 9
 */
public class MenuMetaModPlayerManager extends PlayerListener {
	/*private final MenuMetaMod plugin;

    public MenuMetaModPlayerManager(MenuMetaMod instance) {
        plugin = instance;
    }*/
	HashMap<Player,MetaModMenu> playerMenus = new HashMap<Player,MetaModMenu>();
	HashMap<Player,Integer> playerPage = new HashMap<Player,Integer>();
	
	// For testing
	String[] options = {"This 1", "That 2", "A 3", "Extra 4", "Another 5",
			"This 6", "That 7", "A 8", "Extra 9", "Another 10",
			"This 11", "That 12", "A 13", "Extra 14", "Another 15",
			"This 16", "That 17", "A 18", "Extra 19", "Another 20",
	};
	String[] commands = {"Command 1", "Command 2", "Command 3", "Command 4","Command 5",
			"Command 6", "Command 7", "Command 8", "Command 9","Command 10",
			"Command 1", "Command 2", "Command 3", "Command 4","Command 5",
			"Command 6", "Command 7", "Command 8", "Command 9","Command 10"
	};
	MetaModMenu TestMenu = new MetaModMenu( 
					"Test Menu",
					options,
					commands
			);
	
	MetaModValueMenu TestValueMenu = new MetaModValueMenu(
			"Test Value Menu",
			options,
			commands,
			"How many do you want?"
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
    	
    	if( pe.isCancelled() ) return;
    	
    	if( pe.getPlayer() == null) return;
    	
    	Player player = pe.getPlayer();
    	
    	if( playerMenus.get(player) != null ) // if player has menu
    	{
    		// Maybe a response
    		try{
    			int response = Integer.parseInt(pe.getMessage()); // inputs 1-9,0
    			onPlayerResponse(player,response);
    			
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
    		sendMenu(player,TestMenu);
    		pe.setCancelled(true);
    	}
    	if( pe.getMessage().equals(">") )
    	{
    		sendMenu(player,TestValueMenu);
    		pe.setCancelled(true);
    	}
    	
    	// end testing
    }
    
    public void onPlayerResponse(Player player, int response)
    {
    	MetaModMenu menu = null;
    	if( playerMenus.get(player) == null )
    		return;
    	menu = playerMenus.get(player);
    	
    	int page = 1;
		if( playerPage.get(player) != null )
		{
			page = playerPage.get(player).intValue();
			
		}
		
		ResponseStatus handled = menu.handleResponse(player, response, page);
		if( handled == ResponseStatus.NotHandled )
		{
			player.sendMessage("Invalid Option " + (response));
			// Resend menu?
			menu.send(player, page);
		}
		else if( handled == ResponseStatus.HandledFinished)
		{
			playerMenus.remove(player); // menu finished
			playerPage.remove(player);
		}
    }

    /**
     * Sends a menu to a player
     * @param p - Player to send to 
     * @param menu - MetaModMenu to send 
     */
    public void sendMenu(Player p, MetaModMenu menu)
    {
    	sendMenu(p, menu, 1);
    }
    
    /**
     * Sends a page of a menu to a player
     * @param p - Player to send to 
     * @param menu - MetaModMenu to send 
     * @param page - int Page number 1+
     * @return sent successfully
     */
    public boolean sendMenu(Player p, MetaModMenu menu, int page)
    {
    	// Num. <OptionText>
    	// order is 1-9, 0
    	
    	playerMenus.put(p, menu);
    	playerPage.put(p, Integer.valueOf(page));
    	
    	return menu.send(p, page);
    }

	public void empty() {
		playerMenus.clear();
		playerPage.clear();
	}
}

