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

public class Station {
    private int id;
    private int capacity;
    private List<Slot> slotList = new ArrayList<>();
    private List<Observer> observers = new ArrayList<>();
    private int emptyIntervals = 0;
    private int fullIntervals = 0;
    private int IntervalsOfTheft = 0;
    private Colors colors = new Colors();

    public Station(int id, int capacity) {
        this.id = id;
        this.capacity = capacity;

        for (int i = 0; i < capacity; i++) {
            slotList.add(new Slot(i));
        }
    }

    public void attach(Observer observer) {
        observers.add(observer);
    }

    private void notifyObservers(String action) {
        for (Observer o : observers) {
            o.update(this, action);
        }
    }

    public boolean isEmpty() {
        return getNbOccupiedSlot() == 0;
    }

    public boolean isFull() {
        return getNbOccupiedSlot() == capacity;
    }

    public int getNbOccupiedSlot() {
        int count = 0;
        for (Slot slot : slotList) {
            if (slot.getIsOccupied())
                count++;
        }
        return count;
    }

    private void resetCountersIfChanged() {
        if (!isEmpty())
            emptyIntervals = 0;
        if (!isFull())
            fullIntervals = 0;
        if (getNbOccupiedSlot() != 1)
            IntervalsOfTheft = 0;
    }

    public int getId() {
        return id;
    }

    public List<Slot> getSlotList() {
        return slotList;
    }

    public int getCapacity() {
        return capacity;
    }

    public void parkVehicule(Vehicule vehicule) {
        parkVehiculeWithMessage(vehicule);
    }
    
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
    
    public String rentSpecificVehicule(Vehicule vehicule) {
        if (vehicule == null) return null;
        
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

    public Vehicule getFirstAvailableVehicule() {
        for (Slot slot : slotList) {
            if (slot.getIsOccupied() && slot.getActualVehicule().getVehiculeState() instanceof ParkedState) {
                return slot.getActualVehicule();
            }
        }
        return null;
    }

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

    public boolean needsRedistribution() {
        return emptyIntervals >= 2 || fullIntervals >= 2;  
    }

    public int getOccupiedCount() {
        return (int) getSlotList().stream()
                .filter(slot -> slot.getIsOccupied())
                .count();
    }
}