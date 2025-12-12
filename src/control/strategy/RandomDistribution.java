package control.strategy;

import java.util.*;
import java.util.stream.Collectors;

import control.Station;
import vehicle.Vehicule;
import vehicle.state.ParkedState;

public class RandomDistribution implements Distribution {

    private final Random random = new Random();

    @Override
    public void distribute(List<Station> stations) {
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
            System.out.println("\u001B[31m[Random] Impossible de redistribuer : aucune station source disponible\u001B[0m");
            return;
        }

        List<Vehicule> toMove = new ArrayList<>();
        int totalNeeded = emptyStations.size() * 3; 

        for (Station source : sourceStations) {
            if (toMove.size() >= totalNeeded) break;

            int toTake = Math.min(3, source.getNbOccupiedSlot() - 2); // Garde au moins 2
            
            for (int i = 0; i < toTake && toMove.size() < totalNeeded; i++) {
                Vehicule v = source.removeVehiculeForRedistribution();
                if (v != null && v.getVehiculeState() instanceof ParkedState) {
                    toMove.add(v);
                }
            }
        }

        if (toMove.isEmpty()) {
            System.out.println("\u001B[31m[Random] Aucun vélo disponible pour redistribution\u001B[0m");
            return;
        }

        for (Vehicule v : toMove) {
            Station target = emptyStations.get(random.nextInt(emptyStations.size()));
            target.parkVehicule(v);
        }

        System.out.println("\u001B[33m[Random] " + toMove.size() + " vélo(s) redistribué(s) vers " + 
                         emptyStations.size() + " station(s) vide(s)\u001B[0m");
    }

    private void redistributeFromFull(List<Station> fullStations, List<Station> allStations) {
        List<Vehicule> toMove = new ArrayList<>();
        
        for (Station full : fullStations) {
            Vehicule v;
            while ((v = full.removeVehiculeForRedistribution()) != null) {
                if (v.getVehiculeState() instanceof ParkedState) {
                    toMove.add(v);
                }
            }
        }

        if (toMove.isEmpty()) return;

        List<Station> targets = allStations.stream()
                .filter(s -> !s.isFull())
                .sorted(Comparator.comparingInt(Station::getNbOccupiedSlot))
                .collect(Collectors.toList());

        for (Vehicule v : toMove) {
            if (targets.isEmpty()) break;
            Station target = targets.get(random.nextInt(targets.size()));
            target.parkVehicule(v);
        }

        System.out.println("\u001B[33m[Random] " + toMove.size() + " vélo(s) redistribué(s)\u001B[0m");
    }
}