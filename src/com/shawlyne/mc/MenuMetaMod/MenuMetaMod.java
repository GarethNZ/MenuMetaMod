package com.shawlyne.mc.MenuMetaMod;

// TODO: No idea how to make spout (server side) OPTIONAL
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * MenuMetaMod for Bukkit
 *
 * @author GarethNZ
 */
public class MenuMetaMod extends JavaPlugin {
    private final static MenuMetaModPlayerManager playerManager = new MenuMetaModPlayerManager();
	public static MenuMetaMod plugin;
	
    public static boolean debug = false;
    public static Logger log;
    protected FileConfiguration config;
    // Menus accessible by "/quick <String>"
    static HashMap<String,Menu> configuredMenus = new HashMap<String,Menu>(); // command,menu
    static Menu quickMenu; // default menu that responds to "/quick"

    public BukkitScheduler scheduler; 
    
    public MenuMetaMod()
    {
    	plugin = this;
    	log = Logger.getLogger("Minecraft");
    }
    
    
    public void onEnable() {
    	scheduler = getServer().getScheduler();
    	
    	// Initialize and read in the YAML file
		getDataFolder().mkdirs();
		//File yml = new File(getDataFolder(), "config.yml");
		config = getConfig();
		
		if( config != null )
		{
			debug = config.getBoolean("debug",false);
			if( debug )
			{
				log.log(Level.CONFIG, "[MenuMetaMod] Debug mode enabled");
			}
			// Load in the values from the configuration file
			//
			MemorySection menuConfigs = (MemorySection)config.get("menus");
			
			if( menuConfigs != null )
			{
				Set<String> menukeys = menuConfigs.getKeys(false);
				for(String title : menukeys)
				{
					MemorySection menudata = (MemorySection)config.get("menus."+title);
					String type = menudata.getString("type");
					String command = menudata.getString("command");
					if( command.startsWith("/") ) // remove starting '/'
						command = command.substring(1);
					MemorySection optionConfigs = (MemorySection)config.get("menus."+title+".options");
					Set <String> options = optionConfigs.getKeys(false);
					
					ArrayList<String> commands = new ArrayList<String>();
					for(String option : options)
					{
						String optionCommand = menudata.getString("options."+option);
						optionCommand = optionCommand.replaceAll("(;?)\\s*/", "$1"); // remove starting '/' (from potentially multiple commands
						if( debug )
							System.out.println("[MenuMetaMod] MenuItem: " + option + " - " + optionCommand);
						commands.add(optionCommand);
					}
					String[] opts = new String[1];
					opts = options.toArray(opts);
					String[] comms = new String[1];
					comms = commands.toArray(comms);
					if( type.equalsIgnoreCase("Menu") )
					{
						Menu menu = new Menu(title, opts, comms);
						if( quickMenu == null ) quickMenu = menu;
						configuredMenus.put(command, menu);
						if( debug )
							log.log(Level.INFO, "[MenuMetaMod] Menu " + title + " added, it will respond to the command '/qm "+ command+"'");
					}
					else if( type.equalsIgnoreCase("ValueMenu") )
					{
						String question = menudata.getString("question");
						ValueMenu menu = new ValueMenu(title, opts, comms, question);
						if( quickMenu == null ) quickMenu = menu;
						configuredMenus.put(command, menu);
						if( debug )
							log.log(Level.INFO, "[MenuMetaMod] ValueMenu " + title + " added, it will respond to the command '/qm "+ command+"'");
					}
					else
					{
						log.log(Level.WARNING, "[MenuMetaMod] Unknown menu type: " + type +". Menu " + title + " not added");
					}
					// TODO: Investigate programatically adding more commands to listen to
					// PluginDescriptionFile wangleDescription = new PluginDescriptionFile("MenuMetaMod", "0.4", "com.shawlyne.mc.MenuMetaMod.MenuMetaMod");
			    	// this.initialize(loader, server, description, dataFolder, file, classLoader)
				}
			}
			
			saveConfig();
		}
		else
			log.log(Level.WARNING, "[MenuMetaMod] Error accessing Config");

		
		
        // Register our events
        PluginManager pm = getServer().getPluginManager();
       
        pm.registerEvent(Event.Type.PLAYER_CHAT, playerManager, Priority.Monitor, this);
        pm.registerEvent(Event.Type.CUSTOM_EVENT, new MenuInputListener(), Event.Priority.Low, this);
        pm.registerEvent(Event.Type.CUSTOM_EVENT, new MenuScreenListener(), Event.Priority.Low, this);
        
        
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }
    public void onDisable() {
        // NOTE: All registered events are automatically unregistered when a plugin is disabled
    	
    	MenuMetaModPlayerManager.empty();
    }
    
    public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args) {
    		
    		if( command.getName().equalsIgnoreCase("qm") )
    		{
    			if( !(sender instanceof Player) )
    	    		return false;
    			Player player = (Player)sender;
    		
    			if( args.length == 1)
    			{
    				Menu menu = configuredMenus.get(args[0]);
    				if( menu == null )
    				{
    					System.out.println("[MenuMetaMod] Error no menu configured for : \""+ args[0]+"\"");
    					return false;
    				}
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
    public static void sendMenu(Player p, Menu menu)
    {
    	MenuMetaModPlayerManager.sendMenu(p, menu);
    }
}

