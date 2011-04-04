package com.shawlyne.mc.MenuMetaMod;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

/**
 * MenuMetaMod for Bukkit
 *
 * @author GarethNZ
 */
public class MenuMetaMod extends JavaPlugin {
    private final static MenuMetaModPlayerManager playerManager = new MenuMetaModPlayerManager();
    protected final Logger log;
    Configuration config;
    // Menus accessible by "/quick <String>"
    HashMap<String,MetaModMenu> configuredMenus = new HashMap<String,MetaModMenu>(); // command,menu
    MetaModMenu quickMenu; // default menu that responds to "/quick" 
    
    public MenuMetaMod()
    {
    	log = Logger.getLogger("Minecraft");
    	
    }
    
    
    public void onEnable() {
    	// Read config here so re-enable reloads it
    	
    	// Initialize and read in the YAML file
		getDataFolder().mkdirs();
		File yml = new File(getDataFolder(), "config.yml");
		config = getConfiguration();
		
		if (!yml.exists())
		{
			try {
				yml.createNewFile();
				log.info("Created an empty file " + getDataFolder() +"/config.yml, please edit it!");
				config.setProperty("toughblocks", null);
				config.save();
			} catch (IOException ex){
				log.warning(getDescription().getName() + ": could not generate config.yml. Are the file permissions OK?");
			}
		}
		
		if( config != null )
		{
			// Load in the values from the configuration file
			List <String> menukeys = config.getKeys("menus");
			if( menukeys != null )
			{
				for(String title : menukeys)
				{
					ConfigurationNode menudata = config.getNode("menus."+title);
					String type = menudata.getString("type");
					String command = menudata.getString("command");
					if( command.startsWith("/") ) // remove starting '/'
						command = command.substring(1);
					List <String> options = menudata.getKeys("options");
					
					if( options != null && options.size() > 0 )
					{
						ArrayList<String> commands = new ArrayList<String>();
						for(String option : options)
						{
							String optionCommand = menudata.getString("options."+option);
							if( optionCommand.startsWith("/") ) // remove starting '/'
								optionCommand = optionCommand.substring(1);
							System.out.println("MenuItem: " + option + " - " + optionCommand);
							commands.add(optionCommand);
						}
						String[] opts = new String[1];
						opts = options.toArray(opts);
						String[] comms = new String[1];
						comms = commands.toArray(comms);
						if( type.equalsIgnoreCase("Menu") )
						{
							MetaModMenu menu = new MetaModMenu(title, opts, comms);
							if( quickMenu == null ) quickMenu = menu;
							configuredMenus.put(command, menu);
							log.log(Level.INFO, "Menu " + title + " added, it will respond to the command '/quick "+ command+"'");
						}
						else if( type.equalsIgnoreCase("ValueMenu") )
						{
							String question = menudata.getString("question");
							MetaModValueMenu menu = new MetaModValueMenu(title, opts, comms, question);
							if( quickMenu == null ) quickMenu = menu;
							configuredMenus.put(command, menu);
							log.log(Level.INFO, "Menu " + title + " added, it will respond to the command '/quick "+ command+"'");
						}
						else
						{
							log.log(Level.WARNING, "Unknown menu type: " + type +". Menu " + title + " not added");
						}
						// TODO: Investigate programatically adding more commands to listen to
						// PluginDescriptionFile wangleDescription = new PluginDescriptionFile("MenuMetaMod", "0.4", "com.shawlyne.mc.MenuMetaMod.MenuMetaMod");
				    	// this.initialize(loader, server, description, dataFolder, file, classLoader)
					}
					
				}
			}
			
		}
		else
			System.out.println("Error accessing Config");

		
		
        // Register our events
        PluginManager pm = getServer().getPluginManager();
       
        pm.registerEvent(Event.Type.PLAYER_CHAT, playerManager, Priority.Monitor, this);

        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }
    public void onDisable() {
        // NOTE: All registered events are automatically unregistered when a plugin is disabled
    	
    	MenuMetaModPlayerManager.empty();
    }
    
    public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args) {
    		
    		if (command.getName().equalsIgnoreCase("menu") )
    		{
    			if( !(sender instanceof Player) )
    	    		return false;
    			Player player = (Player)sender;
    		
    			if( args.length >= 1)
    			{
    				if( args[0].equalsIgnoreCase("modinstalled") )
    				{
		    			playerManager.setClientMod(player,true);
		    			player.sendMessage(ChatColor.AQUA+" MenuMod detected");
		    			return true;
    				}
    				try{
    					int response = Integer.parseInt(args[0]); 
    					playerManager.onPlayerResponse(player, response);
    					
    					return true;
		    		}
		    		catch(NumberFormatException e)
		    		{
		    			
		    		} 
		    		
    			}    			
    			player.sendMessage("Error in command format. Should be /menu <integer>");
    		}
    		else if( command.getName().equalsIgnoreCase("quick") )
    		{
    			if( !(sender instanceof Player) )
    	    		return false;
    			Player player = (Player)sender;
    		
    			if( args.length == 1)
    			{
    				MetaModMenu menu = configuredMenus.get(args[0]);
    				if( menu == null )
    					return false;
    				MenuMetaModPlayerManager.sendMenu(player, menu);
    				return true;
    			}
    			else if( quickMenu != null)
    			{
    				// Default menu
    				MenuMetaModPlayerManager.sendMenu(player, quickMenu);
    				return true;
    			}
    		}
    		return false;
    }
    
    /**
     * Wraps MenuMetaModPlayerManager.sendMenu so we can have a nicer API
     * TODO: Move function to here cause of static vars anyway?
     * Description: Will show the first page if there are > 1 page.
     * Additional pages handled within MenuMetaModPlayerManager
     * @param p - Player to send to
     * @param menu - The MetaModMenu to show 
     */
    public static void sendMenu(Player p, MetaModMenu menu)
    {
    	MenuMetaModPlayerManager.sendMenu(p, menu);
    }
}

