/**
 * 
 */
package com.afforess.minecartmaniaautomations.observers;

import java.util.Random;

import org.bukkit.Material;

import com.afforess.minecartmaniaautomations.BlockObserver;
import com.afforess.minecartmaniacore.minecart.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.world.Item;
import com.afforess.minecartmaniacore.world.MinecartManiaWorld;

/**
 * @author Rob
 * 
 */
public class DefoliatorObserver extends BlockObserver {
    
    private Random random;
    
    public DefoliatorObserver() {
        super("Defoliate");
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.afforess.minecartmaniaautomations.BlockObserver#onBlockSeen(com.afforess.minecartmaniacore.minecart.MinecartManiaStorageCart, int, int, int)
     */
    @Override
    public boolean onBlockSeen(final MinecartManiaStorageCart minecart, final int x, final int y, final int z) {
        if (minecart.getDataValue("Defoliate") == null)
            return false;
        if (random == null) {
            random = new Random(x * y);
        }
        boolean dirty = false;
        //update data
        final int id = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y, z);
        final int belowId = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y - 2, z);
        
        boolean remove = true;
        switch (Material.getMaterial(id)) {
            case RED_ROSE:
            case YELLOW_FLOWER:
                if (minecart.getDataValue("SmartForest") != null) {
                    int controlBlock = 4;
                    if (id == Material.RED_ROSE.getId()) {
                        controlBlock = Material.COBBLESTONE.getId();
                    } else if (id == Material.YELLOW_FLOWER.getId()) {
                        controlBlock = Material.SANDSTONE.getId();
                    }
                    remove = !(belowId == controlBlock);
                }
                if (remove) {
                    if (minecart.addItem(id)) {
                        MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.AIR.getId(), x, y, z);
                        dirty = true;
                    }
                }
                break;
            case LONG_GRASS:
                /*
                 * If grass ever gets a control block if (minecart.getDataValue("SmartForest") != null) { //int belowData = MinecartManiaWorld.getBlockData(minecart.minecart.getWorld(), x, y - 2, z); int controlBlock = (id == Item.RED_ROSE.getId()) ? Item.COBBLESTONE.getId() : Item.SANDSTONE.getId(); remove = !(belowId == controlBlock); }
                 */
                if (remove) {
                    if (minecart.addItem(Item.SEEDS.getId())) {
                        MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.AIR.getId(), x, y, z);
                        dirty = true;
                    }
                }
                break;
            case SNOW:
                if (remove) {
                    if (minecart.addItem(Item.SNOW_BALL.getId())) {
                        MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.AIR.getId(), x, y, z);
                        dirty = true;
                    }
                }
                break;
        }
        return dirty;
    }
}
