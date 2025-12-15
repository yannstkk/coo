package vehicle.state;

import exceptions.IllegalStateException;
import vehicle.Vehicule;

/**
 * Represents a vehicle that is parked at a station and available for rent.
 * Can transition to in use, under repair, or stolen states.
 */
public class ParkedState extends VehiculeState {

    /**
     * @param vehicule the vehicle in this state
     */
    public ParkedState(Vehicule vehicule) {
        super(vehicule);
    }

    /**
     * Transitions the vehicle to under repair state
     * 
     * @throws IllegalStateException if state transition fails
     */
    public void underRepair() throws IllegalStateException {
        this.vehicule.setState(new UnderRepairState(vehicule));
    }

    /**
     * Transitions the vehicle to in use state and increments location count
     */
    public void in_use() {
        this.vehicule.incrementLocationNb();
        this.vehicule.setState(new InUseState(this.vehicule));
    }

    /**
     * Transitions the vehicle to stolen state
     */
    public void stolen() {
        this.vehicule.setState(new StolenState(this.vehicule));
    }

    /**
     * Keeps the vehicle in parked state (no-op)
     */
    public void parked() {
    }
}