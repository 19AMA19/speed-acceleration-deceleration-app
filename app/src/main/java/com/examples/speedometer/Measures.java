package com.examples.speedometer;

public class Measures {
    public double acceleration;
    public String date;
    public String time;
    public double latitude;
    public double longitude;
    public int start_speed;
    public String uuid;

    public Measures(String uuid, double acceleration, String date, String time, double latitude, double longitude, int start_speed) {
        this.uuid = uuid;
        this.acceleration = acceleration;
        this.date = date;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.start_speed = start_speed;
    }

}
