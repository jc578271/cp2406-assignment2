package communityuni.com.model;

import java.awt.*;
import java.util.Arrays;

public class Bus extends Vehicle {

    public Bus(int id, Road currentRoad) {
        super(id, currentRoad);
        this.id = String.format("bus_%d", id);
        this.length = 3 * super.length;
        this.position = getInitialPosition(this.length);
    }

    @Override
    public void draw(Graphics g, int scale) {
        g.setColor(Color.GREEN);

        int directSign = super.signSpeed == -1 ? 0 : -1;

        int x = (int) (super.getDirectionIndex() == 0 ? ((position[0] + directSign*length) * scale) : (position[0]-1 + 0.25) * scale);
        int y = (int) (super.getDirectionIndex() == 0 ? ((position[1]+0.25) * scale) : (position[1] + 1 + directSign*length) * scale);
        if (getDirectionIndex() == 1 && getSignSpeed() == 1) {
            x = (int) ((position[0] + 0.25) * scale);
        } else if (getDirectionIndex() == 0 && getSignSpeed() == -1) {
            y = (int) ((position[1]+1.25) * scale);
        }

        if (super.getLaneIndex() == 1) {
            if (getDirectionIndex() == 1 && getSignSpeed() == -1) x = x + scale;
            if (getDirectionIndex() == 0 && getSignSpeed() == 1) y = y + scale;
            if (getDirectionIndex() == 1 && getSignSpeed() == 1) x = x - scale;
            if (getDirectionIndex() == 0 && getSignSpeed() == -1) y = y - scale;
        }

        int width = super.getDirectionIndex() == 0 ? (int) (super.length * scale) : (int) (super.getBreadth() * scale);
        int height= super.getDirectionIndex() == 0 ? (int) (super.getBreadth() * scale): (int) (super.length * scale);
        g.fillRect(x , y, width, height);
        g.fillRect(x , y, width, height);
        g.setColor(Color.black);
        g.drawRect(x , y, width, height);
    }

    @Override
    public String bio() {
        return id +
                ", position: " + Arrays.toString(position) +
                ", speed: " + super.getSpeed() + " m/s" +
                ", currentRoad: " + super.getCurrentRoad().getId()+
                ", currentLane: " + (super.getCurrentRoad().getCarsOnLane0().contains(this) ? "lane0"
                : super.getCurrentRoad().getCarsOnLane1().contains(this) ? "lane1" : "null");
    }
}
