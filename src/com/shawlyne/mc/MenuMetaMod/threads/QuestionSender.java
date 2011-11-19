package com.shawlyne.mc.MenuMetaMod.threads;

import org.bukkit.entity.Player;

import com.shawlyne.mc.MenuMetaMod.ValueMenu;

public class QuestionSender implements Runnable {

	Player player;
	ValueMenu menu;
	
	public QuestionSender(Player p, ValueMenu m)
	{
		player = p;
		menu = m;
	}
	
	
	@Override
	public void run() {
		menu.sendQuestion(player);
	}

}
