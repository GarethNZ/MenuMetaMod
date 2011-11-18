package com.shawlyne.mc.MenuMetaMod;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

// Known implementation issue:
// If a user responds to a menu and the player selected has ALREADY left... the menu will (accidently) select the next player @ the same index (or fail) 
/**
 * Pick a player to use with a command
 * @author garethnz
 *
 */
public class PlayerMenu extends Menu {
	String[] commands;
	String title; 
	private Player[] players; // Players that were present when i sent it;
	
	public int pages;
	public int optionCount;
	protected int page;
	
	public PlayerMenu(String t, String[] c)
	{
		super(t, null, c);
		title = t;
		commands = c;
		
	}
	
	public boolean sendPage(Player player, int p)
	{
		// Update size of menu
		Server server = Bukkit.getServer();
		players = server.getOnlinePlayers();
		optionCount = players.length-1;
		pages = 1;
		if( optionCount > 9 ) // 10 is exit / next page
		{
			int options = optionCount-9;
			pages += (options/8);
			if( options % 8 != 0 )
				pages++;
		}
		
		page = p+1; // 0 --> Page 1 etc
    	if( page > pages )
    		return false; // throw error?
    	
    	int firstOption = 0;
    		
    	if( page > 1 )
		{
			firstOption = 9;
			firstOption += ((page-2)*8); // not first 2 pages
		}
    	
    	int optionsToSend = (page==1)?9:8; // 9 for page 1, else 8
    	// check not too many optionsToSend
		if( (optionCount - firstOption) < optionsToSend )
		{
			optionsToSend = optionCount - firstOption;
		}
    	
    	player.sendMessage(title);
    	int o = 0;
    	for(; o < optionsToSend ; o++)
    	{
    		player.sendMessage(optionText[o]+". "+players[firstOption+o].getName());
    	}
    	// Add next / prev / Cancel
    	if( page > 1 )
		{
			// Add a 'prev page option'
			player.sendMessage("9. "+ChatColor.BLUE + "Prev Page");
			o++;
		}
		if( pages > page )
		{
			// Add a 'next page option'
			player.sendMessage("0. "+ChatColor.BLUE + "Next Page");
		}
		if( pages == page )
		{
			// Add an 'Exit page option'
			player.sendMessage("0. "+ChatColor.BLUE + "Cancel");
		}
		
		return true;
	}
	
	// Expected Response: 1-9,0
	public ResponseStatus handleResponse(Player player, String r) // 1 - 10
	{
		int optionOffset = 0;
		if( isNumber(r) )
		{
			int response = Integer.valueOf(r).intValue();
		
			if( response == 0 ) response = 10; // 0 = the tenth
			
			if( pages > page && response == 10 )
			{
				// Next page
				sendPage(player, page+1);
				return ResponseStatus.Handled;
			}
			else if( page > 1 && response == 9)
			{
				// Prev page
				sendPage(player, page-1);
				return ResponseStatus.Handled;
			}
			else if( pages == page && response == 10)
			{
				// Cancel Menu
				return ResponseStatus.HandledFinished;
			}
			
			if( page > 1 )
			{
				optionOffset = 9;
				optionOffset += ((page-2)*8); // not first two
			}
			if( commands.length < (optionOffset+response) )
				return ResponseStatus.NotHandled;
			else
			{
				// Accept Multiple Commands
				String[] comArray = {commands[optionOffset+response-1]};
				// Split Multiple Commands
				if( commands[optionOffset+response-1].contains(";") )
				{
					comArray = commands[optionOffset+response-1].split(";");
				}
				
				for(String command : comArray)
				{
					command = getCommand(player, command);
					Player selectedPlayer = players[response-1];
					command = command.replaceAll("*player*", selectedPlayer.getName());
					if( MenuMetaMod.debug )
						player.sendMessage("Performing command " + command);
					player.performCommand( command );
				}
				
				return ResponseStatus.HandledFinished;
			}
		}
		return ResponseStatus.NotHandled;
	}
	
}
