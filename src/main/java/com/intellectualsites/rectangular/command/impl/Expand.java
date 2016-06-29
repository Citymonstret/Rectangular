package com.intellectualsites.rectangular.command.impl;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import com.intellectualsites.rectangular.Rectangular;
import com.intellectualsites.rectangular.api.objects.Region;
import com.intellectualsites.rectangular.config.Message;
import com.intellectualsites.rectangular.core.Rectangle;
import com.intellectualsites.rectangular.misc.RectangularRunnable;
import com.intellectualsites.rectangular.parser.impl.RegionParser;
import com.intellectualsites.rectangular.player.RectangularPlayer;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandDeclaration(
        command = "expand"
)
public class Expand extends Command {

    public Expand() {
        withArgument("region", new RegionParser(), "Region to expand");
    }

    @Override
    public boolean onCommand(CommandInstance instance) {
        RectangularPlayer player = (RectangularPlayer) instance.getCaller();
        if (Rectangular.getServiceManager().getSelectionManager().isFinished(player)) {
            final Rectangle rectangle = Rectangular.getServiceManager()
                    .getSelectionManager().getSelection(player);
            final Region region = instance.getValue("region", Region.class);
            if (!region.isExpandableTo(rectangle)) {
                if (region.isExceeding(rectangle) == null) {
                    return Message.ERROR_BOUNDS_NOT_SHARING_EDGE.send(player);
                } else {
                    Message.INFO_BOUNDS_WERE_ADJUSTED.send(player);
                }
            }
            Rectangle copy = new Rectangle();
            copy.copyFrom(rectangle);
            copy.shrink(1);
            if (region.overlaps(copy)) {
                Message.ERROR_BOUNDS_INSIDE_REGION.send(player);
            } else {
                List<Region> overlaps = new ArrayList<>(Rectangular.getRegionManager().overlaps(player.getWorld(), rectangle));
                overlaps.remove(region); // Make sure that we're not testing for the current region
                if (!overlaps.isEmpty()) {
                    Message.ERROR_BOUNDS_OVERLAPPING.send(player, StringUtils.join(overlaps, ","));
                } else {
                    // Yay, everything is fine!
                    ((RectangularRunnable) () -> {
                        try {
                            Rectangular.getDatabase().addRectangle(region.getId(), rectangle);
                            List<Rectangle> rectangles = new ArrayList<>();
                            rectangles.addAll(Arrays.asList(region.getRectangles()));
                            rectangles.add(rectangle);
                            region.setRectangles(rectangles);
                            region.compile();
                            Rectangular.getRegionManager().addRegionUnsafe(region);
                            Message.INFO_SUCCESS.send(player);
                        } catch (final Exception e) {
                            player.sendMessage("Something went wrong when adding the rectangle");
                            e.printStackTrace();
                        }
                    }).runAsync();
                }
            }
        } else {
            Message.ERROR_MISSING_SELECTION.send(player);
            Message.TIP_USE_WAND.send(player);
        }
        return true;
    }
}
