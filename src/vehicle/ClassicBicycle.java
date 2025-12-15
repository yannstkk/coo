package vehicle;

/**
 * Represents a classic bicycle in the bike-sharing system.
 * A basic vehicle type with standard pricing.
 */
public class ClassicBicycle extends Vehicule {
    /**
     * @param price the base price of the bicycle
     */
    public ClassicBicycle(double price) {
        super(price);
    }

    /**
     * @return the price of the bicycle
     */
    @Override
    public double getPrice() {
        return price;
    }
}