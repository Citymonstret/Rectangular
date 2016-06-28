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

@SuppressWarnings("unused")
class BukkitSelectionManager implements SelectionManager, Listener {

    private Map<Integer, TemporarySelection> selectionMap = new HashMap<>();

    private static final String ITEM_TITLE = ChatColor.translateAlternateColorCodes('&', "&9[&8Rectangular&9] &cSelection Tool");

    private ItemStack stack;

    private ItemStack getSelectionTool() {
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

    private boolean isSelectionTool(@NonNull final ItemStack ostack) {
        return ostack.isSimilar(getSelectionTool());
    }

    @Override
    public boolean hasSelection(RectangularPlayer player) {
        return selectionMap.containsKey(player.getId());
    }

    @Override
    public Rectangle getSelection(RectangularPlayer player) {
        TemporarySelection temp = selectionMap.get(player.getId());
        return new Rectangle(temp.getV1(), temp.getV2());
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
                    selection = new TemporarySelection();
                    selectionMap.put(rectangularPlayer.getId(), selection);
                }
                Vector2 vector2 = BukkitUtil.locationToVector(event.getClickedBlock().getLocation());
                selection.add(vector2, rectangularPlayer);
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

    @Override
    public boolean isFinished(RectangularPlayer player) {
        return hasSelection(player) && selectionMap.get(player.getId()).isComplete();
    }

    private static class TemporarySelection {

        @Getter
        @Setter
        private Vector2 v1, v2;

        public int add(Vector2 v3, RectangularPlayer player) {
            if (v3.equals(v1) || v3.equals(v2)) {
                return 0;
            }

            if (v1 == null) {
                v1 = v3;
            } else if (v2 == null) {
                v2 = v3;
            }

            if (isComplete()) {
                int x1 = v1.getX();
                int x2 = v2.getX();
                int y1 = v1.getY();
                int y2 = v2.getY();
                v1.set(Math.min(x1, x2), Math.min(y1, y2));
                v2.set(Math.max(x1, x2), Math.max(y1, y2));
            }

            player.sendMessage("&cSet min to: &6" + v1);
            player.sendMessage("&cSet max to: &6" + v2);

            return 0;
        }

        boolean isComplete() {
            return v1 != null && v2 != null;
        }
    }
}
