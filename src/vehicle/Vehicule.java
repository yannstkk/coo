package vehicle;

import intervenant.Technician;
import intervenant.visitor.Visitor;
import vehicle.state.ParkedState;
import vehicle.state.VehiculeState;

public abstract class Vehicule {
    protected static int count = 1;
    protected int id;
    public int locationNb;
    protected VehiculeState vehiculeState;
    protected double price;
    public int repairIntervalsRemaining = 0;
    protected Technician technicien;

    public Vehicule(double price) {
        this.id = count;
        count++;
        this.vehiculeState = new ParkedState(this);
        this.price = price;
        this.locationNb = 0;
    }

    public abstract double getPrice();

    public void setState(VehiculeState vehiculeState) {
        this.vehiculeState = vehiculeState;
    }

    public void incrementLocationNb() {
        this.locationNb++;
    }

    public VehiculeState getVehiculeState() {
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

    public void accept(Visitor v) throws IllegalStateException {
        v.visit(this);
    }
}
