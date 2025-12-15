package vehicle.state;

import exceptions.IllegalStateException;
import vehicle.Vehicule;

/**
 * Represents a vehicle that is currently being used by a customer.
 * Transitions to parked or under repair state when returned.
 */
public class InUseState extends VehiculeState {

    /**
     * @param vehicule the vehicle in this state
     */
    public InUseState(Vehicule vehicule) {
        super(vehicule);
    }

    /**
     * Attempts to transition to under repair state
     * 
     * @throws IllegalStateException because vehicle cannot be repaired while in use
     */
    @Override
    public void underRepair() throws IllegalStateException {
        throw new IllegalStateException("Vehicule cannot be repaired while in use");
    }

    /**
     * Attempts to transition to in use state
     * 
     * @throws IllegalStateException because vehicle is already in use
     */
    @Override
    public void in_use() throws IllegalStateException {
        throw new IllegalStateException("Vehicule already in use");
    }

    /**
     * Attempts to mark as stolen
     * 
     * @throws IllegalStateException because vehicle cannot be stolen while in use
     */
    @Override
    public void stolen() throws IllegalStateException {
        throw new IllegalStateException("Vehicule cannot be stolen while in use");
    }

    /**
     * Transitions the vehicle to parked or under repair state based on usage count
     */
    @Override
    public void parked() {
        if (this.vehicule.getLocationNb() == 5) {
            this.vehicule.setRepairIntervalsRemaining(2);
            this.vehicule.setState(new UnderRepairState(vehicule));
        } else {
            this.vehicule.setState(new ParkedState(vehicule));
        }
    }

}