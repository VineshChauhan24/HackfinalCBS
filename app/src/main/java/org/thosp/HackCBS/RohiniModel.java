package org.thosp.HackCBS;

public class RohiniModel {

    private String maxSpeed;
    private String rpm;
    private String probablity;

    public RohiniModel(String maxSpeed, String rpm, String probablity) {
        super();
        this.maxSpeed = maxSpeed;
        this.rpm = rpm;
        this.probablity = probablity;
    }


    public String getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(String maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public String getRpm() {
        return rpm;
    }

    public void setRpm(String rpm) {
        this.rpm = rpm;
    }

    public String getProbablity() {
        return probablity;
    }

    public void setProbablity(String probablity) {
        this.probablity = probablity;
    }
}
