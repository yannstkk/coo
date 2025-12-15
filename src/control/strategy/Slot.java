package control.strategy;

import vehicle.Vehicule;

/**
 * Represents a parking slot that can hold a vehicle.
 * Tracks the slot number, occupation status, and the vehicle currently parked.
 */
public class Slot {
    private int num;
    private boolean isOccupied;
    private Vehicule actualVehicule;

    /**
     * Initializes a new parking slot with a specified number, setting it as
     * unoccupied with no vehicle.
     * 
     * @param num the unique identifier/number for the parking slot
     */
    public Slot(int num) {
        this.num = num;
        this.isOccupied = false;
        this.actualVehicule = null;
    }

    /**
     * Retrieves the slot's identification number.
     * 
     * @return the slot number
     */
    public int getNum() {
        return num;
    }

    /**
     * Checks whether the slot is currently occupied by a vehicle.
     * 
     * @return true if occupied, false if available
     */
    public boolean getIsOccupied() {
        return isOccupied;
    }

    /**
     * Updates the occupation status of the slot.
     * 
     * @param isOccupied the new occupation status (true for occupied, false for
     *                   available)
     */
    public void setIsOccupied(boolean isOccupied) {
        this.isOccupied = isOccupied;
    }

    /**
     * Retrieves the vehicle currently parked in the slot.
     * 
     * @return the vehicle object in the slot, or null if the slot is empty
     */
    public Vehicule getActualVehicule() {
        return actualVehicule;
    }

    /**
     * Assigns a vehicle to the slot.
     * 
     * @param actualVehicule the vehicle object to place in the slot (can be null to
     *                       clear the slot)
     */
    public void setActualVehicule(Vehicule actualVehicule) {
        this.actualVehicule = actualVehicule;
    }
}