package vehicle.state;

import vehicle.Vehicule;

public class InUseState extends VehiculeState {

    public InUseState(Vehicule vehicule) {
        super(vehicule);
    }

    @Override
    public void underRepair() throws IllegalStateException {
        throw new IllegalStateException("Vehicule cannot be repaired while in use");
    }

    @Override
    public void in_use() throws IllegalStateException {
        throw new IllegalStateException("Vehicule already in use");
    }

    @Override
    public void stolen() throws IllegalStateException {
        throw new IllegalStateException("Vehicule cannot be stolen while in use");
    }

    @Override
    public void parked() {
        if (this.vehicule.getLocationNb() == 5) {
            this.vehicule.setRepairIntervalsRemaining(2);
            this.vehicule.setState(new UnderRepairState(vehicule));
        } else {
            this.vehicule.setState(new ParkedState(vehicule));
        }
    }

}
