package com.shawlyne.mc.MenuMetaMod;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class MetaModValueMenu extends MetaModMenu {
	static HashMap<Player,String> valuesPending = new HashMap<Player,String>(); // Store player + command which we will append the value to
	
	private String valueQuestion;
	public MetaModValueMenu(String title, String[] options, String[] commands, String vQuestion) {
		super(title, options, commands);
		valueQuestion = vQuestion;
	}
	
	
	public ResponseStatus handleResponse(Player player, int response, int page) 
	{
		// Check if player is just responding to value question
		if( valuesPending.get(player) != null )
		{
			player.sendMessage("Got value: " + response);
			String command = valuesPending.get(player);
			command += " " + response;
			player.sendMessage("performCommand: " + command);
			player.performCommand( command );
			valuesPending.remove(player);
			return ResponseStatus.HandledFinished;
		}
		
		
		// Normal MetaModMenu behaviour
		return super.handleResponse(player, response, page);
	}

}
