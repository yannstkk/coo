package intervenant;

import intervenant.visitor.Visitor;
import vehicle.Vehicule;

public class Technician implements Visitor {
    public void visit(Vehicule v) throws IllegalStateException {
        v.setRepairIntervalsRemaining(v.getRepairIntervalsRemaining() - 1);
        if (v.getRepairIntervalsRemaining() == 0) {
            v.getVehiculeState().parked();
        }
    }
}