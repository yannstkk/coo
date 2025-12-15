package strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import control.Station;
import control.strategy.RoundRobin;
import exceptions.CannotParkException;
import vehicle.ClassicBicycle;
import vehicle.Vehicule;
import vehicle.state.InUseState;
import vehicle.state.UnderRepairState;

public class RoundRobinTest {
    private RoundRobin roundRobin;
    private List<Station> stations;

    @BeforeEach
    void setUp() {
        roundRobin = new RoundRobin();
        stations = new ArrayList<>();
    }

    @Test
    public void redistributeTestWithEmptyStations() throws CannotParkException {
        Station station1 = new Station(1, 5);
        Station station2 = new Station(2, 5);

        assertEquals(0, station1.getNbOccupiedSlot());
        assertEquals(0, station2.getNbOccupiedSlot());

        stations.add(station1);
        stations.add(station2);

        roundRobin.distribute(stations);

        assertEquals(0, station1.getNbOccupiedSlot());
        assertEquals(0, station2.getNbOccupiedSlot());
    }

    @Test
    public void redistributeTestWithFullEmptyStations() throws CannotParkException {
        Station fullStation = new Station(1, 3);
        Station availableStation1 = new Station(2, 5);
        Station availableStation2 = new Station(3, 5);

        Vehicule velo1 = new ClassicBicycle(100);
        Vehicule velo2 = new ClassicBicycle(100);
        Vehicule velo3 = new ClassicBicycle(100);

        fullStation.parkVehicule(velo1);
        fullStation.parkVehicule(velo2);
        fullStation.parkVehicule(velo3);

        availableStation1.parkVehicule(new ClassicBicycle(101));
        availableStation1.parkVehicule(new ClassicBicycle(101));

        availableStation2.parkVehicule(new ClassicBicycle(102));

        stations.add(fullStation);
        stations.add(availableStation1);
        stations.add(availableStation2);

        roundRobin.distribute(stations);

        assertEquals(1, fullStation.getNbOccupiedSlot());

        int totalVehicles = 0;
        for (Station s : stations)
            totalVehicles += s.getNbOccupiedSlot();
        assertEquals(6, totalVehicles);
    }

    @Test
    public void redistributeTestWhenSkip() throws CannotParkException {
        Station fullStation = new Station(1, 2);
        Station availableStation = new Station(2, 5);

        Vehicule velo1 = new ClassicBicycle(100);
        Vehicule velo2 = new ClassicBicycle(100);

        fullStation.parkVehicule(velo1);
        fullStation.parkVehicule(velo2);

        stations.add(fullStation);
        stations.add(availableStation);

        velo1.setState(new UnderRepairState(velo1));
        velo2.setState(new InUseState(velo2));

        roundRobin.distribute(stations);

        assertEquals(availableStation.getNbOccupiedSlot(), 0);
    }

    @Test
    public void redistributeTestPriorityCheck() throws CannotParkException {
        Station fullStation = new Station(1, 1);
        Station lessOccupied = new Station(2, 10);
        Station moreOccupied = new Station(3, 10);

        fullStation.parkVehicule(new ClassicBicycle(100));

        lessOccupied.parkVehicule(new ClassicBicycle(100));

        for (int i = 0; i < 5; i++) {
            moreOccupied.parkVehicule(new ClassicBicycle(101));
        }

        stations.add(fullStation);
        stations.add(moreOccupied);
        stations.add(lessOccupied);

        roundRobin.distribute(stations);

        // Vehicle should go to less occupied station
        assertEquals(1, fullStation.getNbOccupiedSlot());
        assertEquals(1, lessOccupied.getNbOccupiedSlot());
        assertEquals(5, moreOccupied.getNbOccupiedSlot());
    }
}