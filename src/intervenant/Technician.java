package intervenant;

import intervenant.visitor.Visitor;
import vehicle.Vehicule;

public class Technician implements Visitor {
    
    @Override
    public void visit(Vehicule v) {
        int remaining = v.getRepairIntervalsRemaining();
        
        if (remaining > 0) {
            v.setRepairIntervalsRemaining(remaining - 1);
        }
        
        if (v.getRepairIntervalsRemaining() == 0) {
            v.getVehiculeState().parked();
        }
    }
}