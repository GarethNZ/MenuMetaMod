package com.shawlyne.mc.MenuMetaMod;

import java.util.ArrayList;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.InGameHUD;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.shawlyne.mc.MenuMetaMod.threads.CommandRunner;

public class Menu {
	protected static String[] optionText = { // So index 0 shows "1" etc
		"1","2","3","4","5",
		"6","7","8","9","0"						    
	};
	
	
	public String title;
	public String[] options;// text
	public String[] commands; // result
	protected long sendTime; // used for timeout
	
	// For SpoutCraft Clients:
	public GenericTexture bgImage = null;
	public ArrayList<Widget> widgets = new ArrayList<Widget>();
	public GenericLabel menuTitle = null;
	
	
	public int pages;
	public int optionCount;
	protected int page;
	
	/**
	 * Extended classes should set to true if they have a custom spout response 
	 * true = disable keyboard listener
	 */
	public boolean spoutResponse = false;
	
	public Menu(String t, String[] o, String[] c){// throws Exception {
		title = t;
		options = o;
		commands = c;
		if( options.length != commands.length )
		{
			// TODO: throw ... 
			return;
		}
		
		
		optionCount = options.length;
		pages = 1;
		
		if( optionCount > 9 ) // 10 is exit / next page
		{
			int options = optionCount-9;
			pages += (options/8);
			if( options % 8 != 0 )
				pages++;
		}
		System.out.println(pages + " pages from " + optionCount + " options");
	}
	
	
	
	
	/*
	 * Has the menu faded away / anything??
	 * TODO: remove? not used anyway
	 * NOTE: Chat menu fades after ~8s. But still visible in player's chat log
	 */
	/*private boolean isExpired()
	{
		long now = new Date().getTime();
		now -= 30000; // 30s
		if( now > sendTime )
			return true;
		else
			return false;
	}*/
	
	public boolean sendPage(Player p, int pg)
	{
		SpoutPlayer player = (SpoutPlayer)p;
		player.sendMessage("Sending menu " + title);
		if( player.isSpoutCraftEnabled() )
		{
			return sendPopup(player, pg);
		}
		
		page = pg+1; // 0 --> Page 1 etc
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
		if( (optionCount - firstOption) < optionsToSend )
		{
			optionsToSend = optionCount - firstOption;
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
	
	public boolean sendPopup(Player p, int pg)
	{
		SpoutPlayer player = (SpoutPlayer)p;
		InGameHUD main = player.getMainScreen();
		GenericPopup popup = new GenericPopup();
		page = pg+1; // 0 --> Page 1 etc
		widgets.clear();

		if( main.getActivePopup() != null )
		{
			main = player.getMainScreen();
			player.closeActiveWindow();
			main = player.getMainScreen();
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
		if( (optionCount - firstOption) < optionsToSend )
		{
			optionsToSend = optionCount - firstOption;
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
			System.out.println(pages + " > " + page + " - NextPage button added");
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
			System.out.println(pages + " == " + page + " - Cancel button added");
		}
		
		for (Widget widget : widgets) {
          popup.attachWidget(MenuMetaMod.plugin, widget);
        }
		main.attachPopupScreen(popup);
		sendTime = new Date().getTime();
    	return true;
		
		
	}

	
	
	public ResponseStatus handleResponse(Player p, String r) // Numbered responses are 1 - 10
	{
		SpoutPlayer player = (SpoutPlayer)p;
		int optionOffset = 0;
		if( MenuMetaMod.debug )
			System.out.println("[Menu] handleResponse: " + r);
		
		InGameHUD main = null;
		if( player.isSpoutCraftEnabled() )
		{
			main = player.getMainScreen();
			if( r.equalsIgnoreCase("ESCAPE") )
			{
				if( main.getActivePopup() != null )
				{
					player.closeActiveWindow();
				}
				return ResponseStatus.HandledFinished;
			}
		}
		
		if( isNumber(r) )
		{
			if( player.isSpoutCraftEnabled() )
			{
				// Close the popup
				// Main set in 1st if SpoutCraft
				if( main.getActivePopup() != null )
				{
					player.closeActiveWindow();
				}
				else
					return ResponseStatus.NotHandled; // No menu to handle (WTF)
			}
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
				
				MenuMetaMod.plugin.scheduler.scheduleSyncDelayedTask(MenuMetaMod.plugin, new CommandRunner(player, comArray), 5);
				
				
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
    


	public static String getCommand(Player p, String c)
	{
		String command = c.replaceAll("\\*user\\*", p.getName());
		return command;
	}
	
}
