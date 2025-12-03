package Mehdi;

public abstract class VehiculeDecorator extends Vehicule {
    public Vehicule vehicule;

    public VehiculeDecorator(Vehicule vehicule) {
        super(vehicule.id, vehicule.vehiculeState, vehicule.price);
        this.vehicule = vehicule;
    }

    public abstract double getPrice();
}
