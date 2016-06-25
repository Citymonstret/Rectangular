package com.intellectualsites.rectangular.command.impl;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import com.intellectualsites.rectangular.Rectangular;
import com.intellectualsites.rectangular.api.objects.Region;
import com.intellectualsites.rectangular.parser.impl.StringParser;
import com.intellectualsites.rectangular.player.RectangularPlayer;

@CommandDeclaration(
        command = "test",
        aliases = { "t" }
)
public class Test extends Command {

    public Test() {
        withArgument("message", new StringParser(), "A message that will be echoed back to you");
    }

    @Override
    public boolean onCommand(CommandInstance instance) {
        instance.getCaller().message("Trying to create a region with your uuid");

        RectangularPlayer player = (RectangularPlayer) instance.getCaller();
        final String worldName = "w:" + player.getWorld();
        final String uuid = player.getUniqueId().toString();

        Rectangular.get().getServiceManager().runAsync(() -> {
            player.sendMessage("Now running the async task");
            final Region region = Rectangular.get().getDatabase().createRegionAndFetch(uuid, worldName);
            player.sendMessage("Region created: " + region.getId());
            player.sendMessage("Injecting region into manager...");
            Rectangular.get().getRegionManager().addRegion(region);
            player.sendMessage("Region injected! Now testing the meta");
            player.sendMessage("Region Owner UUID: " + region.getData().getOwner());
            player.sendMessage("Your UUID: " + uuid);
            player.sendMessage("Done!");
        });

        return true;
    }


}
