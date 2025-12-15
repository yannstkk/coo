
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import vehicle.ClassicBicycle;
import vehicle.ElectricAssistBicycle;
import vehicle.Vehicule;
import vehicle.state.InUseState;
import vehicle.state.ParkedState;
import vehicle.state.UnderRepairState;
import vehicle.state.StolenState;


public class VehiculeTest {

        private ClassicBicycle classicBike;
        private ElectricAssistBicycle electricBike;

        @BeforeEach
        public void setUp() {
                Vehicule.reset();
                classicBike = new ClassicBicycle(10.0);
                electricBike = new ElectricAssistBicycle(15.0);
        }

        @Test
        public void testClassicBicycleCreation() {

                assertTrue(classicBike.getVehiculeState() instanceof ParkedState,
                                "Un véhicule devrait etre initialement en état Parked");
                assertEquals(10.0, classicBike.getPrice(), 0.01,
                                "Le prix devrait correspondre au prix passé au constructeur");
                assertEquals(0, classicBike.getLocationNb(),
                                "Le compteur de locations devrait être initialisé a 0");
                assertEquals(0, classicBike.getRepairIntervalsRemaining(),
                                "Les intervalles de réparation devraient être a 0");
        }

        @Test
        public void testElectricBicycleCreation() {
                assertEquals(15.0, electricBike.getPrice(), 0.01,
                                "Le vélo électrique devrait avoir son propre prix");
                assertTrue(electricBike.getVehiculeState() instanceof ParkedState,
                                "Le vélo électrique devrait aussi etre initialement Parked");
        }

        @Test
        public void testUniqueIdGeneration() {

                Vehicule v1 = new ClassicBicycle(10.0);
                Vehicule v2 = new ClassicBicycle(10.0);
                Vehicule v3 = new ElectricAssistBicycle(15.0);

                assertEquals(3, v1.getId());
                assertEquals(4, v2.getId());
                assertEquals(5, v3.getId());

                assertNotEquals(v1.getId(), v2.getId(),
                                "Chaque véhicule devrait avoir un ID unique");
                assertNotEquals(v2.getId(), v3.getId(),
                                "Les ID devraient etre différents même pour des types différent");
        }

        @Test
        public void testIncrementLocationNb() {
                assertEquals(0, classicBike.getLocationNb());

                classicBike.incrementLocationNb();
                assertEquals(1, classicBike.getLocationNb(),
                                "Une incrémentation devrait augmenter de 1");

                classicBike.incrementLocationNb();
                classicBike.incrementLocationNb();
                assertEquals(3, classicBike.getLocationNb(),
                                "Trois incrémentations devraient donner 3");
        }

        @Test
        public void testMultipleLocationIncrements() {
                for (int i = 1; i <= 10; i++) {
                        classicBike.incrementLocationNb();
                        assertEquals(i, classicBike.getLocationNb(),
                                        "Après " + i + " incrémentations, le compteur devrait etre a " + i);
                }
        }

        @Test
        public void testSetState() {
                assertTrue(classicBike.getVehiculeState() instanceof ParkedState);

                classicBike.setState(new InUseState(classicBike));
                assertTrue(classicBike.getVehiculeState() instanceof InUseState,
                                "L'état devrait pouvoir être changé vers InUse");

                classicBike.setState(new UnderRepairState(classicBike));
                assertTrue(classicBike.getVehiculeState() instanceof UnderRepairState,
                                "L'état devrait pouvoir être changé vers UnderRepair");

                classicBike.setState(new StolenState(classicBike));
                assertTrue(classicBike.getVehiculeState() instanceof StolenState,
                                "L'état devrait pouvoir être changé vers Stolen");
        }

        @Test
        public void testStateTransitionPreservesVehicleReference() {
                Vehicule original = classicBike;

                classicBike.setState(new InUseState(classicBike));
                assertSame(original, classicBike,
                                "La référence au véhicule devrait rester la même");

                classicBike.setState(new ParkedState(classicBike));
                assertSame(original, classicBike,
                                "La référence devrait être préservée après plusieurs transitions");
        }

        @Test
        public void testRepairIntervalsRemaining() {
                assertEquals(0, classicBike.getRepairIntervalsRemaining(),
                                "Initialement les intervalles devraient etre a 0");

                classicBike.setRepairIntervalsRemaining(3);
                assertEquals(3, classicBike.getRepairIntervalsRemaining(),
                                "Devrait pouvoir définir les intervalles a 3");

                classicBike.setRepairIntervalsRemaining(0);
                assertEquals(0, classicBike.getRepairIntervalsRemaining(),
                                "Devrait pouvoir réinitialiser a 0");
        }

}