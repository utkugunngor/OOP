public class BuyOrderFactory extends OrderFactory {

    // TODO
    public BuyOrderFactory(){

    }

    @Override
    public Order createOrder(Country country) {
        return new BuyOrder(country, country.getPosition().getX(),country.getPosition().getY());
    }
}