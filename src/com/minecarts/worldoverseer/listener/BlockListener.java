package com.minecarts.worldoverseer.listener;

import com.minecarts.worldoverseer.WorldFlag;
import com.minecarts.worldoverseer.WorldLaborer;
import com.minecarts.worldoverseer.WorldOverseer;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;

public class BlockListener extends org.bukkit.event.block.BlockListener {
    private WorldOverseer plugin;

    public BlockListener(WorldOverseer plugin){
        this.plugin = plugin;
    }

    @Override
    public void onBlockPhysics(BlockPhysicsEvent event){
        if(event.isCancelled()) return;

        WorldLaborer laborer = plugin.getLaborer(event.getBlock().getWorld());
        if(laborer == null) return;

        //There is no onBlockGrow event, so we haev to use the physics check??
        if(event.getBlock().getType() == Material.CROPS){
            if(laborer.isFlagSet(WorldFlag.CROP_GROW)){
                event.setCancelled(true);
            }
        }
    }


    @Override
    public void onBlockSpread(BlockSpreadEvent event){
        if(event.isCancelled()) return;
        
        WorldLaborer laborer = plugin.getLaborer(event.getBlock().getWorld());
        if(laborer == null) return;

        if(event.getNewState().getBlock().getType() == Material.GRASS){
            if(laborer.isFlagSet(WorldFlag.GRASS_SPREAD)){
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event){
        if(event.isCancelled()) return;
        WorldLaborer laborer = plugin.getLaborer(event.getBlock().getWorld());
        if(laborer == null) return;

        if(laborer.isFlagSet(WorldFlag.BLOCK_PLACE) && !event.getPlayer().hasPermission("overseer.block.bypass")){
            event.setCancelled(true);
        }
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event){
        if(event.isCancelled()) return;
        WorldLaborer laborer = plugin.getLaborer(event.getBlock().getWorld());
        if(laborer == null) return;

        if(laborer.isFlagSet(WorldFlag.BLOCK_BREAK) && !event.getPlayer().hasPermission("overseer.block.bypass")){
            event.setCancelled(true);
        }
    }
}
