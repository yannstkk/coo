// yani/RandomDistribution.java
package yani;

import Mehdi.*;
import java.util.*;
import java.util.stream.Collectors;

public class RandomDistribution implements Distribution {

    private final Random random = new Random();

    @Override
    public void distribute(List<Station> stations) {
        // 1. Récupérer tous les vélos des stations PLEINES
        List<Vehicule> toMove = stations.stream()
            .filter(Station::isFull)
            .flatMap(s -> {
                List<Vehicule> taken = new ArrayList<>();
                Vehicule v;
                while ((v = s.removeVehiculeForRedistribution()) != null) {
                    if (v.getVehiculeState() == State.PARKED) {
                        taken.add(v);
                    }
                }
                return taken.stream();
            })
            .collect(Collectors.toList());

        if (toMove.isEmpty()) return;

        // 2. Stations cibles = celles qui ne sont PAS pleines
        List<Station> targets = stations.stream()
            .filter(s -> !s.isFull())
            .sorted(Comparator.comparingInt(Station::getNbOccupiedSlot)) // vides en premier
            .collect(Collectors.toList());

        if (targets.isEmpty()) {
            System.out.println("RandomDistribution : impossible, toutes les stations sont pleines.");
            return;
        }

        // 3. Redistribution aléatoire mais intelligente
        for (Vehicule v : toMove) {
            Station target = targets.get(random.nextInt(targets.size()));
            target.parkVehicule(v);
        }

        System.out.println("\u001B[33m[Random] " + toMove.size() + " vélo(s) redistribué(s)\u001B[0m");
    }
}