import java.awt.*;

public abstract class State {
    // TODO

    private String name;

    //Method to draw state name
    public void drawState(int x, int y, Graphics2D g2d) {
        g2d.drawString(getName(), x+5, y+75);
    }

    //getter and setter methods
    public String getName(){return name;}
    public void setName(String name) {this.name = name;}

    //Abstract method for every single state
    public abstract void step(Agent agent);
}