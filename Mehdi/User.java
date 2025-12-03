package Mehdi;

import yani.Station;


public class User {
    private String firstName;
    private String lastName;
    private double balance;
    private Vehicule rentedVehicule;

    public User(String firstName, String lastName, double balance) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.balance = balance;
        this.rentedVehicule = null;
    }

    public void rent(Station station) {
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

    public void park(Station s) {
        if (rentedVehicule != null && !s.isFull()) {
            s.parkVehicule(rentedVehicule);
            this.rentedVehicule = null;
        }
    }

    // Getters
    public Vehicule getRentedVehicule() {
        return rentedVehicule;
    }



    public String getName(){
        return this.firstName;
    }

    public Double getBalance(){
        return this.balance;
    }
}
