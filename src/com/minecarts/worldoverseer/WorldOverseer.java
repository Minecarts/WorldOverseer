package com.minecarts.worldoverseer;

import com.minecarts.dbquery.DBQuery;
import com.minecarts.worldoverseer.listener.BlockListener;
import com.minecarts.worldoverseer.listener.WeatherListener;
import com.minecarts.worldoverseer.listener.WorldListener;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

import java.text.MessageFormat;
import java.util.HashMap;

public class WorldOverseer extends org.bukkit.plugin.java.JavaPlugin {
    private DBQuery dbq;
    private HashMap<World, WorldLaborer> worlds = new HashMap<World, WorldLaborer>();

    public void onEnable(){
        PluginManager pm = getServer().getPluginManager();
        PluginDescriptionFile pdf = getDescription();

        dbq = (DBQuery) getServer().getPluginManager().getPlugin("DBQuery");

        WorldListener worldListener = new WorldListener(this);
        WeatherListener weatherListener = new WeatherListener(this);
        BlockListener blockListener = new BlockListener(this);

        pm.registerEvent(Event.Type.WORLD_LOAD,worldListener,Event.Priority.Monitor,this);
        pm.registerEvent(Event.Type.WORLD_UNLOAD,worldListener,Event.Priority.Monitor,this);
        pm.registerEvent(Event.Type.WEATHER_CHANGE,weatherListener,Event.Priority.High,this); //Have to listen on high because multiverse doesn't check for canceled events
        pm.registerEvent(Event.Type.BLOCK_BREAK,blockListener,Event.Priority.Low,this);
        pm.registerEvent(Event.Type.BLOCK_PLACE,blockListener,Event.Priority.Low,this);

        //Handle any existing worlds
        for (World world : Bukkit.getWorlds()) {
            handleWorldFlags(world);
            try {
                Thread.sleep(500); //Throttle the queries a bit, TODO, change this to be async so it doesn't add to startup time?
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        //Start the time controlling thread
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
            public void run() {
                for (World world : worlds.keySet()) {
                    //Day has precedence over night, if both flags are set
                    if (worlds.get(world).isFlagSet(WorldFlag.NIGHT)) {
                        world.setTime(6000); //Is this safe, do we need to world.addTime(world.getTime() + X)?
                    } else if (worlds.get(world).isFlagSet(WorldFlag.DAY)) {
                        //Disable Day, always night
                        world.setTime(18000);
                    }
                }
            }
        }, 20 * 5, 20 * 5);

        log(pdf.getVersion() + " enabled.");
    }

    public void onDisable(){

    }
    
    public WorldLaborer getLaborer(World world){
        return worlds.get(world);
    }

    public static void log(String msg){
        System.out.println("WorldOverseer> " + msg);
    }

    class Query extends com.minecarts.dbquery.Query {
        public Query(String sql) {
            super(WorldOverseer.this, dbq.getProvider(getConfig().getString("db.provider")), sql);
        }
        @Override
        public void onComplete(FinalQuery query) {
            if(query.elapsed() > 500) {
                log(MessageFormat.format("Slow query took {0,number,#} ms", query.elapsed()));
            }
        }
        @Override
        public void onException(Exception x, FinalQuery query) {
            try { throw x; }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void handleWorldFlags(final World world){
        new Query("SELECT `worlds`.* FROM `worlds` WHERE `name` = ? LIMIT 1") {
            @Override
            public void onFetchOne(HashMap row) {
                if(row == null){
                    log("Could not find a world with name: " + world.getName());
                    return;
                }
                //Skip any null world flags
                if(row.get("disable") == null || ((String)row.get("disable")).equalsIgnoreCase("")){
                    return;
                }

                if(!worlds.containsKey(world)){
                    worlds.put(world,new WorldLaborer(world));
                }
                for(String flag : ((String)row.get("disable")).split(",")){
                    worlds.get(world).setFlag(WorldFlag.valueOf(flag));
                }
                log("Flags set for world " + world);
            }
        }.fetchOne(world.getName());
    }


}
