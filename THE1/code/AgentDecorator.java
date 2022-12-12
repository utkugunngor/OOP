import java.awt.*;

public abstract class AgentDecorator extends Agent {
    // TODO

    private Agent agentDecorator;

    //constructor
    public AgentDecorator(Agent agent){
        super(agent, agent.getPosition().getIntX(),agent.getPosition().getIntY());
        agentDecorator = agent;
    }

    public Agent getAgentDecorator() {return agentDecorator;}
    public void setAgentDecorator(Agent agentDecorator) {this.agentDecorator = agentDecorator;}
    public abstract void draw(Graphics2D g2d);
    public abstract void drawBox();

}