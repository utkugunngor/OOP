public abstract class OrderFactory {
    // TODO

    public OrderFactory(){
    }

    //abstract create method for BuyOrderFactory and SellOrderFactory
    public abstract Order createOrder(Country country);
}