package com.afforess.minecartmaniaautomations;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import com.afforess.minecartmaniacore.MinecartManiaInventory;
import com.afforess.minecartmaniacore.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.MinecartManiaWorld;

public class StorageMinecartUtils {

	public static void doAutoFarm(MinecartManiaStorageCart minecart) {
		if (minecart.getDataValue("AutoHarvest") == null && minecart.getDataValue("AutoTill") == null && minecart.getDataValue("AutoSeed") == null) {
			return;
		}
		if (MinecartManiaWorld.getIntValue(MinecartManiaWorld.getConfigurationValue("Nearby Collection Range")) < 1) {
			return;
		}
		Location loc = minecart.minecart.getLocation().clone();
		int range = minecart.getEntityDetectionRange();
		for (int dx = -(range); dx <= range; dx++){
			for (int dy = -(range); dy <= range; dy++){
				for (int dz = -(range); dz <= range; dz++){
					//Setup data
					int x = loc.getBlockX() + dx;
					int y = loc.getBlockY() + dy;
					int z = loc.getBlockZ() + dz;
					int id = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y, z);
					int aboveId = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y+1, z);
					boolean dirty = false; //set when the data gets changed
					//Harvest fully grown crops first
					if (minecart.getDataValue("AutoHarvest") != null) {
						int data = MinecartManiaWorld.getBlockData(minecart.minecart.getWorld(), x, y, z);
						if (id == Material.CROPS.getId()) {
							//fully grown
							if (data == 0x7) {
								minecart.addItem(Material.WHEAT.getId());
								minecart.addItem(Material.SEEDS.getId());
								if ((new Random()).nextBoolean()) { //Randomly add second seed.
									minecart.addItem(Material.SEEDS.getId());
								}
								MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.AIR.getId(), x, y, z);
								dirty = true;
							}
						}
					}
					//update data
					if (dirty) {
						id = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y, z);
						aboveId = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y+1, z);
						dirty = false;
					}
					//till soil
					if (minecart.getDataValue("AutoTill") != null) {
						if (id == Material.GRASS.getId() ||  id == Material.DIRT.getId()) {
							if (aboveId == Material.AIR.getId()) {
								MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.SOIL.getId(), x, y, z);
								dirty = true;
							}
						}
					}
					
					//update data
					if (dirty) {
						id = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y, z);
						aboveId = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y+1, z);
						dirty = false;
					}
					//Seed tilled land 
					if (minecart.getDataValue("AutoSeed") != null) {
						if (id == Material.SOIL.getId()) {
							if (aboveId == Material.AIR.getId()) {
								if (minecart.removeItem(Material.SEEDS.getId())) {
									MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Material.CROPS.getId(), x, y+1, z);
									dirty = true;
								}
							}
						}
					}
					
				}
			}
		}
	}
	
	
	public static void doAutoTimber(MinecartManiaStorageCart minecart) {
		if (minecart.getDataValue("AutoTimber") == null) {
			return;
		}
		if (MinecartManiaWorld.getIntValue(MinecartManiaWorld.getConfigurationValue("Nearby Collection Range")) < 1) {
			return;
		}
		Location loc = minecart.minecart.getLocation().clone();
		int range = minecart.getEntityDetectionRange();
		for (int dx = -(range); dx <= range; dx++){
			for (int dy = -(range); dy <= range; dy++){
				for (int dz = -(range); dz <= range; dz++){
					//Setup data
					int x = loc.getBlockX() + dx;
					int y = loc.getBlockY() + dy;
					int z = loc.getBlockZ() + dz;
					World w = minecart.minecart.getWorld();
					int id = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y, z);
					if (id == Material.LOG.getId()) {
						int down = 1;
						while (MinecartManiaWorld.getBlockIdAt(w, x, y - down, z) == Material.LOG.getId()) {
							down++;
						}
						int baseId = MinecartManiaWorld.getBlockIdAt(w, x, y - down, z);
						//base of tree
						if (baseId == Material.DIRT.getId() || baseId == Material.GRASS.getId()) {
							removeLogs(x, y - down + 1, z, w, minecart);
							//Attempt to replant the tree
							if (minecart.getDataValue("AutoForest") != null) {
								if (minecart.contains(Material.SAPLING)) {
									minecart.removeItem(Material.SAPLING.getId());
									MinecartManiaWorld.setBlockAt(w, Material.SAPLING.getId(), x, y - down + 1, z);
								}
							}
						}
					}
					
				}
			}
		}
		
	}
	
	public static void doAutoFertilize(MinecartManiaStorageCart minecart) {
		return; //TODO fix this
		/*if (minecart.getDataValue("AutoFertilize") == null) {
			return;
		}
		if (MinecartManiaWorld.getIntValue(MinecartManiaWorld.getConfigurationValue("Nearby Collection Range")) < 1) {
			return;
		}
		ItemStack[] contents = minecart.getContents();
		for (ItemStack item: contents) {
			System.out.println("Item: " + item.getType() + " Amt: " + item.getAmount() + "durability: " + item.getDurability());
		}
		//Uses same id as bonemeal
		if (!minecart.contains(Material.INK_SACK.getId(), (short) 15)) {
			return;
		}
		Location loc = minecart.minecart.getLocation().clone();
		int range = minecart.getEntityDetectionRange();
		for (int dx = -(range); dx <= range; dx++){
			for (int dy = -(range); dy <= range; dy++){
				for (int dz = -(range); dz <= range; dz++){
					//Setup data
					int x = loc.getBlockX() + dx;
					int y = loc.getBlockY() + dy;
					int z = loc.getBlockZ() + dz;
					World w = minecart.minecart.getWorld();
					int id = MinecartManiaWorld.getBlockIdAt(w, x, y, z);
					if (id == Material.CROPS.getId()) {
						int data = MinecartManiaWorld.getBlockData(w, x, y, z);
						//fully grown
						if (data != 0x7) {
							if (minecart.removeItem(Material.INK_SACK.getId(), 1, (short) 0xf)) {
								MinecartManiaWorld.setBlockData(w, 0x7, x, y, z);
							}
						}
					}
					else if (id == Material.SAPLING.getId()) {
						if (minecart.removeItem(Material.INK_SACK.getId(), 0xf))  {
							int rand = ((new Random()).nextInt(5));
							TreeType t = null;
							switch (rand) {
								case 0: t = TreeType.BIG_TREE; break;
								case 1: t = TreeType.BIRCH; break;
								case 2: t = TreeType.REDWOOD; break;
								case 3: t = TreeType.TALL_REDWOOD; break;
								case 4: t = TreeType.TREE; break;
							}
							MinecartManiaWorld.setBlockAt(w, 0, x, y, z);
							w.generateTree(new Location(w, x, y, z), t);
						}
					}
				}
			}
		}*/
	}


	private static boolean removeLogs(int posx, int posy, int posz, World w, MinecartManiaInventory inventory) {
		boolean action = false;
		int range = 1;
		for (int dx = -(range); dx <= range; dx++){
			for (int dy = -(range); dy <= range; dy++){
				for (int dz = -(range); dz <= range; dz++){
					//Setup data
					int x = posx + dx;
					int y = posy + dy;
					int z = posz + dz;
					int id = MinecartManiaWorld.getBlockIdAt(w, x, y, z);
					if (id  == Material.LOG.getId() && inventory.addItem(id)) {
						action = true;
						MinecartManiaWorld.setBlockAt(w, 0, x, y, z);
						removeLogs(x, y, z, w, inventory);
					}
				}
			}
		}
		return action;
	}

}
