import java.awt.*;

public abstract class Agent extends Entity {

    // TODO
    protected String agentName;
    protected State state;
    protected int stolenMoney;


    //constructor just with coordinates since Basic Agent cannot use the next constructor
    public Agent(double x, double y){
        super(x, y);
        state = new Rest();
        stolenMoney = getStolenMoney();
    }

    //constructor for Agent Decorator with Agent type parameter
    public Agent(Agent agent, double x, double y) {
        super(x, y);
        this.agentName = agent.getAgentName();
        this.state = agent.getState();
        this.stolenMoney = agent.getStolenMoney();
    }

    //getter and setters
    public String getAgentName() {return agentName;}
    public void setAgentName(String agentName) {this.agentName = agentName;}
    public State getState() {return state;}
    public void setState(State state) {this.state = state;}
    public int getStolenMoney() {return stolenMoney;}
    public void setStolenMoney(int stolenMoney) {
        this.stolenMoney = stolenMoney;
    }

    public Country getCountry(){
        if(this.agentName.equals("MSS")) {return Common.getChina();}
        else if(this.agentName.equals("CIA")) {return Common.getUsa();}
        else if(this.agentName.equals("MIT")) {return Common.getTurkey();}
        else if(this.agentName.equals("MOSSAD")) {return Common.getIsrael();}
        else {return Common.getRussia();}
    }

    //method to add stolen money to the agent
    public void addStolen(int stolenMoney){
        this.stolenMoney += stolenMoney;
    }

    public abstract void step();
    public abstract void draw(Graphics2D g2d);
}