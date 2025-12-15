package intervenant;

import intervenant.visitor.Visitor;
import exceptions.IllegalStateException;
import vehicle.Vehicule;

/**
 * Represents a technician who repairs vehicles.
 * Decrements repair intervals and transitions vehicles to parked state when
 * complete.
 */
public class Technician implements Visitor {

    /**
     * Visits a vehicle to perform repair work, decrements repair intervals and
     * transitions to parked state when complete
     * 
     * @param v the vehicle to visit
     * @throws IllegalStateException if an illegal state occurs during visit
     */
    @Override
    public void visit(Vehicule v) throws IllegalStateException {
        int remaining = v.getRepairIntervalsRemaining();

        if (remaining > 0) {
            v.setRepairIntervalsRemaining(remaining - 1);
        }

        if (v.getRepairIntervalsRemaining() == 0) {
            v.getVehiculeState().parked();
        }
    }
}