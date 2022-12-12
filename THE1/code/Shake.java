import java.awt.*;

public class Shake extends State {
    // TODO

    private Position firstPosition;

    public Shake(double x, double y) {
        super();
        setName("Shake");
    }

    @Override
    public void step(Agent agent) {
        shakeAction(agent);
    }

    private boolean checkIn(Position agentPos, Position updated){
        if((updated.getX() < agentPos.getX() + 5) && (updated.getX() > agentPos.getX() - 5) &&
                (updated.getY() < agentPos.getY() + 5) && (updated.getY() > agentPos.getY() - 5)){
            return true;
        }
        return false;
    }
    private void shakeAction(Agent agent){


        Position updatedPosition;
        int direction = Common.getRandomGenerator().nextInt(4); //4 random options: up right, up left, down right, down left
        while(true) {
            if (direction == 0)
                updatedPosition = new Position(firstPosition.getX() + 3, firstPosition.getY() + 3);
            else if (direction == 1)
                updatedPosition = new Position(firstPosition.getX() + 3, firstPosition.getY() - 3);
            else if (direction == 2)
                updatedPosition = new Position(firstPosition.getX() - 3, firstPosition.getY() + 3);
            else updatedPosition = new Position(firstPosition.getX() - 3, firstPosition.getY() - 3);

            if(checkIn(firstPosition, updatedPosition)) break;
        }

        agent.position.setX(updatedPosition.getX());
        agent.position.setY(updatedPosition.getY());
    }

    public Position getFirstPosition() {
        return firstPosition;
    }

    public void setFirstPosition(Position firstPosition) {
        this.firstPosition = firstPosition;
    }
}