package technician;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import exceptions.IllegalStateException;
import intervenant.Technician;
import vehicle.ClassicBicycle;
import vehicle.Vehicule;
import vehicle.state.ParkedState;
import vehicle.state.UnderRepairState;
import vehicle.state.InUseState;

/**
 * Tests pour vérifier le comportement du Technicien
 * lors de la réparation des véhicules
 */
public class TechnicianTest {

        private Technician technician;
        private Vehicule vehicule;

        @BeforeEach
        public void setUp() {
                technician = new Technician();
                vehicule = new ClassicBicycle(10.0);
        }

        @Test
        public void testTechnicianCreation() {
                assertNotNull(technician, "Le technicien devrait être créé");
        }

        @Test
        public void testRepairCompletesAfterLastInterval() throws IllegalStateException {
                vehicule.setState(new UnderRepairState(vehicule));
                vehicule.setRepairIntervalsRemaining(1);

                technician.visit(vehicule);

                assertEquals(0, vehicule.getRepairIntervalsRemaining(),
                                "Le compteur devrait être a 0");
                assertTrue(vehicule.getVehiculeState() instanceof ParkedState,
                                "Le véhicule devrait etre en état Parked après réparation complete");
        }

        @Test
        public void testRepairWith2Intervals() throws IllegalStateException {
                vehicule.setState(new UnderRepairState(vehicule));
                vehicule.setRepairIntervalsRemaining(2);

                technician.visit(vehicule);
                assertEquals(1, vehicule.getRepairIntervalsRemaining(),
                                "Après le 1er passage : 1 intervalle restant");
                assertTrue(vehicule.getVehiculeState() instanceof UnderRepairState,
                                "Le véhicule devrait toujours être en réparation");

                technician.visit(vehicule);
                assertEquals(0, vehicule.getRepairIntervalsRemaining(),
                                "Après le 2ème passage : 0 intervalle restant");
                assertTrue(vehicule.getVehiculeState() instanceof ParkedState,
                                "Le véhicule devrait être réparé et parqué");
        }

        @Test
        public void testRepairResetsLocationCounter() throws IllegalStateException {
                vehicule.setState(new ParkedState(vehicule));
                for (int i = 0; i < 5; i++) {
                        vehicule.getVehiculeState().in_use();
                        vehicule.setState(new ParkedState(vehicule));
                }
                assertEquals(5, vehicule.getLocationNb(),
                                "Le véhicule devrait avoir 5 locations");

                vehicule.setState(new UnderRepairState(vehicule));
                vehicule.setRepairIntervalsRemaining(1);
                technician.visit(vehicule);

                assertEquals(0, vehicule.getLocationNb(),
                                "Le compteur de locations devrait être réinitialisé à 0");
                assertTrue(vehicule.getVehiculeState() instanceof ParkedState,
                                "Le véhicule devrait être en état Parked");
        }

        @Test
        public void testTechnicianDoesNotAffectParkedVehicle() throws IllegalStateException {
                vehicule.setState(new ParkedState(vehicule));
                int initialLocationNb = vehicule.getLocationNb();

                // Le technicien visite (ne devrait rien faire)
                technician.visit(vehicule);

                assertTrue(vehicule.getVehiculeState() instanceof ParkedState,
                                "L'état ne devrait pas changer");
                assertEquals(initialLocationNb, vehicule.getLocationNb(),
                                "Le compteur de locations ne devrait pas changer");
        }

        @Test
        public void testCompleteRepairCycle() throws IllegalStateException {

                vehicule.setState(new ParkedState(vehicule));
                vehicule.locationNb = 6;
                assertEquals(6, vehicule.getLocationNb());

                vehicule.setState(new UnderRepairState(vehicule));
                vehicule.setRepairIntervalsRemaining(2);
                assertTrue(vehicule.getVehiculeState() instanceof UnderRepairState);

                technician.visit(vehicule);
                assertEquals(1, vehicule.getRepairIntervalsRemaining());
                assertTrue(vehicule.getVehiculeState() instanceof UnderRepairState);

                technician.visit(vehicule);
                assertEquals(0, vehicule.getRepairIntervalsRemaining());
                assertTrue(vehicule.getVehiculeState() instanceof ParkedState);

                assertEquals(0, vehicule.getLocationNb(),
                                "Le compteur devrait être réinitialisé");

                vehicule.getVehiculeState().in_use();
                assertTrue(vehicule.getVehiculeState() instanceof InUseState);
                assertEquals(1, vehicule.getLocationNb(),
                                "Le compteur devrait recommencer à 1");
        }
}