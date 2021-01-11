package communityuni.com.test;

import communityuni.com.model.Road;
import communityuni.com.model.TrafficLight;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrafficLightTest {
    Road road = new Road(1, 1, 5, "RIGHT");
    TrafficLight light = new TrafficLight(0, road);

    @Test
    void testOperate() {
        light.operate(1);
        ArrayList<String> list = new ArrayList<>();
        list.add("red");
        list.add("green");
        assertTrue(list.contains(light.getState()));
    }

    @Test
    void getRoad() {
        assertEquals(road, light.getRoadAttachedTo());
    }

    @Test
    void getPosition() {
        assertEquals(true, Arrays.equals(new double[] {5, 0}, light.getPosition()));
    }

    @Test
    void getId() {
        assertEquals("light_0", light.getId());
    }
}
