package vehicle.state;

import vehicle.Vehicule;

public class ParkedState extends VehiculeState {

    public ParkedState(Vehicule vehicule) {
        super(vehicule);
    }

    public void underRepair() throws IllegalStateException {
        this.vehicule.setState(new UnderRepairState(vehicule));
    }

    public void in_use() {
        this.vehicule.incrementLocationNb();
        this.vehicule.setState(new InUseState(this.vehicule));
    }

    public void stolen() {
        this.vehicule.setState(new StolenState(this.vehicule));
    }

    public void parked() {
    }
}
