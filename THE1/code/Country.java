import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Country extends Entity {

    // TODO

    private String countryName;
    private int goldAmount;
    private int cashAmount;
    private int worth;
    private Font font = new Font("Arial", Font.BOLD, 13);
    private Graphics2D g2d;
    private int stepSize;

    private BufferedImage image = null;


    //Constructor
    //Necessary attributes are set.
    //I get the image in the constructor not to read it every single time.
    public Country(String countryName, int goldAmount, int cashAmount, double x, double y) {
        super(x, y);
        this.countryName = countryName;
        this.goldAmount = goldAmount;
        this.cashAmount = cashAmount;
        this.worth = (int)(cashAmount + goldAmount*Common.getGoldPrice().getCurrentPrice());
        stepSize = 0;
        try {
            image = ImageIO.read(getClass().getResource("/images/" + countryName + ".jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //getters
    public String getCountryName() {return countryName;}
    public int getGoldAmount() {return goldAmount;}
    public int getCashAmount() {return cashAmount;}
    public int getWorth() {return (int) (cashAmount + goldAmount*Common.getGoldPrice().getCurrentPrice());}



    //draw the flag, after that call the other functions that are explained below
    @Override
    public void draw(Graphics2D g2d) {
        this.g2d = g2d;
        g2d.drawImage(image, getPosition().getIntX(), getPosition().getIntY(), 90, 60, null);
        drawText();
        createOrders();

    }

    //draw country information like name, cash, gold and worth
    private void drawText(){
        g2d.setFont(font);
        g2d.setColor(Color.BLACK);
        g2d.drawString(String.format("%s", getCountryName()),this.getPosition().getIntX(), this.getPosition().getIntY()+80 );
        g2d.setColor(Color.YELLOW);
        g2d.drawString(String.format("%d gold", getGoldAmount()),this.getPosition().getIntX(), this.getPosition().getIntY()+100 );
        g2d.setColor(Color.GREEN);
        g2d.drawString(String.format("%d cash", getCashAmount()),this.getPosition().getIntX(), this.getPosition().getIntY()+120 );
        g2d.setColor(Color.BLUE);
        g2d.drawString(String.format("Worth: %d", getWorth()),this.getPosition().getIntX(), this.getPosition().getIntY()+140 );
    }

    //call drawText() for attributes that change continuously
    @Override
    public void step() {
        drawText();
        createOrders();
        stepSize++;
    }

    //generate a random order type and according to that type, create an order from BuyOrderFactory or SellOrderFactory
    public Order createOrder(){
        int orderType = Common.generateRandomInt(1, 10);
        orderType %= 2;
        OrderFactory orderFactory = null;

        if(orderType == 0){
            orderFactory = new BuyOrderFactory();
        }
        else{
            orderFactory = new SellOrderFactory();
        }
        return orderFactory.createOrder(this);
    }

    //I put a bound to arrange how often an order will be created, I also arranged its position and add to the orders array list
    private void createOrders() {
        if (Common.getRandomGenerator().nextInt(200) == 0) {
            Order order = createOrder();
            order.position = new Position(this.getPosition().getX() + 45, this.getPosition().getY() - 10);
            Common.getOrders().add(order);
        }

    }

    //methods for increasing or decreasing gold amount or cash amount for hitting the upper line or order stealing
    public void gainCash(int goldAmount){
        this.cashAmount += goldAmount*Common.getGoldPrice().getCurrentPrice();
    }
    public void loseCash(int goldAmount){
        this.cashAmount -= goldAmount*Common.getGoldPrice().getCurrentPrice();
    }
    public void gainGold(int goldAmount){
        this.goldAmount += goldAmount;
    }
    public void loseGold(int goldAmount){
        this.goldAmount -= goldAmount;
    }
    public void buyGold(int goldAmount){
        loseCash(goldAmount);
        gainGold(goldAmount);
    }
    public void sellGold(int goldAmount){
        gainCash(goldAmount);
        loseGold(goldAmount);
    }
}