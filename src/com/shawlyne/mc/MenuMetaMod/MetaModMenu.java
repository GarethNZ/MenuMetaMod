package com.shawlyne.mc.MenuMetaMod;

import java.util.Date;

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
	
	public String getCommand(int response, int page) // 1 - 10
	{
		int optionOffset = 0;
		if( page > 1 )
		{
			optionOffset = 9;
			optionOffset += ((page-2)*8); // not first two
		}
		if( commands.length < (optionOffset+response-1) )
			return null;
		else
			return commands[optionOffset+response-1];
	}
	
	
}
