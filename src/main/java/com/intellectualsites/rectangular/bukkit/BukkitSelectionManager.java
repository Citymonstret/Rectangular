package com.intellectualsites.rectangular.bukkit;

import com.intellectualsites.rectangular.core.Rectangle;
import com.intellectualsites.rectangular.player.RectangularPlayer;
import com.intellectualsites.rectangular.selection.SelectionManager;
import com.intellectualsites.rectangular.vector.Vector2;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class BukkitSelectionManager implements SelectionManager, Listener {

    private Map<Integer, TemporarySelection> selectionMap = new HashMap<>();

    private static final String ITEM_TITLE = ChatColor.translateAlternateColorCodes('&', "&9[&8Rectangular&9] &cSelection Tool");

    private ItemStack stack;

    public ItemStack getSelectionTool() {
        if (stack == null) {
            stack = new ItemStack(Material.POTATO_ITEM);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(ITEM_TITLE);
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            stack.setItemMeta(meta); // Because this fails sometimes, apparently?
        }
        return stack;
    }

    public boolean isSelectionTool(@NonNull final ItemStack ostack) {
        return ostack.isSimilar(getSelectionTool());
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

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        RectangularPlayer rectangularPlayer = BukkitUtil.getPlayer(event.getPlayer());
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (isSelectionTool(event.getPlayer().getInventory().getItemInMainHand())) {
                TemporarySelection selection;
                if (hasSelection(rectangularPlayer)) {
                    selection = selectionMap.get(rectangularPlayer.getId());
                } else {
                    Location l = event.getPlayer().getLocation().clone();
                    l.subtract(10000, 0, 10000);
                    selection = new TemporarySelection(BukkitUtil.locationToVector(l));
                    selectionMap.put(rectangularPlayer.getId(), selection);
                }
                Vector2 vector2 = BukkitUtil.locationToVector(event.getClickedBlock().getLocation());
                int status = selection.add(vector2);
                if (status == 0) {
                    return;
                }
                rectangularPlayer.sendMessage("&cSet &6" + (status == -1 ? "min" : "max")
                        + " &cto &6" + vector2.getX() + "&c,&6" + vector2.getY());
            }
        }
    }

    @Override
    public void clearSelection(RectangularPlayer player) {
        this.selectionMap.remove(player.getId());
    }

    @Override
    public boolean equipPlayer(RectangularPlayer player) {
        if (!((BukkitPlayer) player).getPlayer().getInventory().contains(getSelectionTool())) {
            player.giveItem(BukkitUtil.itemStackToItem(getSelectionTool()));
            return true;
        }
        return false;
    }

    private static class TemporarySelection {

        @Getter
        @Setter
        private boolean hasMin, hasMax;

        @Getter
        @Setter
        private Vector2 min, max, origin;

        public TemporarySelection(Vector2 origin) {
            this.origin = origin;
        }

        public int add(Vector2 v2) {
            if (v2.equals(max) || v2.equals(min)) {
                return 0;
            }

            int status;

            Vector2 smallest = origin.clone();

            double diff = smallest.distanceSquared(v2);
            double minDiff;

            if (min != null ) {
                minDiff = smallest.distanceSquared(min);
            } else {
                minDiff = -999999999;
            }

            if (diff > minDiff) {
                status = 1;
            } else {
                status = -1;
            }

            if (status == -1) {
                if (min != null) {
                    max = min;
                }
                min = v2;
            } else {
                if (max != null) {
                    min = max;
                }
                max = v2;
            }

            return status;
        }

        public boolean isComplete() {
            return hasMin && hasMax;
        }
    }
}
