package com.shawlyne.mc.MenuMetaMod.threads;

import org.bukkit.entity.Player;

import com.shawlyne.mc.MenuMetaMod.Menu;
import com.shawlyne.mc.MenuMetaMod.MenuMetaMod;

public class CommandRunner implements Runnable {

	Player player;
	String[] commands;
	
	public CommandRunner(Player p, String[] cs)
	{
		player = p;
		commands = cs;
	}
	
	
	@Override
	public void run() {
		for(String command : commands)
		{
			command = Menu.getCommand(player, command);
			if( MenuMetaMod.debug )
				player.sendMessage("Performing command " + command);
			player.performCommand( command );
		}

	}

}
