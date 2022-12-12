import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Novice extends AgentDecorator {

    private Graphics2D g2d;
    private Position position;

    //Constructor
    //I get the current agent position in the constructor so that rectengles(red, yellow, white) move with the agent badge
    public Novice(Agent agent) {
        super(agent);
        setAgentDecorator(agent);
        this.position = agent.getPosition();
    }

    //I needed this method to store and not to lose stolen money
    private void setStolen(){
        getAgentDecorator().setStolenMoney(getStolenMoney());
    }

    //Method to draw white box
    @Override
    public void draw(Graphics2D g2d) {
        this.g2d = g2d;
        drawBox();
    }

    //This method is for not losing the current location
    @Override
    public Position getPosition(){
        return position;
    }

    //If the condition is satisfied, white box is drawn.
    public void drawBox(){

        if(getAgentDecorator().getStolenMoney() > 2000){
            g2d.setColor(Color.WHITE);
            g2d.fillRect(position.getIntX(), position.getIntY()-50, 20,20 );
        }

        getAgentDecorator().draw(g2d); //I used this statement so that Basic Agent picture can be drawn, otherwise, since we
                                       //create Agents with a nested statement, Basic Agent picture is not drawn. I used this
                                       //in Master and Expert classes as well.
    }

    //I set the stolen money so that it does not get lost in the decorator. When I did not do this, the stolen money lost
    //while passing from expert to master.
    @Override
    public void step() {

        setStolen();
        getAgentDecorator().step();
    }
    // TODO
}