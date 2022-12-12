import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class BasicAgent extends Agent {

    // TODO

    private BufferedImage image = null;
    private Graphics2D g2d;
    private int stepSize;

    //Constructor
    //I get the image in the constructor not to read it every single time
    public BasicAgent(String agentName, double x, double y) {
        super(x, y);
        setAgentName(agentName);
        try {
            image = ImageIO.read(getClass().getResource("/images/" + agentName + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //getter
    public Position getPosition(){
        return this.position;
    }

    //draw agent information like badge, name, state and stolen money
    public void mainDraw(){

        g2d.drawImage(image, getPosition().getIntX(), getPosition().getIntY(), 60, 60, null);
        g2d.setColor(Color.BLACK);
        g2d.drawString(String.format("%s", getAgentName()),this.getPosition().getIntX()+5, this.getPosition().getIntY()-10 );
        g2d.setColor(Color.BLUE);
        getState().drawState(this.getPosition().getIntX(), this.getPosition().getIntY(), g2d);
        g2d.setColor(Color.RED);
        g2d.drawString(String.format("%d", this.getStolenMoney()),this.getPosition().getIntX()+5, this.getPosition().getIntY()+90 );
    }

    //Draw method that calls mainDraw() above
    //It then generates state. If stepSize in the method does not satisfy the condition, state remains as it is.
    //Then related state's step is called to draw the positions continuously.
    @Override
    public void draw(Graphics2D g2d) {
        this.g2d = g2d;
        mainDraw();
        generateRandomState();
        getState().step(this);
    }

    //Step method that calls mainDraw() and step of the current state continuously.
    @Override
    public void step() {
        mainDraw();
        getState().step(this);
    }

    //Method to randomly generate state.
    //stepSize checks how often the states will change.
    //According to randomly generated number, related state is set.
    private void generateRandomState(){
        stepSize++;
        if (stepSize % 200 == 0) {
            int stateNo = Common.generateRandomInt(0, 3); //generate a random number for 4 possible states
            if (stateNo == 0)
                setState(new Rest());
            else if (stateNo == 1) {
                Shake s = new Shake(getPosition().getIntX(), getPosition().getIntY());
                if (getState().getName().equals("Shake"))
                    s.setFirstPosition(((Shake) getState()).getFirstPosition());
                else
                    s.setFirstPosition(new Position(this.getPosition().getIntX(), this.getPosition().getIntY()));
                setState(s);
            }
            else if (stateNo == 2)
                setState(new GotoXY(getPosition().getIntX(), getPosition().getIntY()));
            else if (stateNo == 3)
                setState(new ChaseClosest(getPosition().getIntX(), getPosition().getIntY()));
            stepSize = 0;
        }
    }

}