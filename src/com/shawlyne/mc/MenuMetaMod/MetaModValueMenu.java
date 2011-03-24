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
			//player.sendMessage("Got value: " + response);
			String command = valuesPending.get(player);
			command += " " + response;
			player.sendMessage("performCommand: " + command);
			player.performCommand( command );
			valuesPending.remove(player);
			return ResponseStatus.HandledFinished;
		}
		
		
		// NOTE: Can't call super.handleResponse because we also need to add valuesPending :(
		// Normal MetaModMenu behaviour
		int optionOffset = 0;
		
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
		
		if( page > 1 )
		{
			optionOffset = 9;
			optionOffset += ((page-2)*8); // not first two
		}
		if( commands.length < (optionOffset+response-1) )
			return ResponseStatus.NotHandled;
		else
		{
			if( MenuMetaModPlayerManager.playerClientMod.get(player) != null )
				player.sendMessage("##Value_" + valueQuestion);
			else
				player.sendMessage(valueQuestion);
			valuesPending.put(player, commands[optionOffset+response-1]);
			return ResponseStatus.Handled;
		}
	}

}
