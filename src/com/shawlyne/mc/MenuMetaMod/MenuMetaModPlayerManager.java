package com.shawlyne.mc.MenuMetaMod;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.shawlyne.mc.MenuMetaMod.Client.ClientMenu;

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

	public static HashMap<Player,MetaModMenu> playerMenus = new HashMap<Player,MetaModMenu>();
	//public static HashMap<Player,Integer> playerPage = new HashMap<Player,Integer>();
	//public static HashMap<Player,Boolean> playerClientMod = new HashMap<Player,Boolean>();
	
    
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
    
    /*public void setClientMod(Player player, boolean enabled)
    {
    	playerClientMod.put(player, new Boolean(enabled));
    }*/
    
    public static void onPlayerResponse(Player player, String response)
    {
    	MetaModMenu menu = null;
    	if( playerMenus.get(player) == null )
    		return;
    	menu = playerMenus.get(player);
    	
    	/*int page = 1;
		if( playerPage.get(player) != null )
		{
			page = playerPage.get(player).intValue();
			
		}*/
		
		ResponseStatus handled;
		if( menu instanceof ClientMenu )
			handled = ((ClientMenu)menu).handleResponse(player, response);
		else
			handled = menu.handleResponse(player, response);
		
		if( handled == ResponseStatus.NotHandled )
		{
			player.sendMessage("Invalid Option " + (response));
			// Resend menu?
			menu.sendPage(player, menu.getPage());
			//MenuMetaModPlayerManager.sendMenu(player, menu, page);
		}
		else if( handled == ResponseStatus.HandledFinished)
		{
			if( playerMenus.get(player) != menu )
			{
				// A new command must have changed the menu
				// Hopefully this is the correct behaviour
				if( MenuMetaMod.debug )
					MenuMetaMod.log.info("Hmmm... asynchronous / I dunno what order, but now the player has a new menu...");
			}
			else
			{
				playerMenus.remove(player); // menu finished
			}
		}
    }
    
    /**
     * Only used for SpoutCraft players (where Key events are sent)
     * @param player
     * @param response
     */
    public static void onPlayerKeyResponse(SpoutPlayer player, Keyboard k)
    {
    	String response = k.toString().replaceAll("KEY_", "");
		//System.out.println("Got KB response: " + response);
		onPlayerResponse(player,response);
    }

    /**
     * Sends a menu to a player
     * @param p - Player to send to 
     * @param menu - MetaModMenu to send 
     * @return sent successfully or not
     */
    public static boolean sendMenu(Player player, MetaModMenu menu)
    {
    	if( player instanceof SpoutPlayer )
    	{ 
    		SpoutPlayer sp = (SpoutPlayer)player;
    		if( sp.isSpoutCraftEnabled() && !(menu instanceof ClientMenu) )
    		{
	    		ClientMenu cMenu = new ClientMenu(menu.title, menu.options, menu.commands);
	    		System.out.println("Sending a ClientMenu");
	    		playerMenus.put(player, cMenu);
		    	return cMenu.sendPage((SpoutPlayer)player, 0);
    		}
    	}
    	
    	playerMenus.put(player, menu);
	    return menu.sendPage(player, 0);
    }
    
	public static void empty() {
		playerMenus.clear();
		//playerPage.clear();
		// Dont clear playerClientMod
	}
}

