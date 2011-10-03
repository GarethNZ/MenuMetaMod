package com.shawlyne.mc.MenuMetaMod.Client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.InGameHUD;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.InGameHUD;
import org.getspout.spoutapi.gui.InGameScreen;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.shawlyne.mc.MenuMetaMod.MenuMetaMod;
import com.shawlyne.mc.MenuMetaMod.MenuMetaModPlayerManager;
import com.shawlyne.mc.MenuMetaMod.MetaModMenu;
import com.shawlyne.mc.MenuMetaMod.ResponseStatus;

public class ClientMenu extends MetaModMenu  {
	
	//InGameScreen clientScreen;
	//int playerId;
	//
	public GenericTexture bgImage = null;
	public ArrayList<Widget> widgets = new ArrayList<Widget>();
	public GenericLabel menuTitle = null;
	
	public ClientMenu(String t, String[] o, String[] c){// throws Exception {
		super(t,o,c);
	}

	
	public boolean sendPage(SpoutPlayer player, int p)
	{
		InGameHUD main = player.getMainScreen();
		GenericPopup popup = new GenericPopup();
		page = p;
		widgets.clear();

		if( main.getActivePopup() != null )
		{
			main.closePopup();
		}
		/*bgImage.setAnchor(WidgetAnchor.TOP_LEFT);
		bgImage.setWidth(main.getWidth() / 3);
		bgImage.setHeight(main.getHeight());
		bgImage.setPriority(RenderPriority.Highest);
		widgets.add(bgImage.setPriority);*/

		menuTitle = new GenericLabel();
		menuTitle.setText(title);
		menuTitle.setWidth(title.length() * 5);
		menuTitle.setHeight(5);
		menuTitle.setX(11);
		menuTitle.setY(20);//bgImage.getHeight() / 10);
		widgets.add(menuTitle);
		
		int popupWidth = title.length() * 5;
		
		
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
    	
    	int o = 0;
    	for(; o < optionsToSend ; o++)
    	{
    		GenericLabel inputOption = new GenericLabel(optionText[o]+". "+options[firstOption+o]);
			inputOption.setWidth(popupWidth);
			inputOption.setHeight(20);
			inputOption.setX(20);
			inputOption.setY(20 * (o + 2));
			widgets.add(inputOption);
    	}
    	// Add next / prev / Cancel
    	if( page > 1 )
		{
			// Add a 'prev page option'
			GenericLabel inputOption = new GenericLabel("9. "+ChatColor.BLUE + "Prev Page");
			inputOption.setWidth(popupWidth);
			inputOption.setHeight(20);
			inputOption.setX(20);
			inputOption.setY(20 * (o + 2));
			widgets.add(inputOption);
			o++;
		}
		if( pages > page )
		{
			// Add a 'next page option'
			GenericLabel inputOption = new GenericLabel("0. "+ChatColor.BLUE + "Next Page");
			inputOption.setWidth(popupWidth);
			inputOption.setHeight(20);
			inputOption.setX(20);
			inputOption.setY(20 * (o + 2));
			widgets.add(inputOption);
		}
		if( pages == page )
		{
			// Add an 'Exit page option'
			GenericLabel inputOption = new GenericLabel("0. "+ChatColor.BLUE + "Cancel");
			inputOption.setWidth(popupWidth);
			inputOption.setHeight(20);
			inputOption.setX(20);
			inputOption.setY(20 * (o + 2));
			widgets.add(inputOption);
		}
		
		for (Widget widget : widgets) {
          popup.attachWidget(MenuMetaMod.plugin, widget);
        }
		main.attachPopupScreen(popup);
		sendTime = new Date().getTime();
    	return true;
		
		
	}

	public ResponseStatus handleResponse(SpoutPlayer player, Keyboard k)
	{
		String response = k.toString().replaceAll("KEY_", "");
		//System.out.println("Got KB response: " + response);
		return handleResponse(player,response);
	}
	
	public ResponseStatus handleResponse(SpoutPlayer player, String r) // 1 - 10
	{
		int optionOffset = 0;
		
		if( isNumber(r) ) // 'Valid response'
		{
			// Close the popup
			InGameHUD main = player.getMainScreen();
			if( main.getActivePopup() != null )
			{
				main.closePopup();
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
}
