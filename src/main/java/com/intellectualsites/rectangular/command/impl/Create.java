package com.intellectualsites.rectangular.command.impl;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import com.intellectualsites.rectangular.Rectangular;
import com.intellectualsites.rectangular.api.objects.Region;
import com.intellectualsites.rectangular.core.Rectangle;
import com.intellectualsites.rectangular.misc.RectangularRunnable;
import com.intellectualsites.rectangular.player.RectangularPlayer;
import com.intellectualsites.rectangular.util.ChatUtil;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;

@CommandDeclaration(
        command = "create"
)
public class Create extends Command {

    @Override
    public boolean onCommand(CommandInstance instance) {
        RectangularPlayer player = (RectangularPlayer) instance.getCaller();
        if (player.isInRegion()) {
            player.sendMessage("You're already in a region. Use /region expand!");
        } else {
            if (Rectangular.get().getServiceManager().getSelectionManager().isFinished(player)) {
                final Rectangle initialRectangle = Rectangular.get().getServiceManager()
                        .getSelectionManager().getSelection(player);
                final String containerId = "w:" + player.getWorld(); // TODO: Fix this
                final String uuid = player.getUniqueId().toString();
                Collection<Region> overlapping = Rectangular.get().getRegionManager().overlaps(player.getWorld(), initialRectangle);
                if (!overlapping.isEmpty()) {
                    player.sendMessage("&c&lCannot create region: &6The bounds are overlapping with region(s): &e{0}",
                            StringUtils.join(overlapping, ","));
                    return true;
                }
                ((RectangularRunnable) () -> {
                    Region r = Rectangular.get().getDatabase().createRegion(uuid, containerId, initialRectangle);
                    player.sendMessage("Created region with id: " + r.getId());
                    player.sendMessage("Trying to add it to the region manager");
                    try {
                        Rectangular.get().getRegionManager().addRegionUnsafe(r);
                    } catch (Exception e) {
                        player.sendMessage("Something went wrong when adding the region: " + e.getMessage() + " | " + r.getOwningContainer());
                        if (instance.getArguments().length > 0) {
                            if (instance.getArguments()[0].equalsIgnoreCase("verbose")) {
                                ChatUtil.sendStacktrace(player, e);
                            }
                        }
                    }
                    player.sendMessage("Success!");
                }).runAsync();
            }
        }
        return true;
    }
}
