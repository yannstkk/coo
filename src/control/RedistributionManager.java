package control;

import java.util.List;
import java.util.stream.Collectors;

import control.strategy.Distribution;
import exceptions.CannotParkException;

/**
 * Responsabilité : Détecter les besoins de redistribution
 * et déclencher les redistributions automatiques
 */
public class RedistributionManager {

    private final Distribution distributionStrategy;

    /**
     * Creates a redistribution manager with the specified strategy
     * 
     * @param distributionStrategy the distribution strategy to use
     */
    public RedistributionManager(Distribution distributionStrategy) {
        this.distributionStrategy = distributionStrategy;
    }

    /**
     * Checks if any stations need redistribution and performs it if necessary
     * 
     * @param stations the list of stations to check and redistribute
     * @return true if redistribution was performed, false otherwise
     * @throws CannotParkException if vehicles cannot be parked during
     *                             redistribution
     */
    public boolean checkAndRedistribute(List<Station> stations) throws CannotParkException {
        List<Station> stationsToRedistribute = findStationsNeedingRedistribution(stations);

        if (!stationsToRedistribute.isEmpty()) {
            distributionStrategy.distribute(stations);
            return true;
        }

        return false;
    }

    /**
     * Finds all stations that need redistribution based on their interval counters
     * 
     * @param stations the list of stations to check
     * @return list of stations needing redistribution
     */
    public List<Station> findStationsNeedingRedistribution(List<Station> stations) {
        return stations.stream()
                .filter(Station::needsRedistribution)
                .collect(Collectors.toList());
    }

    /**
     * Gets the IDs of all stations needing redistribution
     * 
     * @param stations the list of stations to check
     * @return list of station IDs needing redistribution
     */
    public List<Integer> getRedistributionStationIds(List<Station> stations) {
        return findStationsNeedingRedistribution(stations).stream()
                .map(Station::getId)
                .collect(Collectors.toList());
    }
}