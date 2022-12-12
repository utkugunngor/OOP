import java.awt.*;

public abstract class Order extends Entity {

    // TODO
    private Color color;
    private Country country;
    private int orderType; //0 for BuyOrder, 1 for SellOrder
    private Graphics2D g2d;
    private int amount;
    private boolean hasReached;

    //constructor
    public Order(double x, double y) {

        super(x, y);

        color = null;
        country = null;
        orderType = -1;
        amount = Common.generateRandomInt(1, 5);
        hasReached = false;

    }

    @Override
    public abstract void draw(Graphics2D g2d);

    //get initials for orders
    protected String getInitialsOfCountry() {
        if(this.country.getCountryName().equals("Usa")) {
            return "US";
        }
        else if(this.country.getCountryName().equals("Israel")) {
            return "IL";
        }
        else if(this.country.getCountryName().equals("Turkey")) {
            return "TR";
        }
        else if(this.country.getCountryName().equals("Russia")) {
            return "RU";
        }
        else {
            return "CN";
        }
    }

    //generate a random destination in the area limited by window sizes
    protected Position getDestination(){
        int newX = Common.generateRandomInt(100, Common.getWindowWidth()-100);
        int newY = 0;
        return new Position(newX,newY);
    }

    //checks if the order hits the upperline
    public boolean hasReached(){
        if(getPosition().getIntY() <= Common.getUpperLineY()) return true;
        else return false;
    }

    //getters and setters
    public Country getCountry() {
        return country;
    }
    public void setCountry(Country country) {
        this.country = country;
    }
    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    public int getAmount(){
        return amount;
    }
    public int getOrderType() {
        return orderType;
    }
    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }
    public Graphics2D getG2d(){
        return g2d;
    }
    public void setG2d(Graphics2D g2d){
        this.g2d = g2d;
    }

}