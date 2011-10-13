package com.afforess.minecartmaniaautomations.observers;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import com.afforess.minecartmaniaautomations.BlockObserver;
import com.afforess.minecartmaniacore.inventory.MinecartManiaInventory;
import com.afforess.minecartmaniacore.minecart.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.world.Item;
import com.afforess.minecartmaniacore.world.MinecartManiaWorld;

public class AutoTimberObserver extends BlockObserver {
    
    public AutoTimberObserver() {
        super("AutoTimber");
    }
    
    @Override
    public boolean onBlockSeen(MinecartManiaStorageCart minecart, int x, int y,
            int z) {
        boolean gdirty = false;
        if (minecart.getDataValue("AutoTimber") == null) {
            return false;
        }
        World w = minecart.minecart.getWorld();
        int id = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y, z);
        
        ////////////////////////////////////////////////////////
        // AUTOMAGIC FERTILIZATION
        ////////////////////////////////////////////////////////
        {
            boolean dirty = false;
            // Grow stems via bonemeal, if the materials are present
            if (minecart.getDataValue("AutoFertilize") != null) {
                int data = MinecartManiaWorld.getBlockData(minecart.minecart.getWorld(), x, y, z);
                if (id == Item.SAPLING.getId()) {
                    // Do we even HAVE bonemeal?
                    if (minecart.amount(Item.BONEMEAL) > 0) {
                        // Remove one bonemeal, use it on crop
                        if (minecart.removeItem(Item.BONEMEAL.getId(), 1, (short) Item.BONEMEAL.getData())) {
                            int treeSubtype = data & 3;
                            // Remove 1 unit of bonemeal and try to dump a tree
                            int rand = ((new Random()).nextInt(10));
                            TreeType t = null;
                            switch (treeSubtype) {
                                case 1:
                                    if (rand == 0) {
                                        t = TreeType.TALL_REDWOOD;
                                    } else {
                                        t = TreeType.REDWOOD;
                                    }
                                    break;
                                case 2:
                                    t = TreeType.BIRCH;
                                    break;
                                default:
                                    if (rand == 0) {
                                        t = TreeType.BIG_TREE;
                                    } else {
                                        t = TreeType.TREE;
                                    }
                                    break;
                            }
                            MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), 0, x, y, z);
                            if (!w.generateTree(new Location(w, x, y, z), t)) {
                                MinecartManiaWorld.setBlockAt(minecart.minecart.getWorld(), Item.SAPLING.getId(), x, y, z);
                                MinecartManiaWorld.setBlockData(minecart.minecart.getWorld(), data, x, y, z);
                            }
                            gdirty = dirty = true;
                        }
                    }
                }
            }
            //update data
            if (dirty) {
                id = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y, z);
                dirty = false;
            }
        }
        if (id == Item.LOG.getId()) {
            int down = 1;
            while (MinecartManiaWorld.getBlockIdAt(w, x, y - down, z) == Item.LOG.getId()) {
                down++;
            }
            int baseId = MinecartManiaWorld.getBlockIdAt(w, x, y - down, z);
            //base of tree
            if (baseId == Material.DIRT.getId() || baseId == Material.GRASS.getId() || baseId == Item.LEAVES.getId()) {
                Item base = Item.getItem(w.getBlockAt(x, y - down + 1, z));
                //Attempt to replant the tree
                if (removeLogs(x, y - down + 1, z, w, minecart, false) && minecart.getDataValue("AutoForest") != null) {
                    Item sapling = Item.SAPLING;
                    if (base.getData() == 0x1)
                        sapling = Item.SPRUCE_SAPLING;
                    if (base.getData() == 0x2)
                        sapling = Item.BIRCH_SAPLING;
                    if (minecart.contains(sapling)) {
                        minecart.removeItem(sapling.getId(), sapling.getData());
                        w.getBlockAt(x, y - down + 1, z).setTypeIdAndData(sapling.getId(), (byte) sapling.getData(), true);
                        gdirty = true;
                    }
                }
            }
        }
        return gdirty;
    }
    
    private boolean removeLogs(int posx, int posy, int posz, World w,
            MinecartManiaInventory inventory, boolean recursing) {
        boolean action = false;
        int range = 1;
        for (int dx = -(range); dx <= range; dx++) {
            for (int dy = -(range); dy <= range; dy++) {
                for (int dz = -(range); dz <= range; dz++) {
                    //Setup data
                    int x = posx + dx;
                    int y = posy + dy;
                    int z = posz + dz;
                    int id = MinecartManiaWorld.getBlockIdAt(w, x, y, z);
                    int data = MinecartManiaWorld.getBlockData(w, x, y, z);
                    if (id == Item.LOG.getId()) {
                        ItemStack logstack = Item.getItem(id, data).toItemStack();
                        if (!inventory.addItem(logstack)) {
                            if (recursing)
                                MinecartManiaWorld.spawnDrop(w, x, y, z, logstack);
                            else
                                return false;
                        }
                        MinecartManiaWorld.setBlockAt(w, 0, x, y, z);
                        removeLogs(x, y, z, w, inventory, true);
                    }
                }
            }
        }
        return action;
    }
}
