package control.strategy;

import java.util.*;
import java.util.stream.Collectors;

import control.Colors;
import control.Station;
import exceptions.CannotParkException;
import vehicle.Vehicule;
import vehicle.state.ParkedState;

/**
 * Implements a random distribution strategy for redistributing vehicles across
 * stations.
 * This strategy randomly assigns vehicles to target stations, prioritizing
 * empty stations
 * and balancing load across the network.
 */
public class RandomDistribution implements Distribution {

    private final Random random = new Random();
    private Colors colors = new Colors();

    /**
     * Distributes vehicles across stations using random selection.
     * First attempts to fill empty stations by taking bikes from stations with more
     * than 2 bikes.
     * If no empty stations exist, redistributes from full stations to available
     * ones randomly.
     * 
     * @param stations the list of stations to redistribute vehicles between
     * @throws CannotParkException if vehicles cannot be parked during
     *                             redistribution
     */
    @Override
    public void distribute(List<Station> stations) throws CannotParkException {
        List<Station> emptyStations = stations.stream()
                .filter(Station::isEmpty)
                .collect(Collectors.toList());

        if (emptyStations.isEmpty()) {
            List<Station> fullStations = stations.stream()
                    .filter(Station::isFull)
                    .collect(Collectors.toList());

            if (fullStations.isEmpty()) {
                return;
            }

            redistributeFromFull(fullStations, stations);
            return;
        }

        List<Station> sourceStations = stations.stream()
                .filter(s -> !s.isEmpty() && s.getNbOccupiedSlot() > 2)
                .sorted((s1, s2) -> Integer.compare(s2.getNbOccupiedSlot(), s1.getNbOccupiedSlot()))
                .collect(Collectors.toList());

        if (sourceStations.isEmpty()) {
            return;
        }

        List<Vehicule> toMove = new ArrayList<>();
        int totalNeeded = emptyStations.size() * 3;

        for (Station source : sourceStations) {
            if (toMove.size() >= totalNeeded)
                break;

            int toTake = Math.min(3, source.getNbOccupiedSlot() - 2);

            for (int i = 0; i < toTake && toMove.size() < totalNeeded; i++) {
                Vehicule v = source.removeVehiculeForRedistribution();
                if (v != null && v.getVehiculeState() instanceof ParkedState) {
                    toMove.add(v);
                }
            }
        }

        if (toMove.isEmpty()) {
            return;
        }

        for (Vehicule v : toMove) {
            Station target = emptyStations.get(random.nextInt(emptyStations.size()));
            target.parkVehicule(v);
        }

        System.out.println("  " + colors.getGreen() + toMove.size() + " vélo(s) redistribué(s) vers " +
                emptyStations.size() + " station(s)" + colors.getReset());
    }

    /**
     * Redistributes vehicles from full stations to available stations using random
     * selection.
     * Removes all vehicles from full stations and randomly distributes them to
     * stations
     * that are not full, prioritizing stations with fewer occupied slots.
     * 
     * @param fullStations the list of full stations to redistribute from
     * @param allStations  the list of all stations in the network
     * @throws CannotParkException if vehicles cannot be parked during
     *                             redistribution
     */
    private void redistributeFromFull(List<Station> fullStations, List<Station> allStations)
            throws CannotParkException {
        List<Vehicule> toMove = new ArrayList<>();

        for (Station full : fullStations) {
            Vehicule v;
            while ((v = full.removeVehiculeForRedistribution()) != null) {
                if (v.getVehiculeState() instanceof ParkedState) {
                    toMove.add(v);
                }
            }
        }

        if (toMove.isEmpty())
            return;

        List<Station> targets = allStations.stream()
                .filter(s -> !s.isFull())
                .sorted(Comparator.comparingInt(Station::getNbOccupiedSlot))
                .collect(Collectors.toList());

        for (Vehicule v : toMove) {
            if (targets.isEmpty())
                break;
            Station target = targets.get(random.nextInt(targets.size()));
            target.parkVehicule(v);
        }

        System.out.println("  " + colors.getGreen() + toMove.size() + " vélo(s) redistribué(s)" + colors.getReset());
    }
}