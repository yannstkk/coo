package Mehdi;

public class ElectricAssistBicycle extends Vehicule {
    public ElectricAssistBicycle(int id, State state, double price) {
        super(id, state, price + 5); 
    }

    @Override
    public double getPrice() {
        return price;
    }
}