package com.intellectualsites.rectangular.bukkit.nms;

import com.intellectualsites.rectangular.bukkit.ArmorStandManager;
import com.intellectualsites.rectangular.bukkit.ItemNBTManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class NMSImplementation {

    @Getter
    private final String implementationName;

    @Getter
    private final ArmorStandManager armorStandManager;

    @Getter
    private final ItemNBTManager itemNBTManager;

}
