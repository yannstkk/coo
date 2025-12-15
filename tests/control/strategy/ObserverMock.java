package strategy;

import control.Station;
import control.observer.Observer;

public class ObserverMock implements Observer {
    private int nbOcc = 0;

    @Override
    public void update(Station station, String action) {
        nbOcc++;
    }

    public int getNbOcc() {
        return nbOcc;
    }
}
