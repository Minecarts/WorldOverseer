package com.minecarts.worldoverseer;

import com.minecarts.dbquery.DBQuery;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

public class WorldOverseer extends org.bukkit.plugin.java.JavaPlugin {
    private DBQuery dbq;
    private HashMap<World, WorldLaborer> worlds = new HashMap<World, WorldLaborer>();

    public void onEnable(){
        PluginManager pm = getServer().getPluginManager();
        PluginDescriptionFile pdf = getDescription();

        dbq = (DBQuery) getServer().getPluginManager().getPlugin("DBQuery");
        pm.registerEvents(new FlagListener(this),this);

        loadAllWorldFlags(); //Handle any existing worlds

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

    public void log(String msg){
        getLogger().log(Level.INFO,msg);
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

    public void loadAllWorldFlags(){
        new Query("SELECT * FROM `worlds`") {
            @Override
            public void onFetch(ArrayList<HashMap> rows) {
                if(rows == null || rows.size() == 0) return;
                for(HashMap row : rows){
                    if(row.get("disable") == null || ((String)row.get("disable")).equalsIgnoreCase("")){
                        continue;
                    }

                    World world = Bukkit.getWorld((String)row.get("name"));
                    if(world == null){
                        log("No world " + row.get("name") + " found.");
                        continue; //No world with this name exists
                    }

                    if(!worlds.containsKey(world)){
                        worlds.put(world,new WorldLaborer(world));
                    }
                    for(String flag : ((String)row.get("disable")).split(",")){
                        worlds.get(world).setFlag(WorldFlag.valueOf(flag));
                    }
                    log("Flags set for world " + world);
                }
            }
        }.fetch();
    }


}
