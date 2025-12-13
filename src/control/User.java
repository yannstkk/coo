package control;

import java.util.Set;

import control.strategy.Slot;
import exceptions.CannotParkException;
import vehicle.Vehicule;
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

    public String rent(Station station, Set<Integer> alreadyUsedVehicleIds) throws CannotParkException {
        if (rentedVehicule != null || station.isEmpty()) {
            return null;
        }
        
        Vehicule vehicule = null;
        
        for (Slot slot : station.getSlotList()) {
            if (slot.getIsOccupied() && slot.getActualVehicule().getVehiculeState() instanceof ParkedState) {
                Vehicule candidate = slot.getActualVehicule();
                
                if (alreadyUsedVehicleIds != null && alreadyUsedVehicleIds.contains(candidate.getId())) {
                    continue;
                }
                
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