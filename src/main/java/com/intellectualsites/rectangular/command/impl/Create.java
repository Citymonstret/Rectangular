package com.intellectualsites.rectangular.command.impl;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import com.intellectualsites.rectangular.Rectangular;
import com.intellectualsites.rectangular.api.objects.Region;
import com.intellectualsites.rectangular.config.Message;
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
            Message.ERROR_IN_REGION.send(player);
            Message.TIP_USE_EXPAND.send(player);
        } else {
            if (Rectangular.getServiceManager().getSelectionManager().isFinished(player)) {
                final Rectangle initialRectangle = new Rectangle();
                initialRectangle.copyFrom(Rectangular.getServiceManager()
                        .getSelectionManager().getSelection(player));
                final String containerId = "w:" + player.getWorld(); // TODO: Fix this
                final String uuid = player.getUniqueId().toString();
                Collection<Region> overlapping = Rectangular.getRegionManager().overlaps(player.getWorld(), initialRectangle);
                if (!overlapping.isEmpty()) {
                    Message.ERROR_BOUNDS_OVERLAPPING.send(player, StringUtils.join(overlapping, ","));
                    return true;
                }
                ((RectangularRunnable) () -> {
                    Region r = Rectangular.getDatabase().createRegion(uuid, containerId, initialRectangle);
                    Message.INFO_CREATED_REGION.send(player, r.getId());
                    Message.INFO_ADDING_TO_MANAGER.send(player, r.getId());
                    try {
                        Rectangular.getRegionManager().addRegionUnsafe(r);
                    } catch (Exception e) {
                        // Non-Standard message
                        player.sendMessage("Something went wrong when adding the region: " + e.getMessage() + " | " + r.getOwningContainer());
                        if (instance.getArguments().length > 0) {
                            if (instance.getArguments()[0].equalsIgnoreCase("verbose")) {
                                ChatUtil.sendStacktrace(player, e);
                            }
                        }
                    }
                    Message.INFO_SUCCESS.send(player);
                }).runAsync();
            } else {
                Message.ERROR_MISSING_SELECTION.send(player);
                Message.TIP_USE_WAND.send(player);
            }
        }
        return true;
    }

}
