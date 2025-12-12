package control;

import java.util.List;

import control.observer.Observer;
import control.strategy.Distribution;

public class ControlCenter implements Observer {

    private List<Station> stations;
    private Distribution distributionStrategy;
    private Colors colors = new Colors();

    public ControlCenter(List<Station> stations, Distribution distributionStrategy) {
        this.stations = stations;
        this.distributionStrategy = distributionStrategy;

        for (Station s : stations) {
            s.attach(this);
        }
    }

    @Override
    public void update(Station station, String action) {
        // Désactiver les notifications répétitives
        // Le système fonctionne en silence
    }

    public Distribution getDistributionStrategy() {
        return distributionStrategy;
    }
}