package control.strategy;

import java.util.List;

import control.Station;
import exceptions.CannotParkException;

/**
 * Strategy interface for redistributing vehicles across stations.
 */
public interface Distribution {
    /**
     * Distributes vehicles across stations.
     * 
     * @param stations the list of stations to redistribute vehicles between
     * @throws CannotParkException if vehicles cannot be parked during
     *                             redistribution
     */
    void distribute(List<Station> stations) throws CannotParkException;
}