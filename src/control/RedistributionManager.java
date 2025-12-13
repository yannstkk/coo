package control;

import java.util.List;
import java.util.stream.Collectors;

import control.strategy.Distribution;

/**
 * Responsabilité : Détecter les besoins de redistribution
 * et déclencher les redistributions automatiques
 */
public class RedistributionManager {
    
    private final Distribution distributionStrategy;
    
    public RedistributionManager(Distribution distributionStrategy) {
        this.distributionStrategy = distributionStrategy;
    }
    

    public boolean checkAndRedistribute(List<Station> stations) {
        List<Station> stationsToRedistribute = findStationsNeedingRedistribution(stations);
        
        if (!stationsToRedistribute.isEmpty()) {
            distributionStrategy.distribute(stations);
            return true;
        }
        
        return false;
    }
    
    public List<Station> findStationsNeedingRedistribution(List<Station> stations) {
        return stations.stream()
            .filter(Station::needsRedistribution)
            .collect(Collectors.toList());
    }
    
    public List<Integer> getRedistributionStationIds(List<Station> stations) {
        return findStationsNeedingRedistribution(stations).stream()
            .map(Station::getId)
            .collect(Collectors.toList());
    }
}