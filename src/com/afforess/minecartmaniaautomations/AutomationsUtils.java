/**
 * 
 */
package com.afforess.minecartmaniaautomations;

import java.lang.reflect.Method;
import java.util.Random;

import net.minecraft.server.Block;

import org.bukkit.inventory.ItemStack;

/**
 * @author Rob
 * 
 */
public class AutomationsUtils {
    
    public static ItemStack getDropsForBlock(final Random random, final int id, final int data, final int miningWithTool) {
        final Block b = net.minecraft.server.Block.byId[id];
        final int numDrops = b.getDropCount(0, random);
        final int dropId = b.getDropType(miningWithTool, random, 0);
        int dropData = data;
        if (dropId <= 0)
            return null;
        try {
            final Method m = b.getClass().getDeclaredMethod("getDropData", int.class);
            m.setAccessible(true);
            dropData = (Integer) m.invoke(b, miningWithTool);
        } catch (final Exception e) {
            // Probably not going to happen.
            e.printStackTrace();
        }
        return new ItemStack(dropId, numDrops, (short) dropData);
    }
    
}
