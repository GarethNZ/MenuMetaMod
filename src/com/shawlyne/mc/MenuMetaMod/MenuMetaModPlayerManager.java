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

	public static HashMap<Player,MetaModMenu> playerMenus = new HashMap<Player,MetaModMenu>();
	public static HashMap<Player,Integer> playerPage = new HashMap<Player,Integer>();
	public static HashMap<Player,Boolean> playerClientMod = new HashMap<Player,Boolean>();
	
	// For testing
	String[] options = {"This 1", "That 2", "A 3", "Extra 4", "Another 5",
			"This 6", "That 7", "A 8", "Extra 9", "Another 10",
			"This 11", "That 12", "A 13", "Extra 14", "Another 15",
			"This 16", "That 17", "A 18", "Extra 19", "Another 20",
	};
	String[] commands = {"Command 1", "Command 2", "Command 3", "Command 4","Command 5",
			"Command 6", "Command 7", "Command 8", "Command 9","Command 10",
			"Command 11", "Command 12", "Command 13", "Command 14","Command 15",
			"Command 16", "Command 17", "Command 18", "Command 19","Command 20"
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
    
    public void setClientMod(Player player, boolean enabled)
    {
    	playerClientMod.put(player, new Boolean(enabled));
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
		
		//System.out.println("Player responded to Menu: \""+menu.title+"\" Page: " + page+ " With: \'"+response+"\'");
    	
		ResponseStatus handled = menu.handleResponse(player, response, page);
		if( handled == ResponseStatus.NotHandled )
		{
			//System.out.println("Invalid Option " + (response));
			player.sendMessage("Invalid Option " + (response));
			// Resend menu?
			MenuMetaModPlayerManager.sendMenu(player, menu, page);
		}
		else if( handled == ResponseStatus.HandledFinished)
		{
			//System.out.println("HandledFinished");
			playerMenus.remove(player); // menu finished
			playerPage.remove(player);
		}
		/*// temp
		else
		{
			System.out.println("Handled but not finished");
		}
		//*/
    }

    /**
     * Sends a menu to a player
     * @param p - Player to send to 
     * @param menu - MetaModMenu to send 
     */
    public static void sendMenu(Player p, MetaModMenu menu)
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
    public static boolean sendMenu(Player player, MetaModMenu menu, int page)
    {
    	// Num. <OptionText>
    	// order is 1-9, 0
    	
    	playerMenus.put(player, menu);
    	playerPage.put(player, Integer.valueOf(page));
    	
    	if( page > menu.pages )
    		return false; // throw error?
    	
    	int optionsToSend = menu.options.length+1;
    	int firstOption = 0;
    	String startString = menu.title;
    	String endString = null;
		if( MenuMetaModPlayerManager.playerClientMod.get(player) != null )
		{
			startString = "##Menu_"+startString;
			endString = "##EndMenu";
		}
			
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
    	
    	player.sendMessage(startString);
    	int o = 0;
    	for(; o < optionsToSend ; o++)
    	{
    		player.sendMessage(optionText[o]+". "+menu.options[firstOption+o]);
    	}
    	// Add next / prev
    	if( page > 1 )
		{
			// Add a 'prev page option'
			//p.sendMessage(optionText[o]+". "+ChatColor.BLUE + "Prev Page");
    		player.sendMessage("9. "+ChatColor.BLUE + "Prev Page");
			o++;
		}
		if( menu.pages > page )
		{
			// Add a 'next page option'
			//p.sendMessage(optionText[o]+". "+ChatColor.BLUE + "Next Page");
			player.sendMessage("0. "+ChatColor.BLUE + "Next Page");
		}
		
		if( endString != null)
			player.sendMessage(endString);
    	return true;
    }

	public static void empty() {
		playerMenus.clear();
		playerPage.clear();
		// Dont clear playerClientMod
	}
}

