package com.shawlyne.mc.MenuMetaMod.Client;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.InGameHUD;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.shawlyne.mc.MenuMetaMod.MenuMetaMod;
import com.shawlyne.mc.MenuMetaMod.ResponseStatus;

public class ClientValueMenu extends ClientMenu {
	static HashMap<Player,String[]> valuesPending = new HashMap<Player,String[]>(); // Store player + command which we will append the value to
	
	String valueQuestion = null;
	
	public ClientValueMenu(String t, String[] o, String[] c, String q){// throws Exception {
		super(t,o,c);
		valueQuestion = q;
	}

	public ResponseStatus handleResponse(Player p, String r, int page) 
	{
		SpoutPlayer player = (SpoutPlayer)p;
		int optionOffset = 0;

		// Check if player is just responding to value question
		if( valuesPending.get(player) != null )
		{
			//player.sendMessage("Got value: " + response);
			// Accept Multiple Commands
			String[] comArray = valuesPending.get(player);
			
			
			for(String command : comArray)
			{
				command = getCommand(player, command);
				command = command.replaceAll("*value*", r);
				if( MenuMetaMod.debug )
					player.sendMessage("performCommand: " + command);
				player.performCommand( command );
			}
			
			valuesPending.remove(player);
			return ResponseStatus.HandledFinished;
		}
		
		if( isNumber(r) ) // 'Valid response'
		{
			// Close the popup
			InGameHUD main = player.getMainScreen();
			if( main.getActivePopup() != null )
			{
				main.closePopup();
				System.out.println("[ClientMenu] close popup " + r);
				
			}
			else
				return ResponseStatus.NotHandled; // No menu to handle (WTF)
			
			
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
			if( commands.length < (optionOffset+response-1) )
				return ResponseStatus.NotHandled;
			else
			{
				/*if( MenuMetaModPlayerManager.playerClientMod.get(player) != null )
					player.sendMessage("##Value_" + valueQuestion);
				else*/
				player.sendMessage(valueQuestion);
				String[] comArray = new String[1];
				if( commands[optionOffset+response-1].contains(";") )
				{
					comArray = commands[optionOffset+response-1].split(";");
				}
				else
					comArray[0] = commands[optionOffset+response-1];
				valuesPending.put(player, comArray);
				return ResponseStatus.Handled;
			}
			
		}
		return ResponseStatus.NotHandled;
		
		
	}

}
