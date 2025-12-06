package vehicle;

public class ElectricAssistBicycle extends Vehicule {
    public ElectricAssistBicycle(double price) {
        super(price);
    }

    @Override
    public double getPrice() {
        return price;
    }
}