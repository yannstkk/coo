
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import exceptions.IllegalStateException;
import vehicle.ClassicBicycle;
import vehicle.Vehicule;
import vehicle.state.ParkedState;
import vehicle.state.InUseState;
import vehicle.state.UnderRepairState;
import vehicle.state.StolenState;

/**
 * Tests pour vérifier les transitions d'états des véhicules
 * et les règles métier associées à chaque état
 */

public class StateTest {

    private Vehicule vehicule;

    @BeforeEach
    public void setUp() {
        vehicule = new ClassicBicycle(10.0);
    }

    @Test
    public void testParkedStateInitial() {
        assertTrue(vehicule.getVehiculeState() instanceof ParkedState,
                "Un véhicule devrait être initialement en état Parked");
    }

    @Test
    public void testParkedToInUse() throws IllegalStateException {
        vehicule.getVehiculeState().in_use();

        assertTrue(vehicule.getVehiculeState() instanceof InUseState,
                "Le véhicule devrait passer en état InUse");
        assertEquals(1, vehicule.getLocationNb(),
                "Le compteur de location devrait etre incrémenté");
    }

    @Test
    public void testParkedToUnderRepair() throws IllegalStateException {
        vehicule.getVehiculeState().underRepair();

        assertTrue(vehicule.getVehiculeState() instanceof UnderRepairState,
                "Le véhicule devrait passer en état UnderRepair");
    }

    @Test
    public void testParkedToStolen() throws IllegalStateException {
        vehicule.getVehiculeState().stolen();

        assertTrue(vehicule.getVehiculeState() instanceof StolenState,
                "Le véhicule devrait passer en état Stolen");
    }

    @Test
    public void testInUseToParked() throws IllegalStateException {
        vehicule.getVehiculeState().in_use();
        vehicule.getVehiculeState().parked();

        assertTrue(vehicule.getVehiculeState() instanceof ParkedState,
                "Le véhicule devrait revenir en état Parked");
    }

    @Test
    public void testInUseCannotBeRepairedWhileInUse() throws IllegalStateException {
        vehicule.getVehiculeState().in_use();

        assertThrows(IllegalStateException.class,
                () -> vehicule.getVehiculeState().underRepair(),
                "Un véhicule en usage ne peut pas etre réparé");
    }

    @Test
    public void testInUseCannotBeUsedTwice() throws IllegalStateException {
        vehicule.getVehiculeState().in_use();

        assertThrows(IllegalStateException.class,
                () -> vehicule.getVehiculeState().in_use(),
                "Un véhicule déjà en usage ne peut pas etre reloué");
    }

    @Test
    public void testInUseCannotBeStolen() throws IllegalStateException {
        vehicule.getVehiculeState().in_use();

        assertThrows(IllegalStateException.class,
                () -> vehicule.getVehiculeState().stolen(),
                "Un véhicule en usage ne peut pas etre volé");
    }

    @Test
    public void testUnderRepairToParked() throws IllegalStateException {
        vehicule.setState(new UnderRepairState(vehicule));
        vehicule.setRepairIntervalsRemaining(0);

        vehicule.getVehiculeState().parked();

        assertTrue(vehicule.getVehiculeState() instanceof ParkedState,
                "Le véhicule devrait revenir en état Parked apres réparation");
        assertEquals(0, vehicule.getLocationNb(),
                "Le compteur de locations devrait etre réinitialisé");
    }

    @Test
    public void testUnderRepairCannotBeStolen() {
        vehicule.setState(new UnderRepairState(vehicule));

        assertThrows(IllegalStateException.class,
                () -> vehicule.getVehiculeState().stolen(),
                "Un véhicule en réparation ne peut pas etre volé");
    }

    @Test
    public void testStolenCannotBeRepaired() {
        vehicule.setState(new StolenState(vehicule));

        assertThrows(IllegalStateException.class,
                () -> vehicule.getVehiculeState().underRepair(),
                "Un véhicule volé ne peut pas etre réparé");
    }

    @Test
    public void testStolenCannotBeUsed() {
        vehicule.setState(new StolenState(vehicule));

        assertThrows(IllegalStateException.class,
                () -> vehicule.getVehiculeState().in_use(),
                "Un véhicule volé ne peut pas être utilisé");
    }

    @Test
    public void testStolenCannotBeParked() {
        vehicule.setState(new StolenState(vehicule));

        assertThrows(IllegalStateException.class,
                () -> vehicule.getVehiculeState().parked(),
                "Un véhicule volé ne peut pas être garé");
    }

    @Test
    public void testStolenCannotBeStolenAgain() {
        vehicule.setState(new StolenState(vehicule));

        assertThrows(IllegalStateException.class,
                () -> vehicule.getVehiculeState().stolen(),
                "Un véhicule déjà volé ne peut pas être volé a nouveau");
    }

    @Test
    public void testLocationCounterIncrements() throws IllegalStateException {
        assertEquals(0, vehicule.getLocationNb());

        for (int i = 1; i <= 3; i++) {
            vehicule.setState(new ParkedState(vehicule));
            vehicule.getVehiculeState().in_use();
            assertEquals(i, vehicule.getLocationNb(),
                    "Le compteur devrait etre a " + i);
        }
    }

    @Test
    public void testRepairResetsLocationCounter() throws IllegalStateException {
        for (int i = 0; i < 3; i++) {
            vehicule.setState(new ParkedState(vehicule));
            vehicule.getVehiculeState().in_use();
        }
        assertEquals(3, vehicule.getLocationNb());

        vehicule.setState(new UnderRepairState(vehicule));
        vehicule.setRepairIntervalsRemaining(0);
        vehicule.getVehiculeState().parked();

        assertEquals(0, vehicule.getLocationNb(),
                "Le compteur devrait etre réinitialisé apres réparation");
    }

}