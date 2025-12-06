package vehicle.state;

import vehicle.Vehicule;

public class StolenState extends VehiculeState {

    public StolenState(Vehicule vehicule) {
        super(vehicule);
    }

    @Override
    public void underRepair() throws IllegalStateException {
        throw new IllegalStateException("Vehicule stolen");
    }

    @Override
    public void in_use() throws IllegalStateException {
        throw new IllegalStateException("Vehicule stolen");
    }

    @Override
    public void stolen() throws IllegalStateException {
        throw new IllegalStateException("Vehicule already marked as stolen");
    }

    @Override
    public void parked() throws IllegalStateException {
        throw new IllegalStateException("Vehicule stolen");
    }
}
