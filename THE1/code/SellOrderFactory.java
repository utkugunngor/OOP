public class SellOrderFactory extends OrderFactory {

    // TODO
    public SellOrderFactory(){

    }

    @Override
    public Order createOrder(Country country) {
        return new SellOrder(country, country.getPosition().getX(),country.getPosition().getY());
    }
}