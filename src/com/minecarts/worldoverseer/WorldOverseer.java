package com.minecarts.worldoverseer;

import com.minecarts.worldoverseer.listener.WorldListener;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

public class WorldOverseer extends org.bukkit.plugin.java.JavaPlugin {
    public void onEnable(){
        PluginManager pm = getServer().getPluginManager();
        PluginDescriptionFile pdf = getDescription();

        WorldListener worldListener = new WorldListener(this);

        pm.registerEvent(Event.Type.WORLD_LOAD,worldListener,Event.Priority.Monitor,this);
        pm.registerEvent(Event.Type.WORLD_UNLOAD,worldListener,Event.Priority.Monitor,this);

        log("[" + pdf.getName() + "] version " + pdf.getVersion() + " enabled.");
    }

    public void onDisable(){

    }

    public static void log(String msg){
        System.out.println("WorldOverseer> " + msg);
    }
}
