package com.example.meeko.sentry;
import com.google.android.gms.maps.model.LatLng;

import java.sql.Timestamp;

public class Incident {
    public String VictimName="";
    public String Time;
    public double Latitude = 0;
    public double Longitude = 0;
    public String IID = "";
    public String Directory="";
    public String Loc="";
    public Incident(String d, String ID,String vname, String time, double lat, double lon,String Location){
        super();
        VictimName=vname;
        Time=time;
        Latitude=lat;
        Longitude=lon;
        IID = ID;
        Directory = d;
        Loc = Location;
    }

    public String getLoc() {
        return Loc;
    }

    public String getDirectory() {
        return Directory;
    }

    public double getLatitude(){
        return Latitude;
    }
    public double getLongitude(){
        return Longitude;
    }

    public String getVictimName() {
        return VictimName;
    }

    public String getTime() {
        return Time;
    }
    public String getID() { return IID;}
}
