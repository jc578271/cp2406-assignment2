package communityuni.com.model;

import java.awt.*;
import java.util.Arrays;
import java.util.Random;

public class TrafficLight {
    private String id;
    private String state = "green";
    private double[] position;
    private Road roadAttachedTo;

    public TrafficLight(int id, Road roadAttachedTo) {
        this.id = String.format("light_%d",id);
        this.position = roadAttachedTo.getEndLocation();
        roadAttachedTo.getLightsOnRoad().add(this);
        this.roadAttachedTo = roadAttachedTo;
    }

    public TrafficLight() {
        this.id = "light_0";
    }

    public void operate(int time) {
        if (time % 100 == 0) {
            Random rd = new Random();
            if (rd.nextDouble() >= 0.5) {
                setState("red");
            } else {
                setState("green");
            }
        }
    }

    public void draw(Graphics g, int scale) {
        g.setColor(state.equals("green") ? Color.GREEN : Color.RED);
        int x = (int) ((position[0] + (roadAttachedTo.getDirectSign() == 0 ? -0.2 : -1)) * scale);
        int y = (int) ((position[1] + (roadAttachedTo.getDirectSign() == 0 ? 0 : 1)) * scale);

        if (roadAttachedTo.getDirection().equals("LEFT")) {
            x = x + (int)(scale);
            y = y + (int)(0.5*scale);
        }
        if (roadAttachedTo.getDirection().equals("DOWN")) {
            x = x + (int)(0.7*scale);
            y = y + (int)(0.8*scale);
        }

        int width = (int) ((roadAttachedTo.getDirectIndex() == 0 ? 0.2 : 0.5) * scale);
        int height= (int) ((roadAttachedTo.getDirectIndex() == 0 ? 0.5 : 0.2) * scale);
        g.fillRect(x, y, width, height);
    }

    public String bio() {
        return  id +
                ", state: " +state.toUpperCase()+
                ", position: " + Arrays.toString(position) +
                ", roadAttachedTo: " + roadAttachedTo.getId();
    }

    public String getId() {
        return id;
    }
    public void setId(int id) {
        this.id = String.format("light_%d",id);
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public double[] getPosition() {
        return position;
    }
    public void setPosition(double[] position) {
        this.position = position;
    }
    public Road getRoadAttachedTo() {
        return roadAttachedTo;
    }
    public void setRoadAttachedTo(Road roadAttachedTo) {
        this.roadAttachedTo = roadAttachedTo;
    }
}
