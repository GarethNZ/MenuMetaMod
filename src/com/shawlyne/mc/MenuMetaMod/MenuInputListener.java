package com.shawlyne.mc.MenuMetaMod;


import org.getspout.spoutapi.event.input.InputListener;
import org.getspout.spoutapi.event.input.KeyReleasedEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MenuInputListener extends InputListener
{
	public MenuInputListener()
	{
		super();
	}

	public void onKeyReleasedEvent(KeyReleasedEvent event)
	{
		System.out.println("["+event.getPlayer().getDisplayName()+"]Got key press: "+event.getKey().toString());
		
		if( MenuMetaModPlayerManager.playerMenus.containsKey(event.getPlayer()) ) // Has menu open
		{
			MenuMetaModPlayerManager.onPlayerKeyResponse(event.getPlayer(),event.getKey());
		}
		else if( event.getKey() == Keyboard.KEY_K ) {
			// Send default quick menu
			//System.out.println("[K Pushed] Sending quick menu");
			// Check if a popup is open
			SpoutPlayer player = (SpoutPlayer)event.getPlayer();
			System.out.println(" player.getActiveScreen() = " +  player.getActiveScreen());
			
			if( player.getActiveScreen() == ScreenType.GAME_SCREEN ) // No other screens active
			{
				MenuMetaModPlayerManager.sendMenu(event.getPlayer(), MenuMetaMod.quickMenu);
			}
		}
			
	}
}
