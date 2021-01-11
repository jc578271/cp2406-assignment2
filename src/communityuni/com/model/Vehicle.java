package communityuni.com.model;

import communityuni.com.Simulator;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Vehicle {
    private boolean finished;
    protected String id;
    protected double length;
    private double breadth;
    private int speed;
    protected int signSpeed;
    protected double[] position;
    private int directionIndex;
    private int oppositeDirectIndex;
    private int laneIndex;
    private Road currentRoad;
    private int order;
    private int randNextRoad;

    public Vehicle(int id, Road currentRoad) {
        this.order = id;
        this.id = "000";
        this.length = 1;
        this.breadth = length/2;
        this.speed = 0;
        currentRoad.getCarsOnRoad().add(this);
        this.currentRoad = currentRoad;
        this.signSpeed = this.currentRoad.getDirectSign() == 0 ? 1 : -1;
        this.currentRoad.getCarsOnLane0().add(this);
        this.laneIndex = currentRoad.getCarsOnLane0().contains(this) ? 0 : 1;
        this.finished = false;
    }

    public Vehicle() {
        this.id = "000";
        this.length = 1;
        this.breadth = length/2;
        this.speed = 0;
        this.finished = false;
    }

    public double[] getInitialPosition(double length) {
        switch (this.currentRoad.getDirection()) {
            case "RIGHT" -> {
                directionIndex = 0;
                oppositeDirectIndex = 1;
                signSpeed = 1;
                return new double[]{length + currentRoad.getStartLocation()[0], currentRoad.getStartLocation()[1]};
            }
            case "LEFT" -> {
                directionIndex = 0;
                oppositeDirectIndex = 1;
                signSpeed = -1;
                return new double[]{-length + currentRoad.getStartLocation()[0], currentRoad.getStartLocation()[1]};
            }
            case "UP" -> {
                directionIndex = 1;
                oppositeDirectIndex = 0;
                signSpeed = -1;
                return new double[]{currentRoad.getStartLocation()[0], -length + currentRoad.getStartLocation()[1]};
            }
            default -> {
                directionIndex = 1;
                oppositeDirectIndex = 0;
                signSpeed = 1;
                return new double[]{currentRoad.getStartLocation()[0], +length + currentRoad.getStartLocation()[1]};
            }
        }
    }

    private void carCollision (int speed) {
        setSpeed(speed);

        // make every car not cover each other
        if (!currentRoad.getCarsOnLanes()[laneIndex].isEmpty()) {
            int oppositeLaneIndex = laneIndex == 0 ? 1 : 0;
            List<Vehicle> toRemove = new ArrayList<>();
            // cars' length on lane 0
            double carsLength0 = 0;
            for (Vehicle car : currentRoad.getCarsOnLanes()[laneIndex]) {
                carsLength0 += car.length;

                if (!id.equals(car.id)) {
                    if (Simulator.running && order > car.order
                        && (
                            (signSpeed * position[directionIndex] - length <= car.signSpeed * car.position[car.directionIndex] - car.length
                            && car.signSpeed * car.position[car.directionIndex] - car.length < signSpeed * position[directionIndex])
                            || (signSpeed * position[directionIndex] - length < car.signSpeed * car.position[car.directionIndex]
                            && car.signSpeed * car.position[car.directionIndex] <= signSpeed * position[directionIndex])
                            || (signSpeed * position[directionIndex] - length >= car.signSpeed * car.position[car.directionIndex] - car.length
                            && signSpeed * position[directionIndex] <= car.signSpeed * car.position[car.directionIndex])
                        )
                    ) {
                        System.out.println(id+"-"+car.id);
                        setSpeed(0);
                    }

                    // if forward object is back of other car
                    if (roundAvoid(position[directionIndex]) == roundAvoid(car.position[car.directionIndex] - car.signSpeed * car.length)
                    ) {
                        // cars' length on lane 2
                        double carsLength1 = 0;
                        for (Vehicle carOnLane : currentRoad.getCarsOnLanes()[oppositeLaneIndex]) {
                            carsLength1 += carOnLane.length;
                        }

                        // car will move to other lane if cars' length shorter
                        if (position[directionIndex] - signSpeed * length != Map.roads.get(0).getStartLocation()[directionIndex]
                            && carsLength1 < carsLength0) {
                            toRemove.add(this);
                            currentRoad.getCarsOnLanes()[oppositeLaneIndex].add(this);
                            setSpeed(speed);
                        } else {
                            setSpeed(car.speed);
                        }
                    }
                }
            }
            currentRoad.getCarsOnLanes()[laneIndex].removeAll(toRemove);
        }
    }

    public double getPeriod(List<Vehicle> toRemoveCars) {
        ArrayList<TrafficLight> currentLights = currentRoad.getLightsOnRoad();

        // if car is at the end of road
        if (roundAvoid(position[directionIndex]) == currentRoad.getEndLocation()[directionIndex]
        ) {
            if (!currentLights.isEmpty() && currentLights.get(0).getState().equals("red")) {
                setSpeed(0);
            } else {
                if (!currentRoad.getConnectedRoads().isEmpty()) {
                    randNextRoad = new Random().nextInt(currentRoad.getConnectedRoads().size());

                    currentRoad.getConnectedRoads().get(randNextRoad).getCarsOnLanes()[laneIndex].add(this);
                    setSpeed(currentRoad.getConnectedRoads().get(randNextRoad).getSpeedLimit());
                    setDirectionIndex(currentRoad.getConnectedRoads().get(randNextRoad));
                    setSignSpeed(currentRoad.getConnectedRoads().get(randNextRoad));
                } else {
                    setSpeed(currentRoad.getSpeedLimit());
                }
            }
        // if car's back is at the end of road
        } else if (roundAvoid(position[directionIndex] - signSpeed * length) == currentRoad.getEndLocation()[directionIndex]) {
            if (currentRoad.getConnectedRoads().isEmpty()) {
                setSpeed(0);
                setFinished(true);
                currentRoad.removeCarsOnRoad(this);
                toRemoveCars.add(this);
            } else {
                currentRoad.getConnectedRoads().get(randNextRoad).getCarsOnLanes()[laneIndex].remove(this);
                currentRoad.removeCarsOnRoad(this);
                setCurrentRoad(currentRoad.getConnectedRoads().get(randNextRoad));
                currentRoad.addCarsOnRoad(this);
            }
        } else {
            if (!currentRoad.getConnectedRoads().isEmpty()
                && signSpeed * currentRoad.getEndLocation()[directionIndex] < signSpeed * position[directionIndex]
                && signSpeed * position[directionIndex] - length < signSpeed * currentRoad.getEndLocation()[directionIndex]
            ) {
                carCollision(currentRoad.getConnectedRoads().get(0).getSpeedLimit());
            } else {
                carCollision(currentRoad.getSpeedLimit());
            }
        }

        this.laneIndex = currentRoad.getCarsOnLane0().contains(this) ? 0: 1;

        // Period of every move
        double period;
        double framePerSecond = Simulator.framePerSecond;

        // if object is about end of road
        if ((signSpeed * position[directionIndex] + speed * framePerSecond > signSpeed * currentRoad.getEndLocation()[directionIndex]
                && signSpeed * position[directionIndex] < signSpeed * currentRoad.getEndLocation()[directionIndex])
        ) {
            period = Math.abs((currentRoad.getEndLocation()[directionIndex] - position[directionIndex]) / (speed * framePerSecond));
        // if object's back is about end of road
        } else if(signSpeed * position[directionIndex] + (speed * framePerSecond - length) > signSpeed * currentRoad.getEndLocation()[directionIndex]
                && signSpeed * position[directionIndex] - length < signSpeed * currentRoad.getEndLocation()[directionIndex]) {
            period = Math.abs((currentRoad.getEndLocation()[directionIndex] - position[directionIndex] + signSpeed*length) / (speed * framePerSecond));
        } else {
            period = 1;
        }

        // if object is about at back of other car
        for (Vehicle car : currentRoad.getCarsOnLanes()[laneIndex]) {
            if (!id.equals(car.id) ) {
                if ((signSpeed * (roundAvoid(position[directionIndex] + signSpeed * speed * framePerSecond)) > car.signSpeed * (roundAvoid(car.position[directionIndex] - car.signSpeed*car.length))
                    && signSpeed * (roundAvoid(position[directionIndex])) < signSpeed * (roundAvoid(car.position[directionIndex] - car.signSpeed*car.length)))
                ) {
                    period = Math.abs((car.position[directionIndex] - car.signSpeed*car.length - position[directionIndex]) / (speed * framePerSecond));
                }
            }
        }

        return period;
    }

    private double roundAvoid(double value) {
        double scale = Math.pow(10, 2);
        return Math.round(value * scale) / scale;
    }

    public void move(double period) {
        // car moving...
        setPosition(roundAvoid(position[directionIndex] + signSpeed * speed * period * Simulator.framePerSecond));
    }

    public abstract String bio();

    public abstract void draw(Graphics g, int scale);

    public String getId() {
        return id;
    }
    public void setId(int id) {
        this.id = String.format("car_%d",id);
    }
    public double getLength() {
        return length;
    }
    public void setLength(double length) {
        this.length = length;
    }
    public double getBreadth() {
        return breadth;
    }
    public void setBreadth(double breadth) {
        this.breadth = breadth;
    }
    public int getSpeed() {
        return speed;
    }
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    public double[] getPosition() {
        return position;
    }
    public void setPosition(double position) {
        if (directionIndex == 0) {
            this.position = new double[] {position, this.position[1]};
        } else {
            this.position = new double[] {this.position[0], position};
        }

    }
    public Road getCurrentRoad() {
        return currentRoad;
    }
    public void setCurrentRoad(Road currentRoad) {
        this.currentRoad = currentRoad;
    }
    public boolean isFinished() {
        return finished;
    }
    public void setFinished(boolean finished) {
        this.finished = finished;
    }
    public int getDirectionIndex() {
        return directionIndex;
    }
    public void setDirectionIndex(Road currentRoad) {
        this.directionIndex = (
            currentRoad.getDirection().equals("RIGHT")
            || currentRoad.getDirection().equals("LEFT")
        ) ? 0 : 1;

        this.oppositeDirectIndex = this.directionIndex == 0 ? 1 : 0;
    }
    public int getLaneIndex() {
        return laneIndex;
    }
    public void setLaneIndex(int laneIndex) {
        this.laneIndex = laneIndex;
    }
    public int getSignSpeed() {
        return signSpeed;
    }
    public void setSignSpeed(Road currentRoad) {
        this.signSpeed = (
            currentRoad.getDirection().equals("RIGHT")
            || currentRoad.getDirection().equals("DOWN")
        ) ? 1 : -1;
    }

    public int getOppositeDirectIndex() {
        return oppositeDirectIndex;
    }

    public void setOppositeDirectIndex(int oppositeDirectIndex) {
        this.oppositeDirectIndex = oppositeDirectIndex;
    }
}
