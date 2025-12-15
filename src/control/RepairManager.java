package control;

import java.util.ArrayList;
import java.util.List;

import control.strategy.Slot;
import exceptions.IllegalStateException;
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

    /**
     * Processes repairs for all vehicles at all stations, detecting wear and
     * managing repair cycles
     * 
     * @param stations the list of stations to process
     * @return list of repair messages
     * @throws IllegalStateException if an illegal state occurs during repair
     */
    public List<String> processRepairs(List<Station> stations) throws IllegalStateException {
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
                    } else if (v.getVehiculeState() instanceof UnderRepairState) {
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

    /**
     * Checks if a vehicle should be repaired based on usage count and state
     * 
     * @param v the vehicle to check
     * @return true if the vehicle should be repaired
     */
    private boolean shouldBeRepaired(Vehicule v) {
        return v.getLocationNb() >= 6 && v.getVehiculeState() instanceof ParkedState;
    }
}