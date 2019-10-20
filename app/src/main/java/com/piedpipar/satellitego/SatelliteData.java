package com.piedpipar.satellitego;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SatelliteData {
    //Properties
    int id;
    String satName;
    JSONArray positionsJSON;
    ArrayList<SatellitePosition> positions = new ArrayList<>();
    JSONArray visualPassesJSON;
    ArrayList<VisualPass> visualPasses = new ArrayList<>();

    //Constructors
    //empty constructor
    SatelliteData() {
    }

    //constructor to construct from jsonObject
    SatelliteData(JSONObject jsonObject) {
        try {
            //get basic info
            JSONObject infoObject = jsonObject.getJSONObject("info");
            id = infoObject.getInt("satid");
            satName = infoObject.getString("satname");

            //get positions
            positionsJSON = jsonObject.getJSONArray("positions");
            for (int i = 0; i < positionsJSON.length(); i++) {
                SatellitePosition satPos = new SatellitePosition(positionsJSON.getJSONObject(i));
                this.positions.add(satPos);
            }
        } catch (Exception e) {
            Log.d("DebugK", e.getMessage());
        }
    }

    //Methods
    void setVisualPasses(JSONArray passes) {
        visualPassesJSON = passes;
        for (int i = 0; i < passes.length(); i++) {
            try {
                VisualPass visualPass = new VisualPass(passes.getJSONObject(i));
                visualPasses.add(visualPass);
            } catch (Exception e) {
                //
            }
        }
    }
}

class SatellitePosition {
    //Properties
    double lat;
    double lon;
    double altitude;
    double azimuth;
    double elevation;
    int timestamp;

    //Constructors
    SatellitePosition() {
    }

    //Constructor from JSONObject
    SatellitePosition(JSONObject jsonObject) {
        try {
            lat = jsonObject.getDouble("satlatitude");
            lon = jsonObject.getDouble("satlongitude");
            altitude = jsonObject.getDouble("sataltitude");
            azimuth = jsonObject.getDouble("azimuth");
            elevation = jsonObject.getDouble("elevation");
            timestamp = jsonObject.getInt("timestamp");
        } catch (Exception e) {
            //
        }
    }
}

class VisualPass {
    //Properties
    double startAzimuth;
    double startEl;
    int startUTC;
    double endAzimuth;
    double endEl;
    int endUTC;
    int duration;

    //Constructors
    VisualPass(JSONObject jsonObject) {
        try {
            this.startAzimuth = jsonObject.getDouble("startAz");
            this.startEl = jsonObject.getDouble("startEl");
            this.startUTC = jsonObject.getInt("startUTC");
            this.endAzimuth = jsonObject.getDouble("endAz");
            this.endEl = jsonObject.getDouble("endEl");
            this.endUTC = jsonObject.getInt("endUTC");
            this.duration = jsonObject.getInt("duration");
        } catch (Exception e) {

        }
    }
}
