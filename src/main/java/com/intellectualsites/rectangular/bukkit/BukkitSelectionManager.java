package com.intellectualsites.rectangular.bukkit;

import com.intellectualsites.rectangular.core.Rectangle;
import com.intellectualsites.rectangular.player.RectangularPlayer;
import com.intellectualsites.rectangular.selection.SelectionManager;
import com.intellectualsites.rectangular.vector.Vector2;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class BukkitSelectionManager implements SelectionManager {

    private Map<Integer, TemporarySelection> selectionMap = new HashMap<>();

    private ItemStack selectionTool;

    public BukkitSelectionManager() {
        // TODO: Make this configurable
        {
            ItemStack stack = new ItemStack(Material.POTATO);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.RED + "Rectangular Selection Tool");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            stack.setItemMeta(meta); // Because this fails sometimes, apparently?
            this.selectionTool = stack;
        }
    }

    public ItemStack getSelectionTool() {
        return selectionTool.clone();
    }

    public boolean isSelectionTool(ItemStack stack) {
        return selectionTool.isSimilar(stack);
    }

    @Override
    public boolean hasSelection(RectangularPlayer player) {
        return selectionMap.containsKey(player.getId());
    }

    @Override
    public Rectangle getSelection(RectangularPlayer player) {
        TemporarySelection temp = selectionMap.get(player.getId());
        return new Rectangle(temp.getMin(), temp.getMax());
    }

    @Override
    public void clearSelection(RectangularPlayer player) {
        this.selectionMap.remove(player.getId());
    }

    private static class TemporarySelection {

        @Getter
        @Setter
        private boolean hasMin, hasMax;

        @Getter
        @Setter
        private Vector2 min, max;

        public void add(Vector2 v2) {
            if (hasMin) {
                if (min.getX() + min.getY() > v2.getX() + v2.getY()) {
                    max = min;
                    min = v2;
                } else {
                    max = v2;
                }
                hasMax = true;
            } else if (hasMax) {
                if (max.getX() + min.getY() < v2.getX() + v2.getY()) {
                    min = max;
                    max = v2;
                } else {
                    min = v2;
                }
                hasMin = true;
            }
        }

        public boolean isComplete() {
            return hasMin && hasMax;
        }
    }
}
