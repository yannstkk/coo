package vehicle.state;

import vehicle.Vehicule;
import exceptions.IllegalStateException;

/**
 * Represents a vehicle that has been stolen.
 * This is a terminal state - no transitions to other states are allowed.
 */
public class StolenState extends VehiculeState {

    /**
     * @param vehicule the vehicle in this state
     */
    public StolenState(Vehicule vehicule) {
        super(vehicule);
    }

    /**
     * Attempts to transition to under repair state
     * 
     * @throws IllegalStateException because stolen vehicles cannot be repaired
     */
    @Override
    public void underRepair() throws IllegalStateException {
        throw new IllegalStateException("Vehicule stolen");
    }

    /**
     * Attempts to transition to in use state
     * 
     * @throws IllegalStateException because stolen vehicles cannot be used
     */
    @Override
    public void in_use() throws IllegalStateException {
        throw new IllegalStateException("Vehicule stolen");
    }

    /**
     * Attempts to mark as stolen
     * 
     * @throws IllegalStateException because vehicle is already stolen
     */
    @Override
    public void stolen() throws IllegalStateException {
        throw new IllegalStateException("Vehicule already marked as stolen");
    }

    /**
     * Attempts to transition to parked state
     * 
     * @throws IllegalStateException because stolen vehicles cannot be parked
     */
    @Override
    public void parked() throws IllegalStateException {
        throw new IllegalStateException("Vehicule stolen");
    }
}