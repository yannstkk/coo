package vehicle.accessory;

import vehicle.Vehicule;

/**
 * Decorator that adds a luggage rack accessory to a vehicle.
 * Increases the rental price by 10 units.
 */
public class LuggageRack extends VehiculeDecorator {
    /**
     * Creates a luggage rack decorator for the given vehicle
     * 
     * @param vehicule the vehicle to decorate
     */
    public LuggageRack(Vehicule vehicule) {
        super(vehicule);
    }

    /**
     * Gets the price with luggage rack cost added
     * 
     * @return the price with luggage rack added
     */
    public double getPrice() {
        return vehicule.getPrice() + 10;
    }
}