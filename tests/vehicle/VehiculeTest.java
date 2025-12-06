import org.junit.jupiter.api.Test;

import vehicle.ClassicBicycle;
import vehicle.ElectricAssistBicycle;
import vehicle.state.InUseState;
import vehicle.state.ParkedState;
import vehicle.state.UnderRepairState;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class VehiculeTest {

    private ClassicBicycle classicBike;
    private ElectricAssistBicycle electricBike;

    @BeforeEach
    public void setUp() {
        classicBike = new ClassicBicycle(10.0);
        electricBike = new ElectricAssistBicycle(10.0);
    }

    @Test
    public void testClassicBicycleCreation() {
        assertEquals(1, classicBike.getId());
        assertTrue(classicBike.getVehiculeState() instanceof ParkedState);
        assertEquals(10.0, classicBike.getPrice());
        assertEquals(0, classicBike.getLocationNb());
    }

    @Test
    public void testElectricBicyclePrice() {
        assertEquals(15.0, electricBike.getPrice());
    }

    @Test
    public void testSetState() {
        classicBike.setState(new InUseState(classicBike));
        assertTrue(classicBike.getVehiculeState() instanceof InUseState);

        classicBike.setState(new UnderRepairState(classicBike));
        assertTrue(classicBike.getVehiculeState() instanceof UnderRepairState);
    }

    @Test
    public void testIncrementLocationNb() {
        assertEquals(0, classicBike.getLocationNb());

        classicBike.incrementLocationNb();
        assertEquals(1, classicBike.getLocationNb());

        classicBike.incrementLocationNb();
        classicBike.incrementLocationNb();
        assertEquals(3, classicBike.getLocationNb());
    }

    @Test
    public void testRepairIntervalsRemaining() {
        assertEquals(0, classicBike.getRepairIntervalsRemaining());

        classicBike.setRepairIntervalsRemaining(3);
        assertEquals(3, classicBike.getRepairIntervalsRemaining());

        classicBike.setRepairIntervalsRemaining(0);
        assertEquals(0, classicBike.getRepairIntervalsRemaining());
    }

    @Test
    public void testMultipleLocationIncrements() {
        for (int i = 0; i < 10; i++) {
            classicBike.incrementLocationNb();
        }
        assertEquals(10, classicBike.getLocationNb());
    }
}