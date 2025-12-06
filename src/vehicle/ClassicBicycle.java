package vehicle;

public class ClassicBicycle extends Vehicule {
    public ClassicBicycle(double price) {
        super(price);
    }

    @Override
    public double getPrice() {
        return price;
    }
}