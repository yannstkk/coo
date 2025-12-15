package vehicle.state;

import vehicle.Vehicule;
import exceptions.IllegalStateException;

/**
 * Abstract base class for vehicle states using the State pattern.
 * Defines possible state transitions for vehicles in the system.
 */
public abstract class VehiculeState {
    protected Vehicule vehicule;

    /**
     * @param vehicule the vehicle in this state
     */
    public VehiculeState(Vehicule vehicule) {
        this.vehicule = vehicule;
    }

    /**
     * Transitions the vehicle to under repair state
     * 
     * @throws IllegalStateException if the transition is not allowed
     */
    public abstract void underRepair() throws IllegalStateException;

    /**
     * Transitions the vehicle to in use state
     * 
     * @throws IllegalStateException if the transition is not allowed
     */
    public abstract void in_use() throws IllegalStateException;

    /**
     * Transitions the vehicle to stolen state
     * 
     * @throws IllegalStateException if the transition is not allowed
     */
    public abstract void stolen() throws IllegalStateException;

    /**
     * Transitions the vehicle to parked state
     * 
     * @throws IllegalStateException if the transition is not allowed
     */
    public abstract void parked() throws IllegalStateException;
}