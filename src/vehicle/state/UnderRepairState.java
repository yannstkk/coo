package vehicle.state;

import vehicle.Vehicule;

public class UnderRepairState extends VehiculeState {

    public UnderRepairState(Vehicule vehicule) {
        super(vehicule);
    }

    @Override
    public void underRepair() throws IllegalStateException {
        throw new IllegalStateException("Vehicule already under reparation");
    }

    @Override
    public void in_use() {
        this.vehicule.setState(new InUseState(this.vehicule));
    }

    @Override
    public void stolen() throws IllegalStateException {
        throw new IllegalStateException("Vehicule cannot be stolen");
    }

    @Override
    public void parked() {
        this.vehicule.locationNb = 0;
        this.vehicule.setState(new ParkedState(this.vehicule));
    }
}
