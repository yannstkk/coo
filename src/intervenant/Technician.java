package intervenant;

import intervenant.visitor.Visitor;
import vehicle.Vehicule;

public class Technician implements Visitor {
    public void visit(Vehicule v) throws IllegalStateException {
        v.repairIntervalsRemaining--;
        if (v.getRepairIntervalsRemaining() == 0) {
            v.getVehiculeState().parked();
        }
    }
}