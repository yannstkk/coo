package Mehdi;


public abstract class Vehicule {
    protected int id;
    protected int locationNb;
    protected State vehiculeState;
    protected double price;
    protected int repairIntervalsRemaining = 0;

    public Vehicule(int id, State vehiculeState, double price) {
        this.id = id;
        this.vehiculeState = vehiculeState;
        this.price = price;
        this.locationNb = 0;
    }

    public abstract double getPrice();

    public void setState(State vehiculeState) {
        this.vehiculeState = vehiculeState;
    }

    public void incrementLocationNb() {
        this.locationNb++;
    }

    public State getVehiculeState() {
        return vehiculeState;
    }

    public int getLocationNb() {
        return locationNb;
    }

    public int getId() {
        return id;
    }

    public int getRepairIntervalsRemaining() {
        return repairIntervalsRemaining;
    }

    public void setRepairIntervalsRemaining(int remaining) {
        this.repairIntervalsRemaining = remaining;
    }
}
