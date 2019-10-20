package com.piedpipar.satellitego;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;

import java.util.Timer;
import java.util.TimerTask;

public class Satellite extends Node {
    //Properties
    int currentPosition = 0;
    SatelliteData satData;
    Timer timer;

    //Constructors
    Satellite() {
    }

    Satellite(SatelliteData satData) {
        this.satData = satData;

        //set scale
        setWorldScale(new Vector3(0.03f, 0.03f, 0.03f));

        //start timer to move position every 1 sec
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                currentPosition += 1;
                moveToNewPosition();
            }
        }, 0, 500);
    }

    //Methods
    void setData(SatelliteData satData) {
        this.satData = satData;
    }

    void setCurrentPosition(int position) {
        this.currentPosition = position;
    }

    void moveToNewPosition() {
//        Vector3 currentPositionVector3 = satData.positions.get(currentPosition).positionVector3;
//        Log.d("DebugK", "Current Position Vector3 " + currentPositionVector3.toString());
//        Vector3 nextPositionVector3 = satData.positions.get(currentPosition + 1).positionVector3;
//        Log.d("DebugK", "Next Position Vector3 " + nextPositionVector3.toString());
//
//        float newX = currentPositionVector3.x + (((nextPositionVector3.x - currentPositionVector3.x)/ 10) * positionCounter);
//        float newY = currentPositionVector3.y + (((nextPositionVector3.y - currentPositionVector3.y)/ 10) * positionCounter);
//        float newZ = currentPositionVector3.z + (((nextPositionVector3.z - currentPositionVector3.z)/ 10) * positionCounter);
//        Vector3 destPositionVector3 = new Vector3(newX, newY, newZ);
//
//        Log.d("DebugK", "Dest Position Vector3 " + destPositionVector3.toString());

        setLocalPosition(getPositionVector3());
    }

    public Vector3 getPositionVector3() {
        double lat = satData.positions.get(currentPosition).lat;
        double lon = satData.positions.get(currentPosition).lon;
        double alt = (satData.positions.get(currentPosition).altitude) / 12756200.0;

        double ls = Math.atan(Math.pow((1), 2) * Math.tan(lat));
        double x = 0.5f * Math.cos(ls) * Math.cos(lon) + alt * Math.cos(lat) * Math.cos(lon);
        double y = 0.5f * Math.cos(ls) * Math.sin(lon) + alt * Math.cos(lat) * Math.sin(lon);
        double z = 0.5f * Math.sin(ls) + alt * Math.sin(lat);

        return new Vector3((float) x, (float) y, (float) z);
    }
}
