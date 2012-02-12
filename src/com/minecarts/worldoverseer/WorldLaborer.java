package com.minecarts.worldoverseer;


import org.bukkit.World;
import java.util.HashMap;

//Simply a wrapper for a world that allows you to check or set multiple flags for a world.
public class WorldLaborer {
    private HashMap<WorldFlag, Boolean> flags = new HashMap<WorldFlag, Boolean>();
    private World world;
    
    public WorldLaborer(World world){
        this.world = world;
    }
    
    public void setFlag(WorldFlag flag){
        flags.put(flag, true);
    }
    public void unsetFlag(WorldFlag flag){
        flags.put(flag, false);
    }
    public Boolean isFlagSet(WorldFlag flag){
        return !(flags.containsKey(flag)) ? false : flags.get(flag);
    }
    
    public World getWorld(){
        return world;
    }
}
