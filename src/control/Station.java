package control;

import java.util.ArrayList;
import java.util.List;
import control.observer.Observer;
import control.strategy.Slot;
import vehicle.Vehicule;
import vehicle.state.InUseState;
import vehicle.state.ParkedState;
import vehicle.state.StolenState;
import vehicle.state.UnderRepairState;

/**
 * Represents a bike-sharing station that manages vehicle parking slots.
 * Handles vehicle rentals, returns, theft detection, and redistribution.
 * Uses the Observer pattern to notify listeners of station events.
 */
public class Station {
    private int id;
    private int capacity;
    private List<Slot> slotList = new ArrayList<>();
    private List<Observer> observers = new ArrayList<>();
    private int emptyIntervals = 0;

    private int fullIntervals = 0;
    private int IntervalsOfTheft = 0;
    private Colors colors = new Colors();

    /**
     * Creates a new station with the given ID and capacity
     * 
     * @param id       the station identifier
     * @param capacity the number of slots
     */
    public Station(int id, int capacity) {
        this.id = id;
        this.capacity = capacity;

        for (int i = 0; i < capacity; i++) {
            slotList.add(new Slot(i));
        }
    }

    /**
     * Attaches an observer to the station
     * 
     * @param observer the observer to attach
     */
    public void attach(Observer observer) {
        observers.add(observer);
    }

    /**
     * Notifies all observers about an action
     * 
     * @param action the action to notify observers about
     */
    private void notifyObservers(String action) {
        for (Observer o : observers) {
            o.update(this, action);
        }
    }

    /**
     * Checks if the station is empty
     * 
     * @return true if no slots are occupied
     */
    public boolean isEmpty() {
        return getNbOccupiedSlot() == 0;
    }

    /**
     * Checks if the station is full
     * 
     * @return true if all slots are occupied
     */
    public boolean isFull() {
        return getNbOccupiedSlot() == capacity;
    }

    /**
     * Counts the number of occupied slots
     * 
     * @return the number of occupied slots
     */
    public int getNbOccupiedSlot() {
        int count = 0;
        for (Slot slot : slotList) {
            if (slot.getIsOccupied())
                count++;
        }
        return count;
    }

    /**
     * Resets the empty, full, and theft interval counters if conditions changed
     */
    private void resetCountersIfChanged() {
        if (!isEmpty())
            emptyIntervals = 0;
        if (!isFull())
            fullIntervals = 0;
        if (getNbOccupiedSlot() != 1)
            IntervalsOfTheft = 0;
    }

    /**
     * Gets the station ID
     * 
     * @return the station ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the list of slots in the station
     * 
     * @return the list of slots
     */
    public List<Slot> getSlotList() {
        return slotList;
    }

    /**
     * Gets the station capacity
     * 
     * @return the station capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Parks a vehicle at the station without returning a message
     * 
     * @param vehicule the vehicle to park
     */
    public void parkVehicule(Vehicule vehicule) {
        parkVehiculeWithMessage(vehicule);
    }

    /**
     * Parks a vehicle at the station and returns a status message
     * 
     * @param vehicule the vehicle to park
     * @return a message about the parking, or null if parking failed
     */
    public String parkVehiculeWithMessage(Vehicule vehicule) {
        if (!isFull() && !(vehicule.getVehiculeState() instanceof UnderRepairState)
                && !(vehicule.getVehiculeState() instanceof StolenState)) {

            for (Slot slot : slotList) {
                if (!slot.getIsOccupied()) {
                    slot.setActualVehicule(vehicule);
                    slot.setIsOccupied(true);
                    vehicule.setState(new ParkedState(vehicule));
                    notifyObservers("park");
                    resetCountersIfChanged();

                    return colors.getGreen() + "Vélo #" + vehicule.getId() +
                            " garé à la Station " + this.id +
                            " (" + vehicule.getLocationNb() + " location(s))" + colors.getReset();
                }
            }
        }
        return null;
    }

