package control.strategy;

import vehicle.Vehicule;

public class Slot {
    private int num;
    private boolean isOccupied;
    private Vehicule actualVehicule;

    public Slot(int num) {
        this.num = num;
        this.isOccupied = false;
        this.actualVehicule = null;
    }

    public int getNum() {
        return num;
    }

    public boolean getIsOccupied() {
        return isOccupied;
    }

    public void setIsOccupied(boolean isOccupied) {
        this.isOccupied = isOccupied;
    }

    public Vehicule getActualVehicule() {
        return actualVehicule;
    }

    public void setActualVehicule(Vehicule actualVehicule) {
        this.actualVehicule = actualVehicule;
    }
}
