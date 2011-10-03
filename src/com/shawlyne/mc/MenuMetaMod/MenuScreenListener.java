package com.shawlyne.mc.MenuMetaMod;

import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.screen.ScreenListener;

public class MenuScreenListener extends ScreenListener
{
	// TODO: Maybe allow GUI button pushes as well as #s?
	public MenuScreenListener()
	{
		super();
	}

	public void onButtonClick(ButtonClickEvent event)
	{
		System.out.println("["+event.getPlayer().getDisplayName()+"]Got ButtonClick: "+event.getButton().getText());
		/*SpoutPlayer player = event.getPlayer();
    if( MenuMetaModPlayerManager.playerMenus.containsKey(event.getPlayer()) ) // Has menu open
		MenuMetaModPlayerManager.onPlayerResponse(event.getPlayer(), event.getButton().);
		 */
	}
}
