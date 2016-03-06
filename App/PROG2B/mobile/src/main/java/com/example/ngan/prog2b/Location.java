package com.example.ngan.prog2b;

import java.util.List;

/**
 * Created by ngan on 3/4/16.
 */
public class Location {
    public final String county;
    public final String state;
    public final String obamaPercent;
    public final String romneyPercent;
    public List<RepListItem> representatives;

    public Location(String county, String state, String obamaPercent, String romneyPercent) {
        this.county = county;
        this.state = state;
        this.obamaPercent = obamaPercent;
        this.romneyPercent = romneyPercent;
    }

}
