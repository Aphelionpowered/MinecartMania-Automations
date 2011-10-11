package com.afforess.minecartmaniaautomations;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import com.afforess.minecartmaniacore.minecart.MinecartManiaMinecart;
import com.afforess.minecartmaniacore.minecart.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.world.Item;
import com.afforess.minecartmaniacore.world.MinecartManiaWorld;
import com.afforess.minecartmaniacore.event.MinecartActionEvent;
import com.afforess.minecartmaniacore.event.MinecartManiaListener;

public class MinecartManiaActionListener extends MinecartManiaListener {
    
    public List<BlockObserver> blockObservers = new ArrayList<BlockObserver>();
    
    public void onMinecartActionEvent(MinecartActionEvent event) {
        if (!event.isActionTaken()) {
            MinecartManiaMinecart minecart = event.getMinecart();
            if (minecart.isStorageMinecart()) {
                //Efficiency. Don't farm overlapping tiles repeatedly, waste of time
                int interval = minecart.getDataValue("Farm Interval") == null ? -1 : (Integer) minecart.getDataValue("Farm Interval");
                if (interval > 0) {
                    minecart.setDataValue("Farm Interval", interval - 1);
                } else {
                    minecart.setDataValue("Farm Interval", minecart.getRange() / 2);
                    
                    if (minecart.getRange() < 1) {
                        return;
                    }
                    Location loc = minecart.minecart.getLocation().clone();
                    int range = minecart.getRange();
                    int rangeY = minecart.getRangeY();
                    for (int dx = -(range); dx <= range; dx++) {
                        for (int dy = -(rangeY); dy <= rangeY; dy++) {
                            for (int dz = -(range); dz <= range; dz++) {
                                //Setup data
                                int x = loc.getBlockX() + dx;
                                int y = loc.getBlockY() + dy;
                                int z = loc.getBlockZ() + dz;
                                int type = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y, z);
                                for (BlockObserver bo : blockObservers) {
                                    if (bo.blockType != Item.AIR) {
                                        if (bo.blockType.getId() != type) {
                                            continue;
                                        }
                                    }
                                    if (bo.onBlockSeen((MinecartManiaStorageCart) minecart, x, y, z)) {
                                        type = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y, z);
                                    }
                                }
                            }
                        }
                    }
                    StorageMinecartUtils.doAutoFarm((MinecartManiaStorageCart) minecart);
                    //StorageMinecartUtils.doAutoMelon((MinecartManiaStorageCart) minecart);
                    //StorageMinecartUtils.doAutoPumpkin((MinecartManiaStorageCart) minecart);
                    StorageMinecartUtils.doAutoTimber((MinecartManiaStorageCart) minecart);
                    StorageMinecartUtils.doAutoCactusFarm((MinecartManiaStorageCart) minecart);
                    StorageMinecartSugar.doAutoSugarFarm((MinecartManiaStorageCart) minecart);
                    //StorageMinecartSmartForest.doSmartForest((MinecartManiaStorageCart) minecart);
                }
            }
        }
    }
}
