import org.junit.jupiter.api.Test;

import control.strategy.Slot;
import vehicle.ClassicBicycle;
import vehicle.Vehicule;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class SlotTest {

    private Slot slot;
    private Vehicule vehicule;

    @BeforeEach
    public void setUp() {
        slot = new Slot(5);
        vehicule = new ClassicBicycle(10.0);
    }

    @Test
    public void testSlotCreation() {

        assertEquals(5, slot.getNum());

        assertFalse(slot.getIsOccupied());
        assertNull(slot.getActualVehicule());
    }

    @Test
    public void testOccupySlot() {

        slot.setIsOccupied(true);
        slot.setActualVehicule(vehicule);

        assertTrue(slot.getIsOccupied());
        assertSame(vehicule, slot.getActualVehicule());
    }

    @Test
    public void testFreeSlot() {
        slot.setIsOccupied(true);
        slot.setActualVehicule(vehicule);

        slot.setIsOccupied(false);

        slot.setActualVehicule(null);

        assertFalse(slot.getIsOccupied());
        assertNull(slot.getActualVehicule());
    }

}