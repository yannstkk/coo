import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import control.Station;
import exceptions.CannotParkException;
import vehicle.ClassicBicycle;
import vehicle.state.InUseState;
import vehicle.state.ParkedState;
import vehicle.state.StolenState;
import vehicle.state.UnderRepairState;

public class StationTest {
    Station station = new Station(1, 2);

    @Test
    public void parkVehiculeTestWhenOK() throws CannotParkException, IllegalStateException {
        ClassicBicycle v1 = new ClassicBicycle(70);
        v1.getVehiculeState().in_use();
        assertTrue(v1.getVehiculeState() instanceof InUseState);
        assertEquals(v1.getLocationNb(), 1);
        assertTrue(station.isEmpty());
        int occupiedCountBefore = station.getOccupiedCount();
        station.parkVehicule(v1);
        assertTrue(v1.getVehiculeState() instanceof ParkedState);
        assertEquals(station.getOccupiedCount() - occupiedCountBefore, 1);
        assertTrue(station.getSlotList().stream()
                .anyMatch(slot -> v1.equals(slot.getActualVehicule()) && slot.getIsOccupied()));
    }

    @Test
    public void parkVehiculeTestWhenKO() throws CannotParkException, IllegalStateException {
        ClassicBicycle v1 = new ClassicBicycle(10);
        ClassicBicycle v2 = new ClassicBicycle(10);
        v1.getVehiculeState().in_use();
        station.parkVehicule(v1);
        int count = station.getOccupiedCount();

        v2.getVehiculeState().stolen();
        assertTrue(v2.getVehiculeState() instanceof StolenState);
        assertThrows(CannotParkException.class, () -> {
            station.parkVehicule(v2);
        });
        assertEquals(count, station.getOccupiedCount());

        ClassicBicycle v3 = new ClassicBicycle(15);
        v3.setState(new UnderRepairState(v3));
        // hard coded because we cannot pass from the parkedState toUnderRepairState
        assertTrue(v3.getVehiculeState() instanceof UnderRepairState);
        assertThrows(CannotParkException.class, () -> {
            station.parkVehicule(v3);
        });
        assertEquals(count, station.getOccupiedCount());

        ClassicBicycle v4 = new ClassicBicycle(14);
        v4.getVehiculeState().in_use();
        assertTrue(v4.getVehiculeState() instanceof InUseState);
        station.parkVehicule(v4);
        assertEquals(count + 1, station.getOccupiedCount());
        assertTrue(station.isFull());

        ClassicBicycle v5 = new ClassicBicycle(12);
        assertThrows(CannotParkException.class, () -> {
            station.parkVehicule(v5);
        });
        assertTrue(station.isFull());
    }

}