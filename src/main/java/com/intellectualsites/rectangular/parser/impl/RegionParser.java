package com.intellectualsites.rectangular.parser.impl;

import com.intellectualsites.rectangular.Rectangular;
import com.intellectualsites.rectangular.api.objects.Region;
import com.intellectualsites.rectangular.parser.Parser;
import com.intellectualsites.rectangular.parser.ParserResult;

public class RegionParser extends Parser<Region> {

    public RegionParser() {
        super("region", "108");
    }

    @Override
    public ParserResult<Region> parse(String in) {
        int id;
        try {
            id = Integer.parseInt(in);
        } catch (final Exception e) {
            return new ParserResult<>(in + " is not a valid number");
        }
        Region region = Rectangular.getRegionManager().getRegion(id);
        if (region == null) {
            return new ParserResult<>(in + " is not a valid region ID");
        } else {
            return new ParserResult<>(region);
        }
    }
}
