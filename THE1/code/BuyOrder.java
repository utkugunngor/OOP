import java.awt.*;

public class BuyOrder extends Order {

    // TODO

    private Graphics2D g2d;
    private int speed;
    private Position destination;
    private int stepsToStop;
    private int stepSize;

    //constructor
    public BuyOrder(Country country, double x, double y) {
        super(x, y);
        setColor(Color.green);
        setCountry(country);
        setOrderType(0);
        speed = Common.generateRandomInt(1, 5);  //generate random speed
        stepsToStop = -1;
        destination = getDestination();  // generate random destination
        stepSize = 0;
    }

    @Override
    public void draw(Graphics2D g2d) {
        this.g2d = g2d;
        drawOrder();
    }

    //draw the order as a circle, with amount and initials of country
    //I tried to write this in the Order.java but somehow it did not work.
    private void drawOrder() {
        try {

            int radius = 15;
            int currentX = getPosition().getIntX();
            int currentY = getPosition().getIntY();
            g2d.setColor(Color.GREEN);
            g2d.fillOval(currentX, currentY, radius, radius);
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("default", Font.BOLD, 11));
            int textX = currentX + 4;
            int textY = currentY + 12;
            g2d.drawString(new StringBuilder().append(getAmount()).toString(), textX, textY);
            int orTextX = currentX + 3;
            int orTextY = currentY - 6;
            g2d.setColor(getColor());
            g2d.drawString(getInitialsOfCountry(), orTextX, orTextY);

        } catch (Exception e) {

        }
    }

    //according to the speed and destination, order moves through that path
    //and takes the value of updated position that is calculated using some simple math computations to find distance, step number, etc.
    private void orderAction() {

            int differenceX;
            int differenceY;
            int wholePath;
            int currentX = getPosition().getIntX();
            int currentY = getPosition().getIntY();
            differenceY = Math.abs(destination.getIntY() - currentY);
            differenceX = Math.abs(destination.getIntX() - currentX);
            wholePath = (int) Math.sqrt(differenceX * differenceX + differenceY * differenceY);
            stepsToStop = wholePath / speed;
            Position updatedPosition = new Position(currentX, currentY);
            double speedX = differenceX / stepsToStop;
            double speedY = differenceY / stepsToStop;
            if (currentX < destination.getX() && stepsToStop != 0) {
                updatedPosition.setX(currentX + speedX);
            } else if (currentX > destination.getX() && stepsToStop != 0) {
                updatedPosition.setX(currentX - speedX);
            }

            if (currentY < destination.getY() && stepsToStop != 0) {
                updatedPosition.setY(currentY + speedY);
            } else if (currentY > destination.getY() && stepsToStop != 0) {
                updatedPosition.setY(currentY - speedY);
            }

            stepsToStop--;
            getPosition().setX(updatedPosition.getX());
            getPosition().setY(updatedPosition.getY());
    }

    @Override
    public void step() {
        try {
            drawOrder();
            orderAction();
        }
        catch (Exception e){

        }
    }

}