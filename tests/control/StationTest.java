import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import control.Station;
import vehicle.ClassicBicycle;
import vehicle.Vehicule;
import vehicle.state.InUseState;
import vehicle.state.ParkedState;
import vehicle.state.UnderRepairState;
import vehicle.state.StolenState;


public class StationTest {

    private Station station;
    private Vehicule vehicule1;
    private Vehicule vehicule2;
    private Vehicule vehicule3;



    
    @BeforeEach
    public void setUp() {
        Vehicule.reset();
        station = new Station(1, 5);
        vehicule1 = new ClassicBicycle(10.0);
        vehicule2 = new ClassicBicycle(10.0);
        vehicule3 = new ClassicBicycle(10.0);
    }


    @Test
    public void testStationCreation() {
        assertEquals(1, station.getId(), "L'Id de la station devrait etre 1");
        assertEquals(5, station.getCapacity(), "La capacité devrait etre 5");
        assertEquals(0, station.getNbOccupiedSlot(), "La station devrait etre vide initialement");
        assertFalse(station.isFull(), "La station ne devrait pas etre pleine");
        assertTrue(station.isEmpty(), "La station devrait etre vide");
    }

   
    @Test
    public void testObserverAttachment() {
        ObserverMock observer = new ObserverMock();
        station.attach(observer);

        station.parkVehicule(vehicule1);

        assertEquals(1, observer.getUpdateCount(), "L'observateur devrait avoir été notifié une fois");
        assertTrue(observer.getNotifications().get(0).contains("park"),
                "La notification devrait contenir 'park'");
    }

   
    @Test
    public void testObserverNotificationsForDifferentActions() {
        ObserverMock observer = new ObserverMock();
        station.attach(observer);

        station.parkVehicule(vehicule1);
        station.rentVehicule();

        assertEquals(2, observer.getUpdateCount(), "Deux notifications devraient etre envoyées");
        assertTrue(observer.getNotifications().get(0).contains("park"));
        assertTrue(observer.getNotifications().get(1).contains("rent"));
    }


    @Test
    public void testParkVehicule() {
        String result = station.parkVehiculeWithMessage(vehicule1);

        assertNotNull(result, "Le parking devrait réussir");
        assertEquals(1, station.getNbOccupiedSlot(), "Un slot devrait etre occupé");
        assertFalse(station.isEmpty(), "La station ne devrait plus etre vide");
        assertTrue(vehicule1.getVehiculeState() instanceof ParkedState,
                "Le véhicule devrait etre en état Parked");
    }

    @Test
    public void testParkMultipleVehicules() {
        station.parkVehicule(vehicule1);
        station.parkVehicule(vehicule2);
        station.parkVehicule(vehicule3);

        assertEquals(3, station.getNbOccupiedSlot(), "Trois slots devraient etre occupés");
        assertFalse(station.isEmpty());
        assertFalse(station.isFull());
    }

    @Test
    public void testParkVehiculeInFullStation() {
        for (int i = 0; i < 5; i++) {
            station.parkVehicule(new ClassicBicycle(10.0));
        }

        assertTrue(station.isFull(), "La station devrait etre pleine");

        Vehicule extraVehicule = new ClassicBicycle(10.0);
        String result = station.parkVehiculeWithMessage(extraVehicule);

        assertNull(result, "Le parking devrait échouer dans une station pleine");
        assertEquals(5, station.getNbOccupiedSlot(), "Le nbr de véhicule ne devrait pas changer");
    }

    


    @Test
    public void testRentVehicule() {
        station.parkVehicule(vehicule1);
        assertEquals(1, station.getNbOccupiedSlot());

        String result = station.rentVehicule();

        assertNotNull(result, "La location devrait réussir");
        assertEquals(0, station.getNbOccupiedSlot(), "Le slot devrait etre libéré");
        assertTrue(vehicule1.getVehiculeState() instanceof InUseState,
                "Le véhicule devrait être en état InUse");
        assertEquals(1, vehicule1.getLocationNb(), "Le compteur de locations devrait etre incrémenté");
    }

    @Test
    public void testRentFromEmptyStation() {
        String result = station.rentVehicule();

        assertNull(result, "La location devrait échouer dans une station vide");
        assertEquals(0, station.getNbOccupiedSlot());
    }

   

    @Test
    public void testCannotRentVehiculeUnderRepair() {
        station.parkVehicule(vehicule1);
        vehicule1.setState(new UnderRepairState(vehicule1));

        String result = station.rentVehicule();

        assertNull(result, "Un véhicule en réparation ne devrait pas pouvoir être loué");
    }


    @Test
    public void testIsEmptyWhenNoVehicles() {
        assertTrue(station.isEmpty());
        assertFalse(station.isFull());
    }

    @Test
    public void testIsFullWhenAllSlotsOccupied() {
        for (int i = 0; i < 5; i++) {
            station.parkVehicule(new ClassicBicycle(10.0));
        }

        assertTrue(station.isFull());
        assertFalse(station.isEmpty());
    }


   

    @Test
    public void testGetFirstAvailableVehicule() {
        station.parkVehicule(vehicule1);
        station.parkVehicule(vehicule2);

        Vehicule available = station.getFirstAvailableVehicule();

        assertNotNull(available, "Un véhicule disponible devrait être trouvé");
        assertTrue(available.getVehiculeState() instanceof ParkedState);
    }

   


    @Test
    public void testRemoveVehiculeForRedistribution() {
        station.parkVehicule(vehicule1);
        assertEquals(1, station.getNbOccupiedSlot());

        Vehicule removed = station.removeVehiculeForRedistribution();

        assertSame(vehicule1, removed, "Le véhicule retiré devrait être vehicule1");
        assertEquals(0, station.getNbOccupiedSlot(), "La station devrait être vide");
    }



    @Test
    public void testRemoveForRedistributionSkipsNonParked() {
        station.parkVehicule(vehicule1);
        station.parkVehicule(vehicule2);

        vehicule1.setState(new InUseState(vehicule1));

        Vehicule removed = station.removeVehiculeForRedistribution();

        assertSame(vehicule2, removed, "Seul le véhicule garé devrait être retiré");
        assertEquals(1, station.getNbOccupiedSlot());
    }

    


    @Test
    public void testTheftDetectionAfterTwoIntervals() {
        station.parkVehicule(vehicule1);

        String result1 = station.verifyStolen();
        assertNull(result1, "Pas de vol au premier intervalle");
        assertTrue(vehicule1.getVehiculeState() instanceof ParkedState);

        String result2 = station.verifyStolen();
        assertNotNull(result2, "Un vol devrait être détecté au deuxième intervalle");
        assertTrue(result2.contains("VOL"), "Le message devrait indiquer un vol");
        assertTrue(vehicule1.getVehiculeState() instanceof StolenState,
                "Le véhicule devrait être en état Stolen");
        assertEquals(0, station.getNbOccupiedSlot(), "La station devrait être vide");
    }


    @Test
    public void testNoTheftForVehiculeUnderRepair() {
        station.parkVehicule(vehicule1);
        vehicule1.setState(new UnderRepairState(vehicule1));

        station.verifyStolen();
        String result = station.verifyStolen();

        assertNull(result, "Aucun vol pour un véhicule en réparation");
        assertTrue(vehicule1.getVehiculeState() instanceof UnderRepairState);
    }


    @Test
    public void testIncrementEmptyFullCountersWhenEmpty() {
        station.incrementEmptyFullCounters();
        station.incrementEmptyFullCounters();

        assertTrue(station.needsRedistribution(),
                "Une station vide pendant 2 intervalles devrait nécessiter une redistribution");
    }

    @Test
    public void testIncrementEmptyFullCountersWhenFull() {
        for (int i = 0; i < 5; i++) {
            station.parkVehicule(new ClassicBicycle(10.0));
        }

        station.incrementEmptyFullCounters();
        station.incrementEmptyFullCounters();

        assertTrue(station.needsRedistribution(),
                "Une station pleine pendant 2 intervalles devrait nécessiter une redistribution");
    }

    @Test
    public void testCountersResetWhenStateChanges() {
        station.incrementEmptyFullCounters();

        station.parkVehicule(vehicule1);

        station.incrementEmptyFullCounters();

        assertFalse(station.needsRedistribution(),
                "Les compteurs devraient être réinitialisés");
    }

    


    @Test
    public void testMultipleRentalsIncrementLocationCounter() {
        for (int i = 1; i <= 3; i++) {
            station.parkVehicule(vehicule1);
            station.rentVehicule();
            assertEquals(i, vehicule1.getLocationNb(),
                    "Le compteur devrait être à " + i);
        }
    }

    @Test
    public void testGetOccupiedCountMatchesNbOccupiedSlot() {
        station.parkVehicule(vehicule1);
        station.parkVehicule(vehicule2);

        assertEquals(station.getNbOccupiedSlot(), station.getOccupiedCount(),
                "Les deux méthodes de comptage devraient donner le même résultat");
    }


    @Test
    public void testStationBehaviorWithMixedVehicleStates() {
        station.parkVehicule(vehicule1);
        station.parkVehicule(vehicule2);
        station.parkVehicule(vehicule3);

        vehicule2.setState(new UnderRepairState(vehicule2));
        vehicule3.setState(new InUseState(vehicule3));

        assertEquals(3, station.getNbOccupiedSlot(), "Trois slots sont occupés");

        Vehicule available = station.getFirstAvailableVehicule();
        assertSame(vehicule1, available, "Seul vehicule1 devrait être disponible à la location");

        String rentResult = station.rentVehicule();
        assertNotNull(rentResult, "La location devrait réussir");
        assertEquals(2, station.getNbOccupiedSlot(), "Deux slots restent occupés");
    }
}