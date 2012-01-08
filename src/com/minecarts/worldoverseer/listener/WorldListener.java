package com.minecarts.worldoverseer.listener;

import com.minecarts.worldoverseer.WorldOverseer;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class WorldListener extends org.bukkit.event.world.WorldListener{
    private WorldOverseer plugin;
    public WorldListener(WorldOverseer plugin){
        this.plugin = plugin;
    }

    @Override
    public void onWorldLoad(WorldLoadEvent event){
        plugin.log("world " + event.getWorld().getName() + " loaded");
    }

    @Override
    public void onWorldUnload(WorldUnloadEvent event){
        plugin.log("world " + event.getWorld().getName() + " unloaded");
    }
}

