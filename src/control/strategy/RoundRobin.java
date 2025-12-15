package control.strategy;

import java.util.*;

import control.Colors;
import control.Station;
import exceptions.CannotParkException;
import vehicle.Vehicule;
import vehicle.state.ParkedState;

/**
 * Implements a round-robin distribution strategy for redistributing vehicles
 * across stations.
 * This strategy moves vehicles from stations with excess bikes to empty or less
 * occupied stations
 * in a circular fashion.
 */
public class RoundRobin implements Distribution {

    private Colors colors = new Colors();

    /**
     * Distributes vehicles across stations using a round-robin approach.
     * First attempts to fill empty stations by taking bikes from stations with more
     * than 2 bikes.
     * If no empty stations exist, redistributes from full stations to available
     * ones.
     * 
     * @param stations the list of stations to redistribute vehicles between
     * @throws CannotParkException if vehicles cannot be parked during
     *                             redistribution
     */
    @Override
    public void distribute(List<Station> stations) throws CannotParkException {

        List<Station> emptyStations = new ArrayList<>();
        for (Station s : stations) {
            if (s.isEmpty()) {
                emptyStations.add(s);
            }
        }

        if (emptyStations.isEmpty()) {
            redistributeFromFull(stations);
            return;
        }

        List<Vehicule> velosADeplacer = new ArrayList<>();

        for (Station station : stations) {
            if (!station.isEmpty() && station.getNbOccupiedSlot() > 2) {
                int toTake = Math.min(3, station.getNbOccupiedSlot() - 2);

                for (int i = 0; i < toTake; i++) {
                    Vehicule velo = station.removeVehiculeForRedistribution();
                    if (velo != null && velo.getVehiculeState() instanceof ParkedState) {
                        velosADeplacer.add(velo);
                    }
                }
            }
        }

        if (velosADeplacer.isEmpty()) {
            return;
        }

        for (int i = 0; i < velosADeplacer.size(); i++) {
            int indexStation = i % emptyStations.size();
            Station stationCible = emptyStations.get(indexStation);
            stationCible.parkVehicule(velosADeplacer.get(i));
        }

        System.out.println("  " + colors.getGreen() + velosADeplacer.size() + " vélo(s) redistribué(s) vers " +
                emptyStations.size() + " station(s)" + colors.getReset());
    }

    /**
     * Redistributes vehicles from full stations to available stations.
     * Removes all vehicles from full stations and distributes them in round-robin
     * fashion
     * to stations that are not full, prioritizing stations with fewer occupied
     * slots.
     * 
     * @param stations the list of stations to redistribute from full stations
     * @throws CannotParkException if vehicles cannot be parked during
     *                             redistribution
     */
    private void redistributeFromFull(List<Station> stations) throws CannotParkException {
        List<Vehicule> velosADeplacer = new ArrayList<>();

        for (Station station : stations) {
            if (station.isFull()) {
                Vehicule velo;
                while ((velo = station.removeVehiculeForRedistribution()) != null) {
                    if (velo.getVehiculeState() instanceof ParkedState) {
                        velosADeplacer.add(velo);
                    }
                }
            }
        }

        if (velosADeplacer.isEmpty()) {
            return;
        }

        List<Station> stationsDisponibles = new ArrayList<>();
        for (Station station : stations) {
            if (!station.isFull()) {
                stationsDisponibles.add(station);
            }
        }

        stationsDisponibles.sort((s1, s2) -> Integer.compare(s1.getNbOccupiedSlot(), s2.getNbOccupiedSlot()));

        if (stationsDisponibles.isEmpty()) {
            return;
        }

        for (int i = 0; i < velosADeplacer.size(); i++) {
            int indexStation = i % stationsDisponibles.size();
            Station stationCible = stationsDisponibles.get(indexStation);
            stationCible.parkVehicule(velosADeplacer.get(i));
        }

        System.out.println(
                "  " + colors.getGreen() + velosADeplacer.size() + " vélo(s) redistribué(s)" + colors.getReset());
    }
}