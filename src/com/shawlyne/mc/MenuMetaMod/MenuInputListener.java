package com.shawlyne.mc.MenuMetaMod;


import org.getspout.spoutapi.event.input.InputListener;
import org.getspout.spoutapi.event.input.KeyReleasedEvent;
import org.getspout.spoutapi.keyboard.Keyboard;

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
			System.out.println("["+event.getPlayer().getDisplayName()+"]Got key press: "+event.getKey().toString());
			MenuMetaModPlayerManager.onPlayerKeyResponse(event.getPlayer(),event.getKey());
		}
		else if( event.getKey() == Keyboard.KEY_K ) {
			// Send default quick menu
			System.out.println("[K Pushed] Sending quick menu");
			MenuMetaModPlayerManager.sendMenu(event.getPlayer(), MenuMetaMod.quickMenu);
		}
			
	}
}
