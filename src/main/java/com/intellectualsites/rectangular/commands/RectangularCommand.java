package com.intellectualsites.rectangular.commands;

import com.intellectualsites.rectangular.player.RectangularPlayer;

import java.util.List;

public interface RectangularCommand {

    void execute(RectangularPlayer player, List<String> args);

}
