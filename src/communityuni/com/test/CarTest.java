package communityuni.com.test;

import communityuni.com.model.Car;
import communityuni.com.model.Road;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CarTest {
    Road road = new Road(0, 1, 5, "RIGHT");
    Car car = new Car(0, road);

    @Test
    void testMove(){
        car.move(1);
        assertEquals(new double[] {2, 0}, car.getPosition());
    }

    @Test
    void getLength() {
        assertEquals(1, car.getLength());
    }

    @Test
    void getBreadth() {
        assertEquals(0.5, car.getBreadth());
    }

    @Test
    void getSpeed() {
        assertEquals(0, car.getSpeed());
    }

    @Test
    void getPosition() {
        assertEquals(new double[] {1,0}, car.getPosition());
    }

    @Test
    void getRoad() {
        assertEquals(road, car.getCurrentRoad());
    }

    @Test
    void getId() {
        assertEquals("car_0", car.getId());
    }

}
