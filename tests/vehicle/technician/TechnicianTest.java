package technician;

import org.junit.jupiter.api.Test;

import intervenant.Technician;
import vehicle.ClassicBicycle;
import vehicle.Vehicule;
import vehicle.state.ParkedState;
import vehicle.state.UnderRepairState;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class TechnicianTest {

    private Technician technician;
    private Vehicule vehicule;

    @BeforeEach
    public void setUp() {

        technician = new Technician();
        vehicule = new ClassicBicycle(10.0);
        vehicule.setRepairIntervalsRemaining(3);
    }

    @Test
    public void testRepairVehicule() {

        technician.visit(vehicule);

        assertTrue(vehicule.getVehiculeState() instanceof ParkedState);
        assertEquals(0, vehicule.getRepairIntervalsRemaining());
    }

    @Test
    public void testRepairResetsCounters() {

        vehicule.setRepairIntervalsRemaining(5);

        technician.visit(vehicule);

        assertEquals(0, vehicule.getRepairIntervalsRemaining());
    }

    @Test
    public void testRepairChangesState() {

        vehicule.setState(new UnderRepairState(vehicule));
        assertTrue(vehicule.getVehiculeState() instanceof UnderRepairState);

        technician.visit(vehicule);
        assertTrue(vehicule.getVehiculeState() instanceof ParkedState);
    }

}