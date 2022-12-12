import java.util.List;

public class ChaseClosest extends State {
    // TODO

    private Position firstPosition;
    private double speed;
    private Agent agent;
    private int stepsToStop;

    public ChaseClosest(double x, double y) {
        super();
        setName("Chase Closest");
        firstPosition = new Position(x, y);
        speed = Common.generateRandomInt(1, 2);
        stepsToStop = -1;
    }

    //Method to find the closest order to the current agent
    //It takes orders as a list from Common
    //If there is some order, it returns the closest one, otherwise it returns null.
    private Order findClosest(){

        double temp = 4000.0;
        Order closest = null;
        List<Order> orderToChase = Common.getOrders();
        for(int i = 0; i < orderToChase.size() ; i++){
            if((agent.getPosition().distanceTo(orderToChase.get(i).getPosition().getX(), orderToChase.get(i).getPosition().getX())) < temp){
                closest = orderToChase.get(i);
            }
        }
        if(closest != null) return closest;
        else return null;
    }

    //Chase action that makes the current agent move to the closest order.
    //If there is a closest order,i.e. there is at least one order, it moves on that order's direction.
    //There are also some simple math calculations(Pisagor Theorem) to find number of steps, distance etc.
    //After it finds the updated position after one step, current agent's position is set to the updated position
    private void chaseAction(){
        Order dest = findClosest();
        int differenceX;
        int differenceY;
        int wholePath;
        if(dest.getPosition() != null) {
            int currentX = agent.getPosition().getIntX();
            int currentY = agent.getPosition().getIntY();
            differenceY = Math.abs(dest.getPosition().getIntY() - currentY);
            differenceX = Math.abs(dest.getPosition().getIntX() - currentX);
            wholePath = (int) Math.sqrt(differenceX * differenceX + differenceY * differenceY);
            stepsToStop = (int) (wholePath / speed);
            if (stepsToStop == 0) return;
            Position updatedPosition = new Position(currentX, currentY);
            double speedX = differenceX / stepsToStop;
            double speedY = differenceY / stepsToStop;
            if (currentX < dest.getPosition().getX() && stepsToStop != 0) {
                updatedPosition.setX(currentX + speedX);
            } else if (currentX > dest.getPosition().getX() && stepsToStop != 0) {
                updatedPosition.setX(currentX - speedX);
            }

            if (currentY < dest.getPosition().getY() && stepsToStop != 0) {
                updatedPosition.setY(currentY + speedY);
            } else if (currentY > dest.getPosition().getY() && stepsToStop != 0) {
                updatedPosition.setY(currentY - speedY);
            }
            stepsToStop--;
            agent.position.setX(updatedPosition.getX());
            agent.position.setY(updatedPosition.getY());
        }

    }

    //step method that calls chaseAction()
    @Override
    public void step(Agent agent) {
        this.agent = agent;
        chaseAction();
    }
}