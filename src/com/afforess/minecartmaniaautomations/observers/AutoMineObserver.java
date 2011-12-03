package com.afforess.minecartmaniaautomations.observers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.afforess.minecartmaniaautomations.AutomationsUtils;
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
        
        // Don't mess with stuff underneath rails or redstone wire
        if (aboveId == Material.RAILS.getId() 
        		|| aboveId==Material.POWERED_RAIL.getId() 
        		|| aboveId == Material.DETECTOR_RAIL.getId()
        		|| aboveId == Material.REDSTONE_WIRE.getId()) {
            return false;
        }
        
        List<Integer> blocks = getAdjacentBlockTypes(minecart,x,y,z);
        for(int type : blocks) {
	        if(type == Material.WALL_SIGN.getId()
    		|| type == Material.TORCH.getId()
    		|| type == Material.REDSTONE_TORCH_ON.getId()
    		|| type == Material.REDSTONE_TORCH_OFF.getId()
    		|| type == Material.STONE_BUTTON.getId()
    		|| type == Material.LEVER.getId()
        	) return false;
        }
        if (id == Material.BEDROCK.getId() || id == Material.RAILS.getId()) {
            return false;
        }
        
        // Otherwise, if it's in the list, mine it.
        AbstractItem[] mineMats = (AbstractItem[]) minecart.getDataValue("AutoMine");
        
        for (AbstractItem stack : mineMats) {
            if (stack.getId() == id && (stack.getData() == data || stack.getData() == -1)) {
                ItemStack is = AutomationsUtils.getDropsForBlock(random,id,data,0);
                if(is!=null) {
                    if (minecart.addItem(is)) {
                        MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.AIR.getId(), x, y, z);
                        dirty = true;
                    }
                } else {
                    MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.AIR.getId(), x, y, z);
                    dirty = true;
                }
            }
        }
        
        return dirty;
    }

	private List<Integer> getAdjacentBlockTypes(
			MinecartManiaStorageCart minecart, int x, int y, int z) {
		ArrayList<Integer> l = new ArrayList<Integer>();
		int types[] = new int[4];
		types[0] = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x+1, y, z);
		types[1] = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x-1, y, z);
		types[2] = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y+1, z);
		types[3] = MinecartManiaWorld.getBlockIdAt(minecart.getWorld(), x, y-1, z);
		for(int type : types) {
			if(!l.contains(type))
				l.add(type);
		}
		return l;
	}
}
