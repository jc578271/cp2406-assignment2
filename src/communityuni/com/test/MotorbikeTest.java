package communityuni.com.test;

import communityuni.com.model.Motorbike;
import communityuni.com.model.Road;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MotorbikeTest {
    Road road = new Road(0, 1, 5, "RIGHT");
    Motorbike motorbike = new Motorbike(0, road);

    @Test
    void testMove() {
        motorbike.move(1);
        assertEquals(new double[]{1.5, 0}, motorbike.getPosition());
    }

    @Test
    void getLength() {
        assertEquals(3, motorbike.getLength());
    }

    @Test
    void getBreadth() {
        assertEquals(0.5, motorbike.getBreadth());
    }

    @Test
    void getSpeed() {
        assertEquals(0, motorbike.getSpeed());
    }

    @Test
    void getPosition() {
        assertEquals(new double[]{0.5, 0}, motorbike.getPosition());
    }

    @Test
    void getRoad() {
        assertEquals(road, motorbike.getCurrentRoad());
    }

    @Test
    void getId() {
        assertEquals("motorbike_0", motorbike.getId());
    }
}
