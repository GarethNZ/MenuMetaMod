package com.shawlyne.mc.MenuMetaMod;

import java.util.Date;

public class MetaModMenu {
	public String[] options;// text
	public String[] commands; // result
	private long sendTime; // used for timeout
	
	public MetaModMenu(String[] options, String[] commands){// throws Exception {
		this.options = options;
		this.commands = commands;
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

	public boolean validResponse(int response) {
		return (response < commands.length);
			
	}
	
}
