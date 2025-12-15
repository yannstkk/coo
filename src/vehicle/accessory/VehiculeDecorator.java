package vehicle.accessory;

import vehicle.Vehicule;

/**
 * Abstract decorator for adding accessories to vehicles.
 * Extends vehicle functionality by wrapping and modifying base vehicle
 * properties.
 */
public abstract class VehiculeDecorator extends Vehicule {
    public Vehicule vehicule;

    /**
     * Creates a decorator for the given vehicle
     * 
     * @param vehicule the vehicle to decorate
     */
    public VehiculeDecorator(Vehicule vehicule) {
        super(vehicule.getPrice());
        this.vehicule = vehicule;
    }

    /**
     * Gets the price with decoration applied
     * 
     * @return the price with decoration
     */
    public abstract double getPrice();
}