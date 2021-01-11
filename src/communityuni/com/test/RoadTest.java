package communityuni.com.test;

import communityuni.com.model.Car;
import communityuni.com.model.Road;
import communityuni.com.model.TrafficLight;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class RoadTest {
    Road road = new Road(0, 10, 123, "RIGHT");
    TrafficLight light = new TrafficLight(0, road);

    @Test
    public void getId() {
        assertEquals("road_0", road.getId());
    }

    @Test
    public void getSpeedLimit() {
        assertEquals(10, road.getSpeedLimit());
    }

    @Test
    public void getLength() {
        assertEquals(123, (int) road.getLength());
    }

    @Test
    public void getStartLocation() {
        assertEquals(true, Arrays.equals(new double[]{0.0, 0.0}, road.getStartLocation()));
    }

    @Test
    public void getEndLocation() {
        assertEquals(true, Arrays.equals(new double[]{123.0, 0.0}, road.getEndLocation()));
    }

    @Test
    public void getCars() {
        ArrayList<Car> expected = new ArrayList<>();
        assertEquals(expected, road.getCarsOnRoad());
    }

    @Test
    public void getLights() {
        ArrayList<TrafficLight> expected = new ArrayList<>();
        expected.add(light);
        assertEquals(expected, road.getLightsOnRoad());
    }

    @Test
    public void getConnectedRoads() {
        ArrayList<Road> expected = new ArrayList<>();
        assertEquals(expected, road.getConnectedRoads());
    }
}
