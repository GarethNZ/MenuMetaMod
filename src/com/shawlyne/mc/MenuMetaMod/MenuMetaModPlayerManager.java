package com.shawlyne.mc.MenuMetaMod;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
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
    			onPlayerResponse(player,pe.getMessage());
    			
    			pe.setCancelled(true);
    			
    		}
    		catch(NumberFormatException e)
    		{
    			// not for me
    		}
    	}
    }
    
    public void setClientMod(Player player, boolean enabled)
    {
    	playerClientMod.put(player, new Boolean(enabled));
    }
    
    public void onPlayerResponse(Player player, String response)
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
			MenuMetaModPlayerManager.sendMenu(player, menu, page);
		}
		else if( handled == ResponseStatus.HandledFinished)
		{
			//System.out.println("HandledFinished");
			if( playerMenus.get(player) != menu )
			{
				if( MenuMetaMod.debug )
					MenuMetaMod.log.info("Hmmm... asynchronous / I dunno what order, but now the player has a new menu...");
			}
			else
			{
				playerMenus.remove(player); // menu finished
				playerPage.remove(player);
			}
		}
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
    	}
    	
    	// check not too many
		if( (menu.options.length - firstOption) < optionsToSend )
		{
			optionsToSend = menu.options.length - firstOption;
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

