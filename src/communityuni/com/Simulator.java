package communityuni.com;

import communityuni.com.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Simulator implements ActionListener, Runnable, MouseListener {
    public static double framePerSecond;

    ArrayList<TrafficLight> lights = Map.lights;
    ArrayList<Vehicle> cars = Map.cars;
    ArrayList<Road> roads = Map.roads;
    Road road0 = new Road();
    int x, y;
    int i = -1;

    JFrame frame = new JFrame("Traffic sim");

    //North container
    Container north = new Container();
    JLabel info = new JLabel("click on screen to select x,y position");
    JLabel labelXPosField = new JLabel("x position");
    JTextField xPosField = new JTextField("0");
    JLabel labelYPosField = new JLabel("y position");
    JTextField yPosField = new JTextField("0");

    //South container
    Container south = new Container();
    Container south1 = new Container();
    Container south0 = new Container();
    public static boolean running = false;
    JButton startSim = new JButton("start");
    JButton removeRoad = new JButton("remove road");
    JButton removeLight= new JButton("remove light");
    JButton changeSpeed = new JButton("change speed limit");

    //West container
    Container west = new Container();
    JLabel roadInfo = new JLabel("Road info:");
    //road length
    JLabel labelLength = new JLabel("Enter road length");
    JTextField length = new JTextField("5");
    //road speed limit
    JLabel labelSpeed = new JLabel("Enter road speed limit");
    JTextField speed = new JTextField("1");
    //road direction selection
    ButtonGroup selections = new ButtonGroup();
    JRadioButton right = new JRadioButton("right");
    JRadioButton up = new JRadioButton("up");
    JRadioButton left = new JRadioButton("left");
    JRadioButton down = new JRadioButton("down");
    JButton addRoad = new JButton("add road");
    // add traffic light
    JLabel lightInfo = new JLabel("Click at screen to select attached road");
    JTextField indexAttachedRoad = new JTextField("-1");
    JButton addLight = new JButton("add light");
    // add vehicle
    JLabel vehicleInfo = new JLabel("Add vehicle:");
    JButton addCar = new JButton("add car");
    JButton addBus = new JButton("add bus");
    JButton addMotorbike = new JButton("add motorbike");

    private Simulator() {
        frame.setSize(1200,700);
        frame.setLayout(new BorderLayout());
        frame.add(road0, BorderLayout.CENTER);
        road0.addMouseListener(this);

        // north contain
        north.setLayout(new GridLayout(1, 5));
        north.add(info);
        north.add(labelXPosField);
        north.add(xPosField);
        north.add(labelYPosField);
        north.add(yPosField);
        frame.add(north, BorderLayout.NORTH);

        // south buttons
        south.setLayout(new GridLayout(1, 3));
        south.add(changeSpeed);
        changeSpeed.addActionListener(this);
        south.add(removeLight);
        removeLight.addActionListener(this);
        south.add(removeRoad);
        removeRoad.addActionListener(this);

        south1.setLayout(new GridLayout(1, 1));
        south1.add(startSim);
        startSim.addActionListener(this);

        south0.setLayout(new GridLayout(2, 1));
        south0.add(south);
        south0.add(south1);
        frame.add(south0, BorderLayout.SOUTH);


        // west buttons
        west.setLayout(new GridLayout(17,1));
        west.add(roadInfo);
        // road's length
        west.add(labelLength);
        west.add(length);
        length.addActionListener(this);
        // road's speed limit
        west.add(labelSpeed);
        west.add(speed);
        speed.addActionListener(this);
        // road's direction
        selections.add(right);
        selections.add(up);
        selections.add(left);
        selections.add(down);
        west.add(right);
        right.addActionListener(this);
        west.add(up);
        up.addActionListener(this);
        west.add(left);
        left.addActionListener(this);
        west.add(down);
        down.addActionListener(this);
        // add road
        west.add(addRoad);
        addRoad.addActionListener(this);

        // light's info
        west.add(lightInfo);
        west.add(indexAttachedRoad);
        indexAttachedRoad.addActionListener(this);
        indexAttachedRoad.setEditable(false);
        west.add(addLight);
        addLight.addActionListener(this);

        // add vehicle
        west.add(vehicleInfo);
        west.add(addCar);
        addCar.addActionListener(this);
        west.add(addBus);
        addBus.addActionListener(this);
        west.add(addMotorbike);
        addMotorbike.addActionListener(this);

        frame.add(west, BorderLayout.WEST);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.repaint();
    }

    public static void main(String[] args) {
        new Simulator();
    }

    public void arrangeCars() {
        for (int i = cars.size() - 1; i >= 0; i--) {
            Vehicle car = cars.get(i);
            double position = car.getPosition()[car.getDirectionIndex()];
            double length = car.getLength();
            int signSpeed = car.getSignSpeed();
            if (i > 0) {
                Vehicle prevCar = cars.get(i - 1);
                double prevPosition = prevCar.getPosition()[prevCar.getDirectionIndex()];
                double prevLength = prevCar.getLength();
                int prevSignSpeed = prevCar.getSignSpeed();

                if (
                    (signSpeed*position - length <= prevSignSpeed*prevPosition - prevLength
                    && prevSignSpeed*prevPosition - prevLength <= signSpeed*position)
                    || (signSpeed*position - length <= prevSignSpeed*prevPosition
                    && prevSignSpeed*prevPosition <= signSpeed*position)
                    || (signSpeed*position - length >= prevSignSpeed*prevPosition - prevLength
                    && signSpeed*position <= prevSignSpeed*prevPosition)
                ) {
                    prevCar.setPosition(car.getPosition()[car.getDirectionIndex()] + prevCar.getSignSpeed() * prevCar.getLength());
                }
            }
        }

    }

    public int closestRoadIndex(int x, int y, int scale) {
        int index = -1;
        int minRange = 6000;
        for (Road road : roads) {
            int roadX = (int) road.getEndLocation()[0] * scale;
            int roadY = (int) road.getEndLocation()[1] * scale;
            int range = (int) (Math.pow(roadX - x, 2) + Math.pow(roadY - y, 2));
            minRange = Math.min(minRange, range);
        }
        for (int i = 0; i < roads.size(); i++) {
            int roadX = (int) roads.get(i).getEndLocation()[0] * scale;
            int roadY = (int) roads.get(i).getEndLocation()[1] * scale;
            int range = (int) (Math.pow(roadX - x, 2) + Math.pow(roadY - y, 2));
            if (minRange == range) {
                index = i;
            }
        }

        return index;
    }

    public boolean isCoverRoad(Road road) {
        int rI = road.getDirectIndex();
        int rOI = road.getOppositeDirectIndex();
        int rMin = (int) Math.min(road.getStartLocation()[rI], road.getEndLocation()[rI]);
        int rMax = (int) Math.max(road.getStartLocation()[rI], road.getEndLocation()[rI]);
        for (Road roadEl: Map.roads) {
            int rElI = roadEl.getDirectIndex();
            int rElOI = roadEl.getOppositeDirectIndex();
            int rElMin = (int) Math.min(roadEl.getStartLocation()[rElI], roadEl.getEndLocation()[rElI]);
            int rElMax = (int) Math.max(roadEl.getStartLocation()[rElI], roadEl.getEndLocation()[rElI]);

            // if two roads on a line
            if (rI == rElI) {
                // if two roads cover each other
                if (road.getStartLocation()[rOI] == roadEl.getStartLocation()[rElOI]
                && (
                    ((rMin <= rElMin && rElMin < rMax)
                    || (rMin < rElMax && rElMax <= rMax))
                    || (road.getEndLocation()[rI] == roadEl.getEndLocation()[rElI])
                    )
                ) {
                    return true;
                }
            } else {
                // if two roads intersect at middle
                if (
                    (rElMin < (int)road.getStartLocation()[rOI] && (int)road.getStartLocation()[rOI] < rElMax
                    && rMin < (int)roadEl.getStartLocation()[rElOI] && (int)roadEl.getStartLocation()[rElOI] < rMax)
                    || (
                        road.getEndLocation()[rI] == roadEl.getEndLocation()[rElOI]
                        && rElMin <= road.getEndLocation()[rOI] && road.getEndLocation()[rOI] <= rElMax
                    ) || (
                        roadEl.getEndLocation()[rElI] == road.getEndLocation()[rOI]
                        && rMin < roadEl.getEndLocation()[rElOI] && roadEl.getEndLocation()[rElOI] < rMax
                    ) || (
                        roadEl.getStartLocation()[rElI] == road.getEndLocation()[rOI]
                        && rMin < roadEl.getStartLocation()[rElOI] && roadEl.getStartLocation()[rElOI] < rMax
                    )
                ) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if(source == startSim){
            if(!running) {
                running = true;
                Thread t = new Thread(this);
                t.start();
            }
        }

        if (source == changeSpeed) {
            String speedStr = JOptionPane.showInputDialog(changeSpeed, "Input new speed", null);
            try {
                int speed = Integer.parseInt(speedStr);
                int indexRoad = Integer.parseInt(this.indexAttachedRoad.getText());
                roads.get(indexRoad).setSpeedLimit(speed);
                frame.repaint();
            } catch (Exception error) {
                JOptionPane.showMessageDialog(null, "Invalid input");
            }

        }

        if (source == removeRoad) {
            int indexRoad;
            try {
                indexRoad = Integer.parseInt(this.indexAttachedRoad.getText());
                if (!roads.get(indexRoad).getConnectedRoads().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "cannot remove this road");
                } else if (running) {
                    JOptionPane.showMessageDialog(null, "cannot remove road when cars running");
                } else {
                    lights.removeIf(light -> light.getRoadAttachedTo() == roads.get(indexRoad));
                    for (Road road:roads) road.getConnectedRoads().remove(roads.get(indexRoad));
                    roads.remove(indexRoad);
                    frame.repaint();
                }
            } catch (Exception error) {
                JOptionPane.showMessageDialog(null, "no road selected");
            }
        }

        if(source == addRoad) {
            int i = roads.size();
            int length = 5;
            int speed = 1;
            String direction = "right";
            if (right.isSelected()) direction = "right";
            if (up.isSelected()) direction = "up";
            if (left.isSelected()) direction = "left";
            if (down.isSelected()) direction = "down";

            try {
                length = Integer.parseInt(this.length.getText());
                speed = Integer.parseInt(this.speed.getText());
                int indexRoad = Integer.parseInt(this.indexAttachedRoad.getText());

                Road road = new Road(i, speed, length, direction);
                Road connectedRoad = null;
                // set startLocation and connectedRoads
                if (i > 0) {
                    connectedRoad = indexRoad == -1 ? roads.get(i-1) : roads.get(indexRoad);
                    road.setStartLocation(connectedRoad.getEndLocation());
                }
                if (!isCoverRoad(road)) {
                    if (i > 0) {
                        connectedRoad.getConnectedRoads().add(road);
                    }
                    roads.add(road);
                    frame.repaint();
                } else if (running) {
                    JOptionPane.showMessageDialog(null, "cannot remove road when cars running");
                } else {
                    JOptionPane.showMessageDialog(null, "road covered");
                }
            } catch (Exception error) {
                JOptionPane.showMessageDialog(null, "road's speed and length needs an integer");
                this.length.setText("5");
            }
        }

        if (source == removeLight) {
            int indexRoad;
            try {
                indexRoad = Integer.parseInt(this.indexAttachedRoad.getText());
                if (indexRoad > roads.size() - 1 || indexRoad < 0) {
                    JOptionPane.showMessageDialog(null, "no road selected");
                    this.indexAttachedRoad.setText("0");
                } else {
                    TrafficLight light = lights.stream()
                            .filter(lightEl -> roads.indexOf(lightEl.getRoadAttachedTo()) == indexRoad)
                            .findAny().orElse(null);
                    if (light != null) {
                        light.getRoadAttachedTo().getLightsOnRoad().remove(light);
                        lights.remove(light);
                        frame.repaint();
                    } else {
                        JOptionPane.showMessageDialog(null, "no light found on this road");
                    }
                }
            } catch (Exception error) {
                JOptionPane.showMessageDialog(null, "road's index needs an integer");
                this.indexAttachedRoad.setText("0");
            }
        }

        if (source == addLight) {
            int i = lights.size();
            int indexRoad;
            try {
                indexRoad = Integer.parseInt(this.indexAttachedRoad.getText());
                List<Integer> roadHadLightIds = roads.stream()
                        .filter(road -> !road.getLightsOnRoad().isEmpty())
                        .map(road -> roads.indexOf(road))
                        .collect(Collectors.toList());
                if (indexRoad > roads.size() - 1 || indexRoad < 0) {
                    JOptionPane.showMessageDialog(null, "no road selected");
                } else if (roadHadLightIds.contains(indexRoad)) {
                    JOptionPane.showMessageDialog(null, "this road had light already");
                } else {
                    TrafficLight light = new TrafficLight(i, roads.get(indexRoad));
                    lights.add(light);
                    frame.repaint();
                }
            } catch (Exception error) {
                JOptionPane.showMessageDialog(null, "road's index needs an integer");
                this.indexAttachedRoad.setText("0");
            }
        }

        if (source == addCar) {
            i++;

            if (roads.isEmpty()) {
                JOptionPane.showMessageDialog(null, "no road found");
            } else  {
                Vehicle car = new Car(i, roads.get(0));
                cars.add(car);
                if (!running) {
                    arrangeCars();
                }
                frame.repaint();
            }
        }

        if (source == addBus) {
            i++;

            if (roads.isEmpty()) {
                JOptionPane.showMessageDialog(null, "no road found");
            } else  {
                Vehicle bus = new Bus(i, roads.get(0));
                cars.add(bus);
                if (!running) {
                    arrangeCars();
                }
                frame.repaint();
            }
        }

        if (source == addMotorbike) {
            i++;

            if (roads.isEmpty()) {
                JOptionPane.showMessageDialog(null, "no road found");
            } else  {
                Vehicle motorbike = new Motorbike(i, roads.get(0));
                cars.add(motorbike);
                if (!running) {
                    arrangeCars();
                }
                frame.repaint();
            }
        }
    }

    @Override
    public void run() {
        // --Simulator...--
        System.out.println("--Start moving--");
        framePerSecond =0.01;
        double time = 0;
        int lightTime = 0;
        while (running) {
            // Find the min period of every car's move
            double minPeriod = Double.MAX_VALUE;
            List<Vehicle> toRemoveCars = new ArrayList<>();
            for (Vehicle car : cars) {
                minPeriod = Math.min(car.getPeriod(toRemoveCars), minPeriod);

                // Print light's state
                if (!car.getCurrentRoad().getLightsOnRoad().isEmpty()
                        && Arrays.equals(car.getPosition(), car.getCurrentRoad().getEndLocation()) ) {
                    TrafficLight light = car.getCurrentRoad().getLightsOnRoad().get(0);
                    System.out.println(light.getId()+": "+light.getState().toUpperCase()+", "+car.getId() + " at the light");
                }
            }

            // Cars moving...
            boolean finishedCars = true;
            for (Vehicle car: cars) {
                // Print car's info
                car.move(minPeriod);
                System.out.println(car.bio());
                // All cars have to be finished
                finishedCars = finishedCars && car.isFinished();
            }
            time += minPeriod*framePerSecond;
            lightTime++;

            // Lights operate
            for (TrafficLight light : lights) {
                light.operate(lightTime);
            }
            System.out.println(minPeriod*framePerSecond);
            System.out.println(time+"s passed");
            System.out.println();

            cars.removeAll(toRemoveCars);

            if (finishedCars) running = false;

            frame.repaint();

            // Delay
            try {
                Thread.sleep((long) (minPeriod*1000*framePerSecond));
            } catch (InterruptedException ignored) {}
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        x = e.getX();
        y = e.getY();
        xPosField.setText(Integer.toString(x));
        yPosField.setText(Integer.toString(y));

        int indexRoad = closestRoadIndex(x, y, 30);
        indexAttachedRoad.setText(Integer.toString(indexRoad));
        for (Road road: roads) road.setSelected(false);

        if (closestRoadIndex(x, y, 30) != -1) {
            roads.get(indexRoad).setSelected(true);
            frame.repaint();

            addLight.setText(String.format("add light on road_%d", indexRoad));
            removeLight.setText(String.format("remove light on road_%d", indexRoad));
            addRoad.setText(String.format("add road at road_%d", indexRoad));
            removeRoad.setText(String.format("remove road_%d", indexRoad));
            changeSpeed.setText(String.format("change speed limit of road_%d", indexRoad));
        } else {
            for (Road road: roads) road.setSelected(false);
            frame.repaint();

            addLight.setText("add light");
            removeLight.setText("remove light");
            addRoad.setText("add road");
            removeRoad.setText("remove road");
            changeSpeed.setText("change speed limit");
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
