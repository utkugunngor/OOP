import java.awt.*;

public class GotoXY extends State {

    // TODO

    private Position destination;
    private double speed;
    private Agent agent;
    private int stepsToStop;

    //constructor
    public GotoXY(double x, double y) {
        super();
        setName("GotoXY");
        destination = generateRandomPosition();
        speed = Common.generateRandomInt(1, 2);
        stepsToStop = -1;
    }

    //a random speed and direction are generated in the constrcutor
    //after that, I found direct path between 2 points by using Pisagor theorem and obtained total number of steps
    //by using that, I found the speed for x and y coordinates seperately
    //according to the current location, I found out updated position of the badge after one step and assign it to agent's position
    private void gotoAction(){

        int differenceX;
        int differenceY;
        int wholePath;
        int currentX = agent.getPosition().getIntX();
        int currentY = agent.getPosition().getIntY();
        differenceY = Math.abs(destination.getIntY() - currentY);
        differenceX = Math.abs(destination.getIntX() - currentX);
        wholePath = (int) Math.sqrt(differenceX*differenceX+differenceY*differenceY);
        stepsToStop = (int) (wholePath/speed);
        if(stepsToStop == 0) return;
        Position updatedPosition = new Position(currentX, currentY);
        double speedX = differenceX/stepsToStop;
        double speedY = differenceY/stepsToStop;
        if (currentX < destination.getX() && stepsToStop != 0) {
            updatedPosition.setX(currentX + speedX);
        }
        else if (currentX > destination.getX() && stepsToStop != 0){
            updatedPosition.setX(currentX - speedX);
        }

        if (currentY < destination.getY() && stepsToStop != 0) {
            updatedPosition.setY(currentY + speedY);
        }
        else if (currentY > destination.getY() && stepsToStop != 0){
            updatedPosition.setY(currentY - speedY);
        }

        stepsToStop--;
        agent.position.setX(updatedPosition.getX());
        agent.position.setY(updatedPosition.getY());
    }

    //random position generator
    private Position generateRandomPosition() {
        int newX = Common.generateRandomInt(0, Common.getWindowWidth() - 50);
        int newY = Common.generateRandomInt(Common.getUpperLineY(), 800);
        return new Position(newX,newY);
    }

    @Override
    public void step(Agent agent) {
        this.agent = agent;
        gotoAction();
    }

}