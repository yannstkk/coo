package vehicle.state;

import vehicle.Vehicule;

public abstract class VehiculeState {
    protected Vehicule vehicule;

    public VehiculeState(Vehicule vehicule) {
        this.vehicule = vehicule;
    }

    public abstract void underRepair() throws IllegalStateException;

    public abstract void in_use() throws IllegalStateException;

    public abstract void stolen() throws IllegalStateException;

    public abstract void parked() throws IllegalStateException;
}
