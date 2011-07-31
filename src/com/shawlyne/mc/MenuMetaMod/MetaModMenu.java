package com.shawlyne.mc.MenuMetaMod;

import java.util.Date;

import org.bukkit.entity.Player;

public class MetaModMenu {
	
	
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
		
		if( optionCount > 9 ) // 10 is exit / next page
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
	
	// Expected Response: 1-9,0
	public ResponseStatus handleResponse(Player player, String r, int page) // 1 - 10
	{
		int optionOffset = 0;
		if( isNumber(r) )
		{
			int response = Integer.valueOf(r).intValue();
		
			if( response == 0 ) response = 10; // 0 = the tenth
			
			if( pages > page && response == 10 )
			{
				// Next page
				MenuMetaModPlayerManager.sendMenu(player, this, page+1);
				return ResponseStatus.Handled;
			}
			else if( page > 1 && response == 9)
			{
				// Prev page
				MenuMetaModPlayerManager.sendMenu(player, this, page-1);
				return ResponseStatus.Handled;
			}
			else if( pages == page && response == 10)
			{
				// Cancel Menu
				return ResponseStatus.Handled;
			}
			
			if( page > 1 )
			{
				optionOffset = 9;
				optionOffset += ((page-2)*8); // not first two
			}
			if( commands.length < (optionOffset+response-1) )
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
    public static boolean isNumber(String s)
	{
		return s.matches("[0-9]+");
	}
    



	
}
