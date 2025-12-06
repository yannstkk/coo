package control;

import exceptions.CannotParkException;
import vehicle.Vehicule;

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

    public void rent(Station station) throws CannotParkException, IllegalStateException {
        if (rentedVehicule == null && !station.isEmpty()) {
            Vehicule vehicule = station.rentVehicule();
            if (vehicule != null) {
                double price = vehicule.getPrice();
                if (this.balance >= price) {
                    this.balance -= price;
                    this.rentedVehicule = vehicule;
                } else {
                    station.parkVehicule(vehicule);
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
