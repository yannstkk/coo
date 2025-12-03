package Mehdi;

public class ClassicBicycle extends Vehicule {
    public ClassicBicycle(int id, State state, double price) {
        super(id, state, price);
    }

    @Override
    public double getPrice() {
        return price;
    }
}