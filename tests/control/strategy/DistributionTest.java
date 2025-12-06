package strategy;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import control.Station;
import exceptions.*;
import control.strategy.Distribution;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class DistributionTest {

    protected Distribution algorithm;
    protected Station s1, s2, s3;
    ArrayList<Station> stations = new ArrayList<>();

    @BeforeAll
    public void init() throws CannotParkException {
        algorithm = createDistribution();
        s1 = new Station(1, 10);

        s2 = new Station(2, 10);

        s3 = new Station(3, 13);

        stations.add(s1);
        stations.add(s2);
        stations.add(s3);
    }

    public abstract Distribution createDistribution();
}
