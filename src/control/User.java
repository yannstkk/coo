package control;

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
        if (rentedVehicule != null || station.isEmpty()) {
            return null;
        }
        
        // Trouver un vélo disponible
        Vehicule vehicule = null;
        for (Slot slot : station.getSlotList()) {
            if (slot.getIsOccupied() && slot.getActualVehicule().getVehiculeState() instanceof ParkedState) {
                vehicule = slot.getActualVehicule();
                break;
            }
        }
        
        if (vehicule == null) {
            return null;
        }
        
        double price = vehicule.getPrice();
        
        if (this.balance >= price) {
            this.balance -= price;
            String result = station.rentVehicule();
            this.rentedVehicule = vehicule;
            return result;
        } else {
            // Pas assez d'argent
            return null;
        }
    }

    public String park(Station s) throws CannotParkException {
        if (rentedVehicule != null && !s.isFull()) {
            String result = s.parkVehiculeWithMessage(rentedVehicule);
            this.rentedVehicule = null;
            return result;
        }
        return null;
    }

    public Vehicule getRentedVehicule() {
        return rentedVehicule;
    }
}