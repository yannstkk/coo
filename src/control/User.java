package control;

import java.util.Set;

import control.strategy.Slot;
import exceptions.CannotParkException;
import vehicle.Vehicule;
import vehicle.state.ParkedState;

/**
 * Represents a user in the bike-sharing system.
 * Manages user balance, vehicle rentals, and parking operations.
 */
public class User {
    private double balance;
    private Vehicule rentedVehicule;

    /**
     * Creates a new user with the given details
     * 
     * @param firstName the user's first name
     * @param lastName  the user's last name
     * @param balance   the initial balance
     */
    public User(String firstName, String lastName, double balance) {
        this.balance = balance;
        this.rentedVehicule = null;
    }

    /**
     * Gets the user's current balance
     * 
     * @return the current balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Rents a vehicle from the station
     * 
     * @param station the station to rent from
     * @return a message about the rental, or null if rental failed
     * @throws CannotParkException if parking operations fail
     */
    public String rent(Station station) throws CannotParkException {
        return rent(station, null);
    }

    /**
     * Rents a vehicle from the station, excluding already used vehicles
     * 
     * @param station               the station to rent from
     * @param alreadyUsedVehicleIds set of vehicle IDs to exclude from selection
     * @return a message about the rental, or null if rental failed
     * @throws CannotParkException if parking operations fail
     */
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

    /**
     * Parks the currently rented vehicle at the station
     * 
     * @param s the station to park at
     * @return a message about the parking, or null if parking failed
     * @throws CannotParkException if the vehicle cannot be parked
     */
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

    /**
     * Gets the currently rented vehicle
     * 
     * @return the currently rented vehicle, or null if none
     */
    public Vehicule getRentedVehicule() {
        return rentedVehicule;
    }
}