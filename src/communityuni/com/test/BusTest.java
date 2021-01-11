package communityuni.com.test;

import communityuni.com.model.Bus;
import communityuni.com.model.Road;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BusTest {
    Road road = new Road(0, 1, 5, "RIGHT");
    Bus bus = new Bus(0, road);

    @Test
    void testMove() {
        bus.move(1);
        assertEquals(new double[] {4, 0}, bus.getPosition());
    }

    @Test
    void getLength() {
        assertEquals(3, bus.getLength());
    }

    @Test
    void getBreadth() {
        assertEquals(0.5, bus.getBreadth());
    }

    @Test
    void getSpeed() {
        assertEquals(0, bus.getSpeed());
    }

    @Test
    void getPosition() {
        assertEquals(new double[] {3,0}, bus.getPosition());
    }

    @Test
    void getRoad() {
        assertEquals(road, bus.getCurrentRoad());
    }

    @Test
    void getId() {
        assertEquals("bus_0", bus.getId());
    }
}
