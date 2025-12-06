import org.junit.jupiter.api.Test;

import vehicle.ClassicBicycle;
import vehicle.ElectricAssistBicycle;
import vehicle.Vehicule;
import vehicle.accessory.Basket;
import vehicle.accessory.LuggageRack;
import vehicle.state.ParkedState;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class VehiculeDecoratorTest {

    private ClassicBicycle baseBike;

    @BeforeEach
    public void setUp() {
        baseBike = new ClassicBicycle(10.0);
    }

    @Test
    public void testBasketDecorator() {
        Basket bikeWithBasket = new Basket(baseBike);

        assertEquals(15.0, bikeWithBasket.getPrice());
        assertEquals(1, bikeWithBasket.getId());
        assertTrue(bikeWithBasket.getVehiculeState() instanceof ParkedState);

    }

    @Test
    public void testLuggageRackDecorator() {

        Vehicule bikeWithRack = new LuggageRack(baseBike);

        assertEquals(20.0, bikeWithRack.getPrice());

        assertEquals(1, bikeWithRack.getId());
    }

    @Test
    public void testMultipleDecorators() {
        Vehicule decorated = new Basket(baseBike);
        decorated = new LuggageRack(decorated);

        assertEquals(25.0, decorated.getPrice());
    }

    @Test
    public void testDecoratorOrder() {
        Vehicule decorated1 = new LuggageRack(new Basket(baseBike));
        Vehicule decorated2 = new Basket(new LuggageRack(baseBike));

        assertEquals(25.0, decorated1.getPrice());
        assertEquals(25.0, decorated2.getPrice());
    }

    @Test
    public void testDecoratorWithElectricBike() {
        ElectricAssistBicycle electricBike = new ElectricAssistBicycle(10.0);
        Vehicule decorated = new Basket(new LuggageRack(electricBike));

        assertEquals(30.0, decorated.getPrice());
    }

}