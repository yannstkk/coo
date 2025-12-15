package control;

import java.util.List;

import control.observer.Observer;
import control.strategy.Distribution;
import exceptions.CannotParkException;

public class ControlCenter implements Observer {

    private List<Station> stations;
    private Distribution distributionStrategy;

    /**
     * Creates a control center that manages stations and attaches itself as an
     * observer
     * 
     * @param stations             the list of stations to control
     * @param distributionStrategy the distribution strategy to use
     */
    public ControlCenter(List<Station> stations, Distribution distributionStrategy) {
        this.stations = stations;
        this.distributionStrategy = distributionStrategy;

        for (Station s : stations) {
            s.attach(this);
        }
    }

    /**
     * Receives updates from observed stations (currently disabled to reduce output
     * noise)
     * 
     * @param station the station that triggered the update
     * @param action  the action that occurred
     */
    @Override
    public void update(Station station, String action) {
        // on a désactiver les notifications répétitives car ca fait trop de bruit
        // Le système fonctionne en silence
    }

    /**
     * Gets the distribution strategy used by the control center
     * 
     * @return the distribution strategy
     */
    public Distribution getDistributionStrategy() {
        return distributionStrategy;
    }
}