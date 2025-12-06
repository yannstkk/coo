package control.strategy;

import java.util.*;

import control.Colors;
import control.Station;
import vehicle.Vehicule;
import vehicle.state.ParkedState;

public class RoundRobin implements Distribution {

    @Override
    public void distribute(List<Station> stations) {
        Colors colors = new Colors();

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
            System.out.println("RoundRobin : impossible, toutes les stations sont pleines");
            return;
        }

        for (int i = 0; i < velosADeplacer.size(); i++) {
            int indexStation = i % stationsDisponibles.size();
            Station stationCible = stationsDisponibles.get(indexStation);
            stationCible.parkVehicule(velosADeplacer.get(i));
        }

        System.out.println(
                colors.getYellow() + "[RoundRobin] " + velosADeplacer.size() + " vélo redistribué" + colors.getReset());
    }
}