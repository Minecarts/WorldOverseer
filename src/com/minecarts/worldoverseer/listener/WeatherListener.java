package com.minecarts.worldoverseer.listener;

import com.minecarts.worldoverseer.WorldFlag;
import com.minecarts.worldoverseer.WorldLaborer;
import com.minecarts.worldoverseer.WorldOverseer;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.security.PublicKey;

public class WeatherListener extends org.bukkit.event.weather.WeatherListener{
    private WorldOverseer plugin;
    public WeatherListener(WorldOverseer plugin){
        this.plugin = plugin;
    }
    
    @Override
    public void onWeatherChange(WeatherChangeEvent event){
        if(event.isCancelled()) return;

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
