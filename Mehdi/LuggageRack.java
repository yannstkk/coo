package Mehdi;

public class LuggageRack extends VehiculeDecorator {
    public LuggageRack(Vehicule vehicule) {
        super(vehicule);
    }

    public double getPrice() {
        return vehicule.getPrice() + 10;
    }
}

/*
 * We suppose that the price of the accessory Luggage Rack is equal to 10 Euros.
 */