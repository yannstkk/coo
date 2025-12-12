package control;

import control.strategy.Slot;
import exceptions.CannotParkException;
import vehicle.Vehicule;
import vehicle.state.ParkedState;

public class User {
    private double balance;

    public double getBalance() {
        return balance;
    }

    private Vehicule rentedVehicule;

    public User(String firstName, String lastName, double balance) {
        this.balance = balance;
        this.rentedVehicule = null;
    }

    public void rent(Station station) throws CannotParkException {
    if (rentedVehicule != null || station.isEmpty()) {
        return;
    }
    
    Vehicule vehicule = station.rentVehicule();
    
    if (vehicule != null) {
        double price = vehicule.getPrice();
        
        if (this.balance >= price) {
            this.balance -= price;
            this.rentedVehicule = vehicule;
        } else {
            System.out.println(" Pas assez d'argent, vélo remis en place");
            
            // Remet le vélo directement dans le slot sans passer par parkVehicule()
            for (Slot slot : station.getSlotList()) {
                if (!slot.getIsOccupied()) {
                    slot.setActualVehicule(vehicule);
                    slot.setIsOccupied(true);
                    vehicule.setState(new ParkedState(vehicule));
                    break;
                }
            }
        }
    }
}



    public void park(Station s) throws CannotParkException {
        if (rentedVehicule != null && !s.isFull()) {
            s.parkVehicule(rentedVehicule);
            this.rentedVehicule = null;
        }
    }

    // Getters
    public Vehicule getRentedVehicule() {
        return rentedVehicule;
    }
}
