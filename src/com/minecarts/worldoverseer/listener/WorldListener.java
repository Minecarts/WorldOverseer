package com.minecarts.worldoverseer.listener;

import com.minecarts.worldoverseer.WorldOverseer;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class WorldListener extends org.bukkit.event.world.WorldListener{
    private WorldOverseer plugin;
    public WorldListener(WorldOverseer plugin){
        this.plugin = plugin;
    }

    //TODO: If a world is loaded after the server loads (eg it's created by multiverse) -- attempt to load any flags??

    @Override
    public void onWorldLoad(WorldLoadEvent event){
        plugin.log("world " + event.getWorld().getName() + " loaded");
    }

    @Override
    public void onWorldUnload(WorldUnloadEvent event){
        plugin.log("world " + event.getWorld().getName() + " unloaded");
    }

}

