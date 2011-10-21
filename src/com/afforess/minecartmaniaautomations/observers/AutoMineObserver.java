package com.afforess.minecartmaniaautomations.observers;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.afforess.minecartmaniaautomations.BlockObserver;
import com.afforess.minecartmaniacore.minecart.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.world.AbstractItem;
import com.afforess.minecartmaniacore.world.MinecartManiaWorld;

public class AutoMineObserver extends BlockObserver {
    
    private Random random;
    
    /**
     * @param name
     */
    public AutoMineObserver() {
        super("AutoMine");
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.afforess.minecartmaniaautomations.BlockObserver#onBlockSeen(com.afforess.minecartmaniacore.minecart.MinecartManiaStorageCart, int, int, int)
     */
    @Override
    public boolean onBlockSeen(MinecartManiaStorageCart minecart, int x, int y,
            int z) {
        if (minecart.getDataValue("AutoMine") == null)
            return false;
        if (random == null) {
            random = new Random(x * y);
        }
        boolean dirty = false;
        //update data
        int id = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y, z);
        int data = MinecartManiaWorld.getBlockData(minecart.minecart.getWorld(), x, y, z);
        int aboveId = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y + 1, z);
        
        // Don't mess with stuff underneath rails.
        if (aboveId == Material.RAILS.getId()) {
            return false;
        }
        
        if (id == Material.BEDROCK.getId() || id == Material.RAILS.getId()) {
            return false;
        }
        
        // Otherwise, if it's in the list, mine it.
        ArrayList<AbstractItem> mineMats = new ArrayList<AbstractItem>();
        for(AbstractItem ai : (AbstractItem[]) minecart.getDataValue("AutoMine")) {
            mineMats.add(ai);
        }
        if (!mineMats.contains(id)) {
            return false;
        }
        
        for (AbstractItem stack : mineMats) {
            if (stack.getId() == id && stack.getData() == data) {
                ItemStack in = new ItemStack(id, 1, (short) data);
                if (minecart.addItem(in)) {
                    MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.AIR.getId(), x, y, z);
                    dirty = true;
                }
            }
        }
        
        return dirty;
    }
}
