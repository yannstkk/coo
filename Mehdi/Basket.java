package Mehdi;

public class Basket extends VehiculeDecorator {

    public Basket(Vehicule vehicule) {
        super(vehicule);
    }

    public double getPrice() {
        return vehicule.getPrice() + 5;
    }

}

/*
 * We suppose that the price of the accessory Luggage Rack is equal to 10 Euros.
 */
