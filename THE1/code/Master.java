import java.awt.*;

public class Master extends AgentDecorator {

    private Graphics2D g2d;
    private Position position;

    //Constructor
    //I get the current agent position in the constructor so that rectengles(red, yellow, white) move with the agent badge
    public Master(Agent agent) {
        super(agent);
        setAgentDecorator(agent);
        this.position = agent.getPosition();
    }

    //This method is for not losing the current location
    @Override
    public Position getPosition(){
        return position;
    }

    //I needed this method to store and not to lose stolen money
    private void setStolen(){
        getAgentDecorator().setStolenMoney(getStolenMoney());
    }

    //Method to draw yellow box
    @Override
    public void draw(Graphics2D g2d) {
        this.g2d = g2d;
        drawBox();
    }

    //If the condition is satisfied, yellow box is drawn.
    public void drawBox(){
        if(getAgentDecorator().getStolenMoney() > 4000){
            g2d.setColor(Color.YELLOW);
            g2d.fillRect(position.getIntX() + 25, position.getIntY()-50, 20,20 );
        }

        getAgentDecorator().draw(g2d);
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