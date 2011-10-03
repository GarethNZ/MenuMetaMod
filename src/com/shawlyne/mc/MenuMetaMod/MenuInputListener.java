package com.shawlyne.mc.MenuMetaMod;


import org.getspout.spoutapi.event.input.InputListener;
import org.getspout.spoutapi.event.input.KeyReleasedEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.shawlyne.mc.MenuMetaMod.Client.ClientMenu;

public class MenuInputListener extends InputListener
{
	public MenuInputListener()
	{
		super();
	}

	public void onKeyReleasedEvent(KeyReleasedEvent event)
	{
		if( MenuMetaModPlayerManager.playerMenus.containsKey(event.getPlayer()) ) // Has menu open
		{
			//System.out.println("["+event.getPlayer().getDisplayName()+"]Got key press: "+event.getKey().toString());
			ClientMenu menu = (ClientMenu)MenuMetaModPlayerManager.playerMenus.get(event.getPlayer());
			menu.handleResponse(event.getPlayer(), event.getKey());
		}
			
	}
}
