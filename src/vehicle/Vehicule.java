package vehicle;

import intervenant.Technician;
import intervenant.visitor.Visitor;
import vehicle.state.ParkedState;
import vehicle.state.VehiculeState;
import exceptions.IllegalStateException;

/**
 * Abstract base class for all vehicles in the bike-sharing system.
 * Manages vehicle state, rental tracking, repairs, and pricing using State and
 * Visitor patterns.
 */
public abstract class Vehicule {
    protected static int count = 1;
    protected int id;
    public int locationNb;
    protected VehiculeState vehiculeState;
    protected double price;
    public int repairIntervalsRemaining = 0;
    protected Technician technicien;

    /**
     * @param price the base rental price
     */
    public Vehicule(double price) {
        this.id = count;
        count++;
        this.vehiculeState = new ParkedState(this);
        this.price = price;
        this.locationNb = 0;
    }

    /**
     * Gets the rental price of the vehicle
     * 
     * @return the price
     */
    public abstract double getPrice();

    /**
     * Sets the vehicle state
     * 
     * @param vehiculeState the new state
     */
    public void setState(VehiculeState vehiculeState) {
        this.vehiculeState = vehiculeState;
    }

    /**
     * Increments the number of times the vehicle has been rented
     */
    public void incrementLocationNb() {
        this.locationNb++;
    }

    /**
     * Gets the current vehicle state
     * 
     * @return the vehicle state
     */
    public VehiculeState getVehiculeState() {
        return vehiculeState;
    }

    /**
     * Gets the number of times the vehicle has been rented
     * 
     * @return the location count
     */
    public int getLocationNb() {
        return locationNb;
    }

    /**
     * Gets the vehicle ID
     * 
     * @return the ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the remaining repair intervals
     * 
     * @return the number of intervals remaining
     */
    public int getRepairIntervalsRemaining() {
        return repairIntervalsRemaining;
    }

    /**
     * Sets the remaining repair intervals
     * 
     * @param remaining the number of intervals to set
     */
    public void setRepairIntervalsRemaining(int remaining) {
        this.repairIntervalsRemaining = remaining;
    }

    /**
     * Accepts a visitor for the visitor pattern
     * 
     * @param v the visitor
     * @throws IllegalStateException if an illegal state occurs during visit
     */
    public void accept(Visitor v) throws IllegalStateException {
        v.visit(this);
    }

    /**
     * Resets the vehicle counter to 1
     */
    public static void reset() {
        count = 1;
    }
}