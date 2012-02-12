package com.minecarts.worldoverseer;

import org.bukkit.event.EventHandler;
import org.bukkit.Material;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class FlagListener implements Listener {

    private WorldOverseer plugin;

    public FlagListener(WorldOverseer plugin){
        this.plugin = plugin;
    }

//Blocks
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event){
        WorldLaborer laborer = plugin.getLaborer(event.getBlock().getWorld());
        if(laborer == null) return;

        //There is no onBlockGrow event, so we have to use the physics check??
        if(event.getBlock().getType() == Material.CROPS){
            if(laborer.isFlagSet(WorldFlag.CROP_GROW)){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockSpread(BlockSpreadEvent event){
        WorldLaborer laborer = plugin.getLaborer(event.getBlock().getWorld());
        if(laborer == null) return;
        if(event.getNewState().getBlock().getType() == Material.GRASS){
            if(laborer.isFlagSet(WorldFlag.GRASS_SPREAD)){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event){
        WorldLaborer laborer = plugin.getLaborer(event.getBlock().getWorld());
        if(laborer == null) return;
        if(laborer.isFlagSet(WorldFlag.BLOCK_PLACE) && !event.getPlayer().hasPermission("overseer.block.bypass")){
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event){
        WorldLaborer laborer = plugin.getLaborer(event.getBlock().getWorld());
        if(laborer == null) return;
        if(laborer.isFlagSet(WorldFlag.BLOCK_BREAK) && !event.getPlayer().hasPermission("overseer.block.bypass")){
            event.setCancelled(true);
        }
    }

//Weather
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWeatherChange(WeatherChangeEvent event){
        WorldLaborer laborer = plugin.getLaborer(event.getWorld());
        if(laborer == null) return;
        if(event.toWeatherState()){
            //It WILL be weathering
            if(laborer.isFlagSet(WorldFlag.STORMING)){
                event.setCancelled(true);
            }
        } else {
            //It's not going to be weathering anymore
            if(laborer.isFlagSet(WorldFlag.SUNNY)){
                event.setCancelled(true);
            }
        }
    }
}
