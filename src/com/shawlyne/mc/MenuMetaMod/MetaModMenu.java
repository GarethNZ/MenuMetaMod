package com.shawlyne.mc.MenuMetaMod;

import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MetaModMenu {
	protected static String[] optionText = { // So index 0 shows "1" etc
		"1","2","3","4","5",
		"6","7","8","9","0"						    
	};
	
	
	public String title;
	public String[] options;// text
	public String[] commands; // result
	protected long sendTime; // used for timeout
	
	public int pages;
	public int optionCount;
	protected int page;
	
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
		
		if( optionCount > 9 ) // 10 is exit / next page
		{
			int options = optionCount-9;
			pages += (options/8);
			if( options % 8 != 0 )
				pages++;
		}
		System.out.println(pages + " pages from " + this.options.length + " options");
	}
	
	
	
	
	/*
	 * Has the menu faded away / anything??
	 * TODO: remove? not used anyway
	 * NOTE: Chat menu fades after ~8s. But still visible in player's chat log
	 */
	private boolean isExpired()
	{
		long now = new Date().getTime();
		now -= 30000; // 30s
		if( now > sendTime )
			return true;
		else
			return false;
	}
	
	public boolean sendPage(Player player, int p)
	{
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
		if( (options.length - firstOption) < optionsToSend )
		{
			optionsToSend = options.length - firstOption;
		}
    	
    	player.sendMessage(title);
    	int o = 0;
    	for(; o < optionsToSend ; o++)
    	{
    		player.sendMessage(optionText[o]+". "+options[firstOption+o]);
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
					if( MenuMetaMod.debug )
						player.sendMessage("Performing command " + command);
					player.performCommand( command );
				}
				
				return ResponseStatus.HandledFinished;
			}
		}
		return ResponseStatus.NotHandled;
	}
	
	
	// helper for the responses
	public static boolean isNumber(char c)
	{
		return '0' >= c && c <= '9';
	}

	public static boolean isNumber(String s)
	{
		return s.matches("[0-9]+");
	}
	
	public int getPage()
	{
		return page-1; // 'Page 1' --> index 0
	}
    



	
}
