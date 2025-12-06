package control.strategy;

import java.util.List;

import control.Station;

public interface Distribution {
    void distribute(List<Station> stations);
}
