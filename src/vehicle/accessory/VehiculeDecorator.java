package vehicle.accessory;

import vehicle.Vehicule;

public abstract class VehiculeDecorator extends Vehicule {
    public Vehicule vehicule;

    public VehiculeDecorator(Vehicule vehicule) {
        super(vehicule.getPrice());
        this.vehicule = vehicule;
    }

    public abstract double getPrice();
}
