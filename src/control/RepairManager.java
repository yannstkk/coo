package control;

import java.util.ArrayList;
import java.util.List;

import control.strategy.Slot;
import intervenant.Technician;
import vehicle.Vehicule;
import vehicle.state.ParkedState;
import vehicle.state.UnderRepairState;

/**
 * Responsabilité : Gérer tout le cycle de réparation des véhicules
 * (détection d'usure, mise en réparation, intervention technicien)
 */
public class RepairManager {
    
    private final Technician technician = new Technician();
    private final Colors colors = new Colors();

    public List<String> processRepairs(List<Station> stations) {
        List<String> repairMessages = new ArrayList<>();
        
        for (Station st : stations) {
            for (Slot slot : st.getSlotList()) {
                if (slot.getIsOccupied()) {
                    Vehicule v = slot.getActualVehicule();
                    
                    if (shouldBeRepaired(v)) {
                        repairMessages.add(colors.getOrange() + "Vélo #" + v.getId() + 
                            " (Station " + st.getId() + ") nécessite une réparation" + colors.getReset());
                        v.setState(new UnderRepairState(v));
                        v.setRepairIntervalsRemaining(2);
                    } 
                    else if (v.getVehiculeState() instanceof UnderRepairState) {
                        v.accept(technician);
                        
                        if (v.getRepairIntervalsRemaining() == 0) {
                            repairMessages.add(colors.getGreen() + "Vélo #" + v.getId() + 
                                " (Station " + st.getId() + ") réparé avec succès" + colors.getReset());
                        }
                    }
                }
            }
        }
        
        return repairMessages;
    }
    
    private boolean shouldBeRepaired(Vehicule v) {
        return v.getLocationNb() >= 6 && v.getVehiculeState() instanceof ParkedState;
    }
}