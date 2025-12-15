package vehicle.state;

import exceptions.IllegalStateException;
import vehicle.Vehicule;

/**
 * Represents a vehicle that is currently being repaired.
 * Transitions to parked state when repairs are complete.
 */
public class UnderRepairState extends VehiculeState {

    /**
     * @param vehicule the vehicle in this state
     */
    public UnderRepairState(Vehicule vehicule) {
        super(vehicule);
    }

    /**
     * Attempts to transition to under repair state
     * 
     * @throws IllegalStateException because vehicle is already under repair
     */
    @Override
    public void underRepair() throws IllegalStateException {
        throw new IllegalStateException("Vehicule already under reparation");
    }

    /**
     * Transitions the vehicle to in use state
     */
    @Override
    public void in_use() {
        this.vehicule.setState(new InUseState(this.vehicule));
    }

    /**
     * Attempts to mark as stolen
     * 
     * @throws IllegalStateException because vehicles under repair cannot be stolen
     */
    @Override
    public void stolen() throws IllegalStateException {
        throw new IllegalStateException("Vehicule cannot be stolen");
    }

    /**
     * Transitions the vehicle to parked state and resets location count
     */
    @Override
    public void parked() {
        this.vehicule.locationNb = 0;
        this.vehicule.setState(new ParkedState(this.vehicule));
    }
}