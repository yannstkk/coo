package control;

import java.util.Set;

import control.strategy.Slot;
import exceptions.CannotParkException;
import vehicle.Vehicule;
import vehicle.state.InUseState;
import vehicle.state.ParkedState;

public class User {
    private double balance;
    private Vehicule rentedVehicule;

    public User(String firstName, String lastName, double balance) {
        this.balance = balance;
        this.rentedVehicule = null;
    }

    public double getBalance() {
        return balance;
    }

    public String rent(Station station) throws CannotParkException {
        return rent(station, null);
    }

    // CHANGEMENT: Accepte maintenant Set<Integer> au lieu de Set<Vehicule>
    public String rent(Station station, Set<Integer> alreadyUsedVehicleIds) throws CannotParkException {
        if (rentedVehicule != null || station.isEmpty()) {
            return null;
        }
        
        // Trouver un vélo disponible
        Vehicule vehicule = null;
        
        // Parcourir TOUS les vélos de la station
        for (Slot slot : station.getSlotList()) {
            if (slot.getIsOccupied() && slot.getActualVehicule().getVehiculeState() instanceof ParkedState) {
                Vehicule candidate = slot.getActualVehicule();
                
                // Vérifier si ce vélo n'a PAS déjà été utilisé ce cycle (par son ID)
                if (alreadyUsedVehicleIds != null && alreadyUsedVehicleIds.contains(candidate.getId())) {
                    // Ce vélo a déjà été utilisé ce cycle, passer au suivant
                    continue;
                }
                
                // Vélo disponible trouvé
                vehicule = candidate;
                break;
            }
        }
        
        if (vehicule == null) {
            return null;
        }
        
        double price = vehicule.getPrice();
        
        if (this.balance >= price) {
            this.balance -= price;
            // CORRECTION CRITIQUE : Louer le vélo SPÉCIFIQUE qu'on a trouvé
            String result = station.rentSpecificVehicule(vehicule);
            this.rentedVehicule = vehicule;
            return result;
        } else {
            return null;
        }
    }

    public String park(Station s) throws CannotParkException {
        if (rentedVehicule != null && !s.isFull()) {
            String result = s.parkVehiculeWithMessage(rentedVehicule);
            if (result != null) {
                this.rentedVehicule = null;
            }
            return result;
        }
        return null;
    }

    public Vehicule getRentedVehicule() {
        return rentedVehicule;
    }
}