package com.shawlyne.mc.MenuMetaMod;

import java.util.Date;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.InGameHUD;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.shawlyne.mc.MenuMetaMod.gui.QuickTextField;
import com.shawlyne.mc.MenuMetaMod.threads.QuestionSender;

public class ValueMenu extends Menu {
	static HashMap<Player,String[]> valuesPending = new HashMap<Player,String[]>(); // Store player + command which we will append the value to
	
	static HashMap<Player,QuickTextField> inputTextFields = new HashMap<Player,QuickTextField>(); // Store values as they come from SpoutPopup 
	
	
	public String valueQuestion;
	
	public ValueMenu(String title, String[] options, String[] commands, String vQuestion) {
		super(title, options, commands);
		valueQuestion = vQuestion;
		
		spoutResponse = true;
		
		
		
	}
	
	public boolean sendQuestionPopup(Player p)
	{
		SpoutPlayer player = (SpoutPlayer)p;
		InGameHUD main = player.getMainScreen();
		GenericPopup popup = new GenericPopup();
		widgets.clear();

		if( main.getActivePopup() != null )
		{
			player.closeActiveWindow();
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
		
		
		GenericLabel questionLabel = new GenericLabel(valueQuestion);
		questionLabel.setWidth(popupWidth);
		questionLabel.setHeight(20);
		questionLabel.setX(20);
		questionLabel.setY(40);
		widgets.add(questionLabel);

		
		QuickTextField inputValue = new QuickTextField(player);
		inputValue.setWidth(popupWidth);
		inputValue.setHeight(20);
		inputValue.setX(20);
		inputValue.setY(60);
		inputValue.setFocus(true);
		widgets.add(inputValue);
		
		inputTextFields.put(p, inputValue); 
		
		// Add a Send / Enter button
		GenericButton  sendButton = new GenericButton ("Send");
		sendButton.setWidth(popupWidth);
		sendButton.setHeight(20);
		sendButton.setX(20);
		sendButton.setY(90);
		widgets.add(sendButton);
		
			
		// Add an 'Exit page option'
		GenericLabel inputOption = new GenericLabel( "Escape to Cancel");
		inputOption.setWidth(popupWidth);
		inputOption.setHeight(20);
		inputOption.setX(20);
		inputOption.setY(120);
		widgets.add(inputOption);
		
		for (Widget widget : widgets) {
          popup.attachWidget(MenuMetaMod.plugin, widget);
        }
		main.attachPopupScreen(popup);
		
		
		
		sendTime = new Date().getTime();
    	return true;
	}
	
	public boolean sendQuestion(Player p)
	{
		SpoutPlayer player = (SpoutPlayer)p;
		player.sendMessage("Sending question " + valueQuestion);
		if( player.isSpoutCraftEnabled() )
		{
			return sendQuestionPopup(player);
		}
			
		player.sendMessage(valueQuestion);
		return true;
	}
	
	public ResponseStatus handleResponse(Player p, String r)
	{
		SpoutPlayer player = (SpoutPlayer)p;
		int optionOffset = 0;
		if( MenuMetaMod.debug )
			System.out.println("[ValueMenu] handleResponse: " + p.getDisplayName() + " - " + r);
		
		InGameHUD main = null;
		if( player.isSpoutCraftEnabled() )
		{
			main = player.getMainScreen();
			if( r.equalsIgnoreCase("ESCAPE") )
			{
				if( main.getActivePopup() != null )
				{
					ValueMenu.inputTextFields.remove(p);
					player.closeActiveWindow();
				}
				return ResponseStatus.HandledFinished;
			}
		}
		
		// Check if player is just responding to value question
		if( valuesPending.get(player) != null ) 
		{
			String value = r;
			
			// If constantly monitoring the keyboard :(
			/*  
			if( inputTextFields.get(player) != null )
			{
				if( r.equals("RETURN") ) 
				{
					// KeyBoard has sent enter... now use the stored value
					value = inputTextFields.get(player).getText();
					inputTextFields.remove(player);
				}
				else
				{
					System.out.println("Waiting for enter");
					return ResponseStatus.Handled; // But not finished
				}
			}*/
			
			String[] comArray = valuesPending.get(player);
			
			for(String command : comArray)
			{
				command = getCommand(player, command);
				command = command.replaceAll("\\*value\\*", value);
				if( MenuMetaMod.debug )
					player.sendMessage("performCommand: " + command);
				player.performCommand( command );
			}
			
			valuesPending.remove(player);
			
			if( player.isSpoutCraftEnabled() ) // Remove popup
			{
				main = player.getMainScreen();
				if( main.getActivePopup() != null )
				{
					ValueMenu.inputTextFields.remove(p);
					player.closeActiveWindow();
				}
			}
			
			return ResponseStatus.HandledFinished;
		}
		
		if( isNumber(r) )
		{
			if( player.isSpoutCraftEnabled() )
			{
				// Close the popup
				if( main.getActivePopup() != null )
				{
					ValueMenu.inputTextFields.remove(p);
					player.closeActiveWindow();
				}
				else
					return ResponseStatus.NotHandled; // No menu to handle (WTF)
			}
			
			int response = Integer.valueOf(r).intValue();
		
			// NOTE: Can't call super.handleResponse because we also need to add valuesPending :(
			// Normal MetaModMenu behaviour
			
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
			if( (optionOffset+response) > commands.length )
				return ResponseStatus.NotHandled;
			else
			{
				// Single command to array
				String[] comArray = {commands[optionOffset+response-1]};
				// Split Multiple Commands
				if( commands[optionOffset+response-1].contains(";") )
				{
					comArray = commands[optionOffset+response-1].split(";");
				}
				valuesPending.put(player, comArray);
				
				MenuMetaMod.plugin.scheduler.scheduleSyncDelayedTask(MenuMetaMod.plugin, new QuestionSender(player, this), 5);
				
				if( sendQuestion(player) )
					return ResponseStatus.Handled;
			}
		}
		
		return ResponseStatus.NotHandled;
	}

}