    /**
     * Rents the first available vehicle from the station
     * 
     * @return a message about the rental, or null if no vehicle available
     */
    public String rentVehicule() {
        if (!isEmpty()) {
            for (Slot slot : slotList) {
                if (slot.getIsOccupied() && slot.getActualVehicule().getVehiculeState() instanceof ParkedState) {
                    Vehicule v = slot.getActualVehicule();
                    slot.setActualVehicule(null);
                    slot.setIsOccupied(false);
                    v.setState(new InUseState(v));
                    v.incrementLocationNb();

                    notifyObservers("rent");
                    resetCountersIfChanged();

                    return colors.getBlue() + "Vélo #" + v.getId() +
                            " loué depuis la Station " + this.id +
                            " (" + v.getLocationNb() + " location(s))" + colors.getReset();
                }
            }
        }
        return null;
    }

    /**
     * Rents a specific vehicle from the station
     * 
     * @param vehicule the specific vehicle to rent
     * @return a message about the rental, or null if rental failed
     */
    public String rentSpecificVehicule(Vehicule vehicule) {
        if (vehicule == null)
            return null;

        for (Slot slot : slotList) {
            if (slot.getIsOccupied() &&
                    slot.getActualVehicule() == vehicule &&
                    vehicule.getVehiculeState() instanceof ParkedState) {

                slot.setActualVehicule(null);
                slot.setIsOccupied(false);
                vehicule.setState(new InUseState(vehicule));
                vehicule.incrementLocationNb();

                notifyObservers("rent");
                resetCountersIfChanged();

                return colors.getBlue() + "Vélo #" + vehicule.getId() +
                        " loué depuis la Station " + this.id +
                        " (" + vehicule.getLocationNb() + " location(s))" + colors.getReset();
            }
        }
        return null;
    }

    /**
     * Gets the first available parked vehicle in the station
     * 
     * @return the first available vehicle, or null if none available
     */
    public Vehicule getFirstAvailableVehicule() {
        for (Slot slot : slotList) {
            if (slot.getIsOccupied() && slot.getActualVehicule().getVehiculeState() instanceof ParkedState) {
                return slot.getActualVehicule();
            }
        }
        return null;
    }

    /**
     * Removes a vehicle from the station for redistribution purposes
     * 
     * @return the removed vehicle, or null if no vehicle available
     */
    public Vehicule removeVehiculeForRedistribution() {
        if (!isEmpty()) {
            for (Slot slot : slotList) {
                if (slot.getIsOccupied() && slot.getActualVehicule().getVehiculeState() instanceof ParkedState) {
                    Vehicule v = slot.getActualVehicule();
                    slot.setActualVehicule(null);
                    slot.setIsOccupied(false);
                    notifyObservers("redistribute_remove");
                    resetCountersIfChanged();
                    return v;
                }
            }
        }
        return null;
    }

    /**
     * Verifies if a theft occurred and marks the vehicle as stolen if conditions
     * are met
     * 
     * @return a message about the theft, or null if no theft detected
     */
    public String verifyStolen() {
        if (getNbOccupiedSlot() == 1) {
            Slot slot = slotList.stream().filter(Slot::getIsOccupied).findFirst().orElse(null);

            if (slot != null && slot.getActualVehicule().getVehiculeState() instanceof ParkedState) {
                IntervalsOfTheft++;

                if (IntervalsOfTheft >= 2) {
                    Vehicule v = slot.getActualVehicule();

                    v.setState(new StolenState(v));
                    slot.setActualVehicule(null);
                    slot.setIsOccupied(false);
                    notifyObservers("stolen");
                    IntervalsOfTheft = 0;

                    return colors.getRed() + "VOL : Vélo #" + v.getId() +
                            " volé à la Station " + id + colors.getReset();
                }
            } else {
                IntervalsOfTheft = 0;
            }
        } else {
            IntervalsOfTheft = 0;
        }
        return null;
    }

    /**
     * Increments the empty and full interval counters based on station status
     */
    public void incrementEmptyFullCounters() {
        if (isEmpty()) {
            emptyIntervals++;
        } else {
            emptyIntervals = 0;
        }

        if (isFull()) {
            fullIntervals++;
        } else {
            fullIntervals = 0;
        }
    }

    /**
     * Checks if the station needs redistribution based on interval counters
     * 
     * @return true if the station needs redistribution
     */
    public boolean needsRedistribution() {
        return emptyIntervals >= 2 || fullIntervals >= 2;
    }

    /**
     * Gets the count of occupied slots using stream operations
     * 
     * @return the number of occupied slots
     */
    public int getOccupiedCount() {
        return (int) getSlotList().stream()
                .filter(slot -> slot.getIsOccupied())
                .count();
    }
}