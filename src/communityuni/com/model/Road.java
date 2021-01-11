package communityuni.com.model;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Road extends JPanel {
    private String id;
    private int speedLimit;
    private double length;
    private double[] startLocation;
    private double[] endLocation;
    private String direction;
    private int directIndex;
    private int oppositeDirectIndex;
    private int directSign;
    private boolean isSelected = false;
    private ArrayList<Road> connectedRoads = new ArrayList<>();
    private ArrayList<TrafficLight> lightsOnRoad = new ArrayList<>();
    private ArrayList<Vehicle> carsOnRoad = new ArrayList<>();
    private ArrayList<Vehicle> carsOnLane0 = new ArrayList<>();
    private ArrayList<Vehicle> carsOnLane1 = new ArrayList<>();
    private ArrayList<Vehicle>[] carsOnLanes = new ArrayList[] {carsOnLane0, carsOnLane1};

    public Road(int id, int speedLimit, int length, String direction) {
        this.id = String.format("road_%d",id);
        this.speedLimit = speedLimit;
        this.length = length;
        this.direction = direction.toUpperCase();
        this.startLocation = new double[]{1, 9};
        switch (this.direction) {
            case "UP" -> this.endLocation = new double[]{this.startLocation[0], this.startLocation[1] - this.length};
            case "RIGHT" -> this.endLocation = new double[]{this.startLocation[0] + this.length, this.startLocation[1]};
            case "LEFT" -> this.endLocation = new double[]{this.startLocation[0] - this.length, this.startLocation[1]};
            default -> this.endLocation = new double[]{this.startLocation[0], this.startLocation[1] + this.length};
        }
        this.directIndex = (this.direction.equals("RIGHT") || this.direction.equals("LEFT")) ? 0 : 1;
        this.oppositeDirectIndex = directIndex == 0 ? 1 : 0;
        this.directSign = (this.direction.equals("DOWN") || this.direction.equals("RIGHT")) ? 0 : -1;
    }

    public Road() {
        this.id = "road_0";
        this.speedLimit = 0;
        this.length = 0;
        this.startLocation = new double[]{0, 0};
        this.endLocation = new double[]{getLength()+getStartLocation()[0], 0};
    }

    public void addCarsOnRoad(Vehicle car) {
        this.carsOnRoad.add(car);
        this.carsOnLanes[car.getLaneIndex()].add(car);
    }

    public void removeCarsOnRoad(Vehicle car) {
        this.carsOnRoad.remove(car);
        this.carsOnLanes[car.getLaneIndex()].remove(car);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 1200, 1200);
        int scale = 30;
        for (int i = Map.roads.size() -1; i >= 0; i--) {
            Map.roads.get(i).draw(g, scale);
            for (int j = Map.cars.size()-1; j >= 0 ; j--) {
                Map.cars.get(j).draw(g, scale);
            }

            for (TrafficLight light: Map.lights) {
                light.draw(g, scale);
            }
        }
    }

    public void draw(Graphics g, int scale) {
        int roadX = (int) (directIndex == 0 ? (startLocation[0] + directSign*length) * scale : (startLocation[0] - 1) * scale);
        int roadY = (int) (directIndex == 0 ? startLocation[1] * scale : (startLocation[1] + 1 + directSign*length) * scale);
        int roadWidth = directIndex == 0 ? (int) ((length) * scale) : 2 * scale;
        int roadHeight = directIndex == 0 ? 2 * scale: (int) ((length) * scale);
        g.setColor(Color.GRAY);
        g.fillRect(roadX, roadY, roadWidth, roadHeight);

        if (isSelected) {
            g.setColor(Color.green);
        } else {
            g.setColor(Color.black);
        }
        g.drawRect(roadX, roadY, roadWidth, roadHeight);

        for (int i = 0; i < (int) length; i++) {
            int laneX = (roadX + (directIndex == 0 ? i : 1) * scale);
            int laneY = (roadY + (directIndex == 0 ? 1 : i) * scale);
            int laneWidth = directIndex == 0 ? (int)(scale*0.5): 2;
            int laneHeight = directIndex == 0 ? 2 : (int)(scale*0.5);
            g.setColor(Color.WHITE);
            g.fillRect(laneX, laneY, laneWidth, laneHeight);
        }

    }

    public String bio() {
        return "Road{" +
                "id=" + id +
                ", speedLimit=" + speedLimit +
                ", length=" + length +
                ", startLocation=" + Arrays.toString(startLocation) +
                ", endLocation=" + Arrays.toString(endLocation) +
                ", connectedRoads=" + connectedRoads.stream().map(Road::getId).collect(Collectors.toList()) +
                ", lightsOnRoad=" + lightsOnRoad.stream().map(TrafficLight::getId).collect(Collectors.toList()) +
                ", carsOnRoad=" + carsOnRoad.stream().map(Vehicle::getId).collect(Collectors.toList()) +
                '}';
    }

    public ArrayList<Vehicle> getCarsOnLane0() {
        return carsOnLane0;
    }
    public void setCarsOnLane0(ArrayList<Vehicle> carsOnLane) {
        this.carsOnLane0 = carsOnLane;
    }
    public ArrayList<Vehicle> getCarsOnLane1() {
        return carsOnLane1;
    }
    public void setCarsOnLane1(ArrayList<Vehicle> carsOnLane) {
        this.carsOnLane1 = carsOnLane;
    }
    public String getId() {
        return id;
    }
    public void setId(int id) {
        this.id = String.format("road_%d",id);
    }
    public int getSpeedLimit() {
        return speedLimit;
    }
    public void setSpeedLimit(int speedLimit) {
        this.speedLimit = speedLimit;
    }
    public double getLength() {
        return length;
    }
    public void setLength(double length) {
        this.length = length;
    }
    public double[] getStartLocation() {
        return startLocation;
    }
    public void setStartLocation(double[] startLocation) {
        this.startLocation = startLocation;
        switch (this.direction) {
            case "UP" -> this.endLocation = new double[]{this.startLocation[0], this.startLocation[1] - this.length};
            case "RIGHT" -> this.endLocation = new double[]{this.startLocation[0] + this.length, this.startLocation[1]};
            case "LEFT" -> this.endLocation = new double[]{this.startLocation[0] - this.length, this.startLocation[1]};
            default -> this.endLocation = new double[]{this.startLocation[0], this.startLocation[1] + this.length};
        }
    }
    public double[] getEndLocation() {
        return endLocation;
    }
    public ArrayList<Road> getConnectedRoads() {
        return connectedRoads;
    }
    public void setConnectedRoads(ArrayList<Road> connectedRoads) {
        this.connectedRoads = connectedRoads;
    }
    public ArrayList<TrafficLight> getLightsOnRoad() {
        return lightsOnRoad;
    }
    public void setLightsOnRoad(ArrayList<TrafficLight> lightsOnRoad) {
        this.lightsOnRoad = lightsOnRoad;
    }
    public ArrayList<Vehicle> getCarsOnRoad() {
        return carsOnRoad;
    }
    public void setCarsOnRoad(ArrayList<Vehicle> carsOnRoad) {
        this.carsOnRoad = carsOnRoad;
    }
    public String getDirection() {
        return direction;
    }
    public void setDirection(String direction) {
        this.direction = direction;
    }
    public ArrayList<Vehicle>[] getCarsOnLanes() {
        return carsOnLanes;
    }
    public void setCarsOnLanes(ArrayList<Vehicle>[] carsOnLanes) {
        this.carsOnLanes = carsOnLanes;
    }
    public int getDirectIndex() {
        return directIndex;
    }
    public void setDirectIndex(int directIndex) {
        this.directIndex = directIndex;
    }
    public int getDirectSign() {
        return directSign;
    }
    public void setDirectSign(int directSign) {
        this.directSign = directSign;
    }

    public int getOppositeDirectIndex() {
        return oppositeDirectIndex;
    }

    public void setOppositeDirectIndex(int oppositeDirectIndex) {
        this.oppositeDirectIndex = oppositeDirectIndex;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
