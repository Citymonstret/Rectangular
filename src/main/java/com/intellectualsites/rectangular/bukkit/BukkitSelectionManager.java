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
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class BukkitSelectionManager implements SelectionManager, Listener {

    private Map<Integer, TemporarySelection> selectionMap = new HashMap<>();

    private static final String ITEM_TITLE = ChatColor.translateAlternateColorCodes('&', "&9[&8Rectangular&9] &cSelection Tool");

    public ItemStack getSelectionTool() {
        ItemStack stack = new ItemStack(Material.POTATO_ITEM);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ITEM_TITLE);
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta); // Because this fails sometimes, apparently?
        return stack;
    }

    public boolean isSelectionTool(ItemStack stack) {
        return stack.isSimilar(getSelectionTool());
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

    @Override
    public void equipPlayer(RectangularPlayer player) {
        player.giveItem(BukkitUtil.itemStackToItem(getSelectionTool()));
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
