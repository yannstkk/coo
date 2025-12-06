package strategy;

import org.junit.jupiter.api.Test;

import control.strategy.Distribution;
import control.strategy.RoundRobin;
import exceptions.CannotParkException;
import vehicle.ClassicBicycle;
import vehicle.ElectricAssistBicycle;
import vehicle.accessory.LuggageRack;

import static org.junit.jupiter.api.Assertions.*;

public class RoundRobinTest extends DistributionTest {

    @Override
    public Distribution createDistribution() {
        return new RoundRobin();
    }

    @Test
    public void distributeTestWhenOk() throws CannotParkException {

        s1.parkVehicule(new ClassicBicycle(14));
        s1.parkVehicule(new ElectricAssistBicycle(13));
        s1.parkVehicule(new ClassicBicycle(4));
        s1.parkVehicule(new LuggageRack(new ClassicBicycle(10)));

        s2.parkVehicule(new ElectricAssistBicycle(13));
        s2.parkVehicule(new ClassicBicycle(9));

        // 4 vehicules are parked in Station #1
        // 2 vehicules are parked in station #2
        // 0 vehicules are parked in station #3
        algorithm.distribute(stations);
        assertEquals(s1.getOccupiedCount(), 2);
        assertEquals(s2.getOccupiedCount(), 2);
        assertEquals(s3.getOccupiedCount(), 2);
    }
}