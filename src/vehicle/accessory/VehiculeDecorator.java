package vehicle.accessory;

import exceptions.IllegalStateException;
import intervenant.visitor.Visitor;
import vehicle.Vehicule;
import vehicle.state.VehiculeState;

/**
 * Abstract decorator for adding accessories to vehicles.
 * Wraps a vehicle and delegates all operations to it while allowing
 * price modifications through decoration.
 * 
 * IMPORTANT: This decorator does NOT create a new vehicle ID.
 * It delegates all core vehicle operations to the wrapped vehicle.
 */
public abstract class VehiculeDecorator extends Vehicule {
    protected Vehicule vehicule;

    /**
     * Creates a decorator for the given vehicle.
     * Note: This does NOT increment the vehicle counter.
     * 
     * @param vehicule the vehicle to decorate
     */
    public VehiculeDecorator(Vehicule vehicule) {
        super(0); // Dummy call to satisfy compiler
        this.vehicule = vehicule;
        // Decrement counter to compensate for the super() call
        count--;
    }

    /**
     * Gets the price with decoration applied.
     * Must be implemented by concrete decorators.
     * 
     * @return the decorated price
     */
    @Override
    public abstract double getPrice();

    // ===== Delegation methods to preserve vehicle identity =====

    /**
     * Gets the ID of the wrapped vehicle (not a new ID)
     * 
     * @return the original vehicle's ID
     */
    @Override
    public int getId() {
        return vehicule.getId();
    }

    /**
     * Gets the current state of the wrapped vehicle
     * 
     * @return the vehicle state
     */
    @Override
    public VehiculeState getVehiculeState() {
        return vehicule.getVehiculeState();
    }

    /**
     * Sets the state of the wrapped vehicle
     * 
     * @param vehiculeState the new state
     */
    @Override
    public void setState(VehiculeState vehiculeState) {
        vehicule.setState(vehiculeState);
    }

    /**
     * Gets the number of times the wrapped vehicle has been rented
     * 
     * @return the location count
     */
    @Override
    public int getLocationNb() {
        return vehicule.locationNb;
    }

    /**
     * Increments the location counter of the wrapped vehicle
     */
    @Override
    public void incrementLocationNb() {
        vehicule.incrementLocationNb();
    }

    /**
     * Gets the remaining repair intervals of the wrapped vehicle
     * 
     * @return the number of intervals remaining
     */
    @Override
    public int getRepairIntervalsRemaining() {
        return vehicule.getRepairIntervalsRemaining();
    }

    /**
     * Sets the remaining repair intervals of the wrapped vehicle
     * 
     * @param remaining the number of intervals to set
     */
    @Override
    public void setRepairIntervalsRemaining(int remaining) {
        vehicule.setRepairIntervalsRemaining(remaining);
    }

    /**
     * Accepts a visitor for the wrapped vehicle
     * 
     * @param v the visitor
     * @throws IllegalStateException if an illegal state occurs during visit
     */
    @Override
    public void accept(Visitor v) throws IllegalStateException {
        vehicule.accept(v);
    }
}