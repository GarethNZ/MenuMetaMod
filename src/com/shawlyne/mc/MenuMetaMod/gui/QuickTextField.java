package com.shawlyne.mc.MenuMetaMod.gui;

import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.GenericTextField;

import com.shawlyne.mc.MenuMetaMod.MenuMetaModPlayerManager;

public class QuickTextField extends GenericTextField {

	Player player;
	
	public QuickTextField(Player p)
	{
		player = p;
	}
	
	public void onTypingFinished()
	{
		//System.out.println("QuickTextField onTypingFinished " + this.getText());
		
		MenuMetaModPlayerManager.onPlayerResponse(player, this.getText());
	}
}
