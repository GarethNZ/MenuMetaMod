package com.shawlyne.mc.MenuMetaMod;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

/**
 * MenuMetaMod for Bukkit
 *
 * @author GarethNZ
 */
public class MenuMetaMod extends JavaPlugin {
    private final static MenuMetaModPlayerManager playerManager = new MenuMetaModPlayerManager();

    public void onEnable() {
        // Register our events
        PluginManager pm = getServer().getPluginManager();
       
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerManager, Priority.High, this);
        pm.registerEvent(Event.Type.PLAYER_CHAT, playerManager, Priority.High, this);

        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }
    public void onDisable() {
        // NOTE: All registered events are automatically unregistered when a plugin is disabled
    	
    	playerManager.empty();
    }
    
    public boolean isDebugging(final Player player) {
        /*if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }*/
        return true;
    }

    public void setDebugging(final Player player, final boolean value) {
        //debugees.put(player, value);
    }
    
    public static void sendMenu(Player p, MetaModMenu menu)
    {
    	playerManager.sendMenu(p, menu);
    }
}

