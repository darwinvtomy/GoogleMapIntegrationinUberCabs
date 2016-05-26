package com.darwinvtomy.showcities.showcities;

import java.util.ArrayList;

/**
 * Created by DARWIN V TOMY on 5/20/2016.
 */
public class GlobalClass {
  private static   ArrayList<geonames> globalgeGeonames;



    public static void setthePlacelist(ArrayList<geonames> thePlacelist) {
        globalgeGeonames = thePlacelist;
    }

    public static  ArrayList<geonames> getthePlacelist() {
       return globalgeGeonames;
    }
}
