import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Common {
    private static final String title = "Gold Wars";
    private static final int windowWidth = 1650;
    private static final int windowHeight = 1000;

    private static final GoldPrice goldPrice = new GoldPrice(588, 62);

    private static final Random randomGenerator = new Random(1234);
    private static final int upperLineY = 100;

    private static final Country china;
    private static final Country usa;
    private static final Country turkey;
    private static final Country israel;
    private static final Country russia;
    private static final Agent mss;
    private static final Agent cia;
    private static final Agent mit;
    private static final Agent mossad;
    private static final Agent svr;

    private static List<Order> orders = new ArrayList<>();
    private static List<Country> countries = new ArrayList<>();
    private static List<Agent> basicAgents = new ArrayList<>();



    static  {
        // TODO: Here, you can initialize the fields you have declared
        //I added countries and agents to the related lists I defined above to use them in stepAllEntities() method
        usa = new Country("Usa",50, 10000, 150, 800);
        countries.add(usa);
        israel = new Country("Israel",50, 10000, 450, 800);
        countries.add(israel);
        turkey = new Country("Turkey",50, 10000, 750, 800);
        countries.add(turkey);
        russia = new Country("Russia", 50, 10000 ,1050, 800);
        countries.add(russia);
        china = new Country("China",50, 10000, 1350, 800);
        countries.add(china);
        mss = new Expert(new Master(new Novice(new BasicAgent("MSS", 150, 600))));
        basicAgents.add(mss);
        cia = new Expert(new Master(new Novice(new BasicAgent("CIA", 450, 600))));
        basicAgents.add(cia);
        mit = new Expert(new Master(new Novice(new BasicAgent("MIT", 750, 600))));
        basicAgents.add(mit);
        mossad = new Expert(new Master(new Novice(new BasicAgent("MOSSAD", 1050, 600))));
        basicAgents.add(mossad);
        svr = new Expert(new Master(new Novice(new BasicAgent("SVR", 1350, 600))));
        basicAgents.add(svr);
    }

    // getters
    public static String getTitle() { return title; }
    public static int getWindowWidth() { return windowWidth; }
    public static int getWindowHeight() { return windowHeight; }

    // getter
    public static Country getChina(){ return china; }
    public static Country getUsa(){ return usa; }
    public static Country getIsrael(){ return israel; }
    public static Country getTurkey(){ return turkey; }
    public static Country getRussia(){ return russia; }
    public static Agent getMss() {return mss;}
    public static Agent getCia() {return cia;}
    public static Agent getMit() {return mit;}
    public static Agent getMossad() {return mossad;}
    public static Agent getSvr() {return svr;}
    public static GoldPrice getGoldPrice() { return goldPrice; }
    public static List<Order> getOrders() {
        return orders;
    }
    public static List<Agent> getBasicAgents(){return basicAgents;}

    // getters
    public static Random getRandomGenerator() { return randomGenerator; }
    public static int getUpperLineY() { return upperLineY; }

    //Method that checks if an order hit the upper line
    //It checks it with the method hasReached() from Order
    //ıf it hits the upper line, I update corresponding entities' money or gold according to the order type.
    //Finally, I remove the order.
    private static void removeOrderHitLine() {
        List<Order> deleteOrders = new ArrayList<>();
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).hasReached()) {
                deleteOrders.add(orders.get(i));
                if(orders.get(i).getOrderType() == 0){
                    orders.get(i).getCountry().buyGold(orders.get(i).getAmount());
                }
                else {
                    orders.get(i).getCountry().sellGold(orders.get(i).getAmount());
                }
            }
        }
        if (deleteOrders.size() != 0) {
            for (int i = 0; i < deleteOrders.size(); i++) {
                orders.remove(deleteOrders.get(i));
            }
        }
    }

    //Method to handle stealing operation
    //I used a nested loop that checks if any order is in the area of any agent
    //If it is and it is not from the agent's country, i.e mit and Turkey, I add the order to the list.
    //Then I update the agent with the stolen money amount
    //According to order type, I update countries that loses and gains money or gold accordingly
    //After I change corresponding entities, I remove the order.
    private static void steal(){
        List<Order> ordersToStole = new ArrayList<>();

        for(int i = 0; i < orders.size() ; i++){
            Country countryOfOrder = orders.get(i).getCountry();
            for(int j = 0; j < basicAgents.size() ; j++) {
                Position currentAgent = basicAgents.get(j).getPosition();
                Country countryOfAgent = basicAgents.get(j).getCountry();
                if ((orders.get(i).getPosition().getX() <= currentAgent.getX() + 60) && (orders.get(i).getPosition().getX() >= currentAgent.getX())
                        && (orders.get(i).getPosition().getY() >= currentAgent.getY()) && (orders.get(i).getPosition().getY() <= currentAgent.getY() + 60)
                        && (countryOfAgent != countryOfOrder)) {
                    ordersToStole.add(orders.get(i));

                    basicAgents.get(j).addStolen((int) (orders.get(i).getAmount() * Common.getGoldPrice().getCurrentPrice()));

                    if (orders.get(i).getOrderType() == 0) {
                        countryOfAgent.gainCash(orders.get(i).getAmount());
                        countryOfOrder.loseCash(orders.get(i).getAmount());
                    } else {
                        countryOfAgent.gainGold(orders.get(i).getAmount());
                        countryOfOrder.loseGold(orders.get(i).getAmount());
                    }
                }
            }
        }
        if (ordersToStole.size() != 0) {
            for (int i = 0; i < ordersToStole.size(); i++) {
                orders.remove(ordersToStole.get(i));
            }
        }
    }

    public static void stepAllEntities() {
        if (randomGenerator.nextInt(200) == 0){ goldPrice.step();}
        // TODO

        for(Country country: countries){
            country.step();
        }
        for(Agent agent: basicAgents){
            agent.step();
        }
        for(Order order: orders){
            order.step();
        }
        steal();
        removeOrderHitLine();


    }
    public static int generateRandomInt(int min, int max) {
        Random rand = new Random();

        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
}