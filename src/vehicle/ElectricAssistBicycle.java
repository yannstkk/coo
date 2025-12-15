package vehicle;

/**
 * Represents an electric assist bicycle in the bike-sharing system.
 * A vehicle type with electric motor assistance, typically at a higher rental
 * price.
 */
public class ElectricAssistBicycle extends Vehicule {
    /**
     * @param price the base price of the electric bicycle
     */
    public ElectricAssistBicycle(double price) {
        super(price);
    }

    /**
     * Gets the rental price of the electric bicycle
     * 
     * @return the price of the bicycle
     */
    @Override
    public double getPrice() {
        return price;
    }
}