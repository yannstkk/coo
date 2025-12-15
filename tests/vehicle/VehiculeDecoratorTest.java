
import org.junit.jupiter.api.Test;

import exceptions.IllegalStateException;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import vehicle.ClassicBicycle;
import vehicle.ElectricAssistBicycle;
import vehicle.Vehicule;
import vehicle.accessory.Basket;
import vehicle.accessory.LuggageRack;
import vehicle.state.ParkedState;

/**
 * Tests pour vérifier le pattern Decorator appliqué aux véhicules
 * avec les accessoires Basket et LuggageRack
 */
public class VehiculeDecoratorTest {

    private ClassicBicycle baseBike;
    private ElectricAssistBicycle electricBike;

    @BeforeEach
    public void setUp() {
        baseBike = new ClassicBicycle(10.0);
        electricBike = new ElectricAssistBicycle(15.0);
    }

    @Test
    public void testBasketDecoratorAddsCorrectPrice() {
        Vehicule bikeWithBasket = new Basket(baseBike);

        assertEquals(15.0, bikeWithBasket.getPrice(), 0.01,
                "Le panier devrait ajouter 5 euro au prix de base (10 + 5 = 15)");
    }

    @Test
    public void testLuggageRackDecoratorAddsCorrectPrice() {
        Vehicule bikeWithRack = new LuggageRack(baseBike);

        assertEquals(20.0, bikeWithRack.getPrice(), 0.01,
                "Le porte bagages devrait ajouter 10 euro au prix de base (10 + 10 = 20)");
    }

    @Test
    public void testMultipleDecoratorsStackPrices() {
        Vehicule decorated = new Basket(baseBike);
        decorated = new LuggageRack(decorated);

        assertEquals(25.0, decorated.getPrice(), 0.01,
                "Panier + Porte bagages = 10 + 5 + 10 = 25 euro");
    }

    @Test
    public void testDecoratorPreservesVehicleState() {
        Vehicule bikeWithBasket = new Basket(baseBike);

        assertTrue(bikeWithBasket.getVehiculeState() instanceof ParkedState,
                "Le véhicule décoré devrait conserver son état initial (Parked)");
    }

    @Test
    public void testDecoratorWorksWithElectricBike() {
        Vehicule decorated = new Basket(new LuggageRack(electricBike));

        assertEquals(30.0, decorated.getPrice(), 0.01,
                "Vélo électrique (15) + Porte bagages ( 10) + Panier (5) = 30 euro");
    }

    @Test
    public void testDecoratorOrderDoesNotMatterForPrice() {
        Vehicule option1 = new LuggageRack(new Basket(baseBike));
        Vehicule option2 = new Basket(new LuggageRack(baseBike));

        assertEquals(25.0, option1.getPrice(), 0.01);
        assertEquals(25.0, option2.getPrice(), 0.01);
        assertEquals(option1.getPrice(), option2.getPrice(), 0.01,
                "L'ordre des décorateur ne devrait pas affecter le prix");
    }

    @Test
    public void testMultipleBaskets() {
        Vehicule multiBasket = new Basket(new Basket(new Basket(baseBike)));

        assertEquals(25.0, multiBasket.getPrice(), 0.01,
                "Troi paniers = 10 + 5 + 5 + 5 = 25 euro");
    }

    @Test
    public void testDecoratedVehicleCanBeUsed() throws IllegalStateException {
        Vehicule decorated = new Basket(new LuggageRack(baseBike));

        decorated.getVehiculeState().in_use();
        assertEquals(1, decorated.getLocationNb(),
                "Le véhicule décoré devrait pouvoir être loué normalement");
    }

}