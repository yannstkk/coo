package strategy;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import control.Station;
import control.strategy.RandomDistribution;
import exceptions.CannotParkException;
import vehicle.ClassicBicycle;
import vehicle.Vehicule;
import vehicle.state.InUseState;
import vehicle.state.UnderRepairState;

public class RandomDistributionTest {
    private RandomDistribution randomDistribution;
    private List<Station> stations;

    @BeforeEach
    void setUp() {
        randomDistribution = new RandomDistribution();
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

        randomDistribution.distribute(stations);

        assertEquals(0, station1.getNbOccupiedSlot());
        assertEquals(0, station2.getNbOccupiedSlot());
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

        randomDistribution.distribute(stations);

        assertEquals(0, availableStation.getNbOccupiedSlot());

        assertEquals(2, fullStation.getNbOccupiedSlot());
    }

    @Test
    public void redistributeTestEmptyStationList() throws CannotParkException {
        assertDoesNotThrow(() -> randomDistribution.distribute(stations));
    }

    @Test
    public void redistributeTestSingleFullStation() throws CannotParkException {
        Station station = new Station(1, 1);
        station.parkVehicule(new ClassicBicycle(100));

        stations.add(station);

        randomDistribution.distribute(stations);

        assertEquals(1, station.getNbOccupiedSlot());
    }

    @Test
    public void redistributeTestMixedVehicleStates() throws CannotParkException {
        Station fullStation = new Station(1, 4);
        Station availableStation = new Station(2, 10);

        Vehicule parked1 = new ClassicBicycle(100);
        Vehicule parked2 = new ClassicBicycle(101);
        Vehicule inUse = new ClassicBicycle(102);
        Vehicule underRepair = new ClassicBicycle(103);

        fullStation.parkVehicule(parked1);
        fullStation.parkVehicule(parked2);
        fullStation.parkVehicule(inUse);
        fullStation.parkVehicule(underRepair);

        inUse.setState(new InUseState(inUse));
        underRepair.setState(new UnderRepairState(underRepair));

        stations.add(fullStation);
        stations.add(availableStation);

        randomDistribution.distribute(stations);

        assertEquals(2, fullStation.getNbOccupiedSlot());
        assertEquals(2, availableStation.getNbOccupiedSlot());
    }

}