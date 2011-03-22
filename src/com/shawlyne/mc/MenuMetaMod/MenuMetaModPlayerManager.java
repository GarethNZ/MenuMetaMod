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
	private static String[] optionText = {
			"1","2","3","4","5",
			"6","7","8","9","0"						    
	};
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
    	
    	if( pe.isCancelled() ) return;
    	
    	if( (menu = playerMenus.get(pe.getPlayer())) != null ) // if player has menu
    	{
    		// Maybe a response
    		try{
    			int response = Integer.parseInt(pe.getMessage()); // inputs 1-9,0
    			if( response == 0 ) response = 10; // 0 = the tenth
    			
    			int page = 1;
    			if( playerPage.get(pe.getPlayer()) != null )
    			{
    				page = playerPage.get(pe.getPlayer()).intValue();
    				
    			}
    			
    			if( menu.pages > page && response == 10 )
    			{
    				// Next page
    				sendMenu(pe.getPlayer(), menu, page+1);
    			}
    			else if( page > 1 && response == 9)
    			{
    				// Prev page
    				sendMenu(pe.getPlayer(), menu, page-1);
    			}
    			else
    			{
	    			String command = menu.getCommand(response, page);
	    			if( command == null )
	    			{
	    				pe.getPlayer().sendMessage("Invalid Option " + (response));
	    				// Resend menu?
	    				sendMenu(pe.getPlayer(), menu, page);
	    			}
	    			else
	    			{
	    				playerMenus.remove(pe.getPlayer()); // menu finished
	    				playerPage.remove(pe.getPlayer());
	    				pe.getPlayer().sendMessage("PerformCommand: " + command);
	    				pe.getPlayer().performCommand(command);
	    			}
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
     */
    public boolean sendMenu(Player p, MetaModMenu menu, int page)
    {
    	// Num. <OptionText>
    	// order is 1-9, 0
    	
    	playerMenus.put(p, menu);
    	playerPage.put(p, Integer.valueOf(page));
    	
    	if( page > menu.pages )
    		return false; // throw error?
    	
    	int optionsToSend = menu.options.length+1;
    	int firstOption = 0;
		
    	if( page > 1 )
		{
			firstOption = 9;
			firstOption += ((page-2)*8); // not first 2 pages
		}
    	
    	if( menu.pages > 1 )
    	{
    		optionsToSend = (page==1)?9:8; // 9 for page 1, else 8
    		
    		// check not too many
    		if( (menu.options.length - firstOption) < optionsToSend )
    		{
    			optionsToSend = menu.options.length - firstOption;
    		}
    	}
    	
    	p.sendMessage("##Menu: " + menu.title);
    	int o = 0;
    	for(; o < optionsToSend ; o++)
    	{
    		p.sendMessage(optionText[o]+". "+menu.options[firstOption+o]);
    	}
    	// Add next / prev
    	if( page > 1 )
		{
			// Add a 'prev page option'
			//p.sendMessage(optionText[o]+". "+ChatColor.BLUE + "Prev Page");
    		p.sendMessage("9. "+ChatColor.BLUE + "Prev Page");
			o++;
		}
		if( menu.pages > page )
		{
			// Add a 'next page option'
			//p.sendMessage(optionText[o]+". "+ChatColor.BLUE + "Next Page");
			p.sendMessage("0. "+ChatColor.BLUE + "Next Page");
		}
		
    	return true;
    }

	public void empty() {
		playerMenus.clear();
		playerPage.clear();
	}
}

