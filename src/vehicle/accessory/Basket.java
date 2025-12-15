package vehicle.accessory;

import vehicle.Vehicule;

/**
 * Decorator that adds a basket accessory to a vehicle.
 * Increases the rental price by 5 units.
 */
public class Basket extends VehiculeDecorator {

    /**
     * Creates a basket decorator for the given vehicle
     * 
     * @param vehicule the vehicle to decorate
     */
    public Basket(Vehicule vehicule) {
        super(vehicule);
    }

    /**
     * Gets the price with basket cost added
     * 
     * @return the price with basket added
     */
    public double getPrice() {
        return vehicule.getPrice() + 5;
    }

}