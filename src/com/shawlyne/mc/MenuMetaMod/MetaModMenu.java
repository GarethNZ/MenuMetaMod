package com.shawlyne.mc.MenuMetaMod;

import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MetaModMenu {
	private static String[] optionText = {
		"1","2","3","4","5",
		"6","7","8","9","0"						    
	};

	
	public String title;
	public String[] options;// text
	public String[] commands; // result
	private long sendTime; // used for timeout
	
	public int pages;
	public int optionCount;
	
	public MetaModMenu(String t, String[] o, String[] c){// throws Exception {
		title = t;
		options = o;
		commands = c;
		if( options.length != commands.length )
		{
			// TODO: throw ... 
			return;
		}
		
		
		optionCount = this.options.length;
		pages = 1;
		
		if( optionCount > 10 )
		{
			int options = optionCount-9;
			pages += (options/8);
			if( options % 8 != 0 )
				pages++;
		}
		
		//if( options.length != commands.length )
		//	throw new Exception("Array size lengths not equal");
		
		this.sendTime = new Date().getTime();
	}
	
	
	
	
	/*
	 * Has the menu faded away / anything??
	 * TODO: remove? not used anyway
	 * NOTE: Chat menu fades after ~8s. But still visible in player's chat log
	 */
	public boolean isExpired()
	{
		long now = new Date().getTime();
		now -= 30000; // 30s
		if( now > sendTime )
			return true;
		else
			return false;
	}
	
	public ResponseStatus handleResponse(Player player, int response, int page) // 1 - 10
	{
		int optionOffset = 0;
		
		if( pages > page && response == 10 )
		{
			// Next page
			send(player, page+1);
			return ResponseStatus.Handled;
		}
		else if( page > 1 && response == 9)
		{
			// Prev page
			send(player, page-1);
			return ResponseStatus.Handled;
		}
		
		
		if( response == 0 ) response = 10; // 0 = the tenth
		
		if( page > 1 )
		{
			optionOffset = 9;
			optionOffset += ((page-2)*8); // not first two
		}
		if( commands.length < (optionOffset+response-1) )
			return ResponseStatus.NotHandled;
		else
		{
			player.performCommand( commands[optionOffset+response-1] );
			return ResponseStatus.HandledFinished;
		}
	}




	public boolean send(Player player, int page) {
		if( page > pages )
    		return false; // throw error?
    	
    	int optionsToSend = options.length+1;
    	int firstOption = 0;
		
    	if( page > 1 )
		{
			firstOption = 9;
			firstOption += ((page-2)*8); // not first 2 pages
		}
    	
    	if( pages > 1 )
    	{
    		optionsToSend = (page==1)?9:8; // 9 for page 1, else 8
    		
    		// check not too many
    		if( (options.length - firstOption) < optionsToSend )
    		{
    			optionsToSend = options.length - firstOption;
    		}
    	}
    	
    	player.sendMessage("##Menu: " + title);
    	int o = 0;
    	for(; o < optionsToSend ; o++)
    	{
    		player.sendMessage(optionText[o]+". "+options[firstOption+o]);
    	}
    	// Add next / prev
    	if( page > 1 )
		{
			// Add a 'prev page option'
			//p.sendMessage(optionText[o]+". "+ChatColor.BLUE + "Prev Page");
    		player.sendMessage("9. "+ChatColor.BLUE + "Prev Page");
			o++;
		}
		if( pages > page )
		{
			// Add a 'next page option'
			//p.sendMessage(optionText[o]+". "+ChatColor.BLUE + "Next Page");
			player.sendMessage("0. "+ChatColor.BLUE + "Next Page");
		}
		player.sendMessage("##EndMenu: " + title);
    	return true;
		
	}
	
	
}
