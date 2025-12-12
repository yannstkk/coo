package control.strategy;

import java.util.*;

import control.Colors;
import control.Station;
import vehicle.Vehicule;
import vehicle.state.ParkedState;

public class RoundRobin implements Distribution {

    private Colors colors = new Colors();

    @Override
    public void distribute(List<Station> stations) {

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

    private void redistributeFromFull(List<Station> stations) {
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

        stationsDisponibles.sort((s1, s2) -> 
            Integer.compare(s1.getNbOccupiedSlot(), s2.getNbOccupiedSlot()));

        if (stationsDisponibles.isEmpty()) {
            return;
        }

        for (int i = 0; i < velosADeplacer.size(); i++) {
            int indexStation = i % stationsDisponibles.size();
            Station stationCible = stationsDisponibles.get(indexStation);
            stationCible.parkVehicule(velosADeplacer.get(i));
        }

        System.out.println("  " + colors.getGreen() + velosADeplacer.size() + " vélo(s) redistribué(s)" + colors.getReset());
    }
}