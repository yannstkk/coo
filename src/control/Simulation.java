package control;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import control.strategy.Distribution;
import control.strategy.RoundRobin;
import control.strategy.Slot;
import exceptions.CannotParkException;
import intervenant.Technician;
import vehicle.*;
import vehicle.accessory.Basket;
import vehicle.state.ParkedState;
import vehicle.state.UnderRepairState;

public class Simulation {

    private List<Station> stations = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private ControlCenter controlCenter;
    private Random random = new Random();
    private Technician technician = new Technician();
    private int nextVehiculeId = 0;
    private Colors colors = new Colors();
    
    // Constantes pour le forçage d'événements
    private static final int CYCLE_PERIOD = 15;
    private static final int FORCE_THEFT_CYCLE = 6;
    private static final int FORCE_REPAIR_CYCLE = 13;
    private static final int FORCE_REDISTRIBUTION_CYCLE = 5;
    private static final String MAGENTA = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";

    public Simulation() {
        this(new RoundRobin());
    }

    public Simulation(Distribution distributionStrategy) {

        for (int i = 1; i <= 3; i++) {
            stations.add(new Station(i, random.nextInt(11) + 10));
        }

        for (Station s : stations) {
            int nbVehicules = random.nextInt(6) + 5;

            for (int i = 0; i < nbVehicules; i++) {
                double basePrice = 10.0;
                Vehicule v = new ClassicBicycle(basePrice);

                if (random.nextBoolean()) {
                    v = new Basket(v); 
                }

                s.parkVehicule(v);
            }
        }

        for (int i = 0; i < 10; i++) {
            User u = new User("Nom num " + i, "prenom num " + i, 100.0);
            users.add(u);
        }

        controlCenter = new ControlCenter(stations, distributionStrategy);
    }

    public void runSimulation() {

        int cycle = 1;
        while (true) {

            System.out.println(colors.getYellow() + "\n=== Début du cycle " + cycle + " ===" + colors.getReset());
            System.out.println(colors.getYellow() + "----------------------------------------" + colors.getReset());

            int numActions = random.nextInt(5) + 1;
            System.out.println("-- " + numActions + "" + colors.getGreen()
                    + " Actions ont été géneré pour ce cycle actions aléatoires --\n" + colors.getReset());

            Set<User> alreadyActed = new HashSet<>();

            for (int i = 0; i < numActions; i++) {
                List<User> availableUsers = users.stream()
                        .filter(u -> !alreadyActed.contains(u))
                        .collect(Collectors.toList());

                if (availableUsers.isEmpty())
                    break;

                User u = availableUsers.get(random.nextInt(availableUsers.size()));
                alreadyActed.add(u);

                Station s = stations.get(random.nextInt(stations.size()));

                if (u.getRentedVehicule() == null) {
                    try {
                        u.rent(s);
                    } catch (IllegalStateException | CannotParkException e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        u.park(s);
                    } catch (CannotParkException e) {
                        e.printStackTrace();
                    }
                }
            }

            System.out.println("\nVoici l'état des stations après ce cycle : \n");
            for (Station s : stations) {
                System.out.println("Station dont l'id est " + s.getId() + " est Occupé par = " + s.getNbOccupiedSlot()
                        + " velos pour une capacite de : " + s.getCapacity());
            }

            // ========== FORÇAGE D'ÉVÉNEMENTS SELON LE CYCLE ==========
            int cyclePos = cycle % CYCLE_PERIOD;
            
            if (cyclePos == FORCE_THEFT_CYCLE) {
                forceTheft(cycle);
            } else if (cyclePos == FORCE_REPAIR_CYCLE) {
                forceRepair(cycle);
            } else if (cyclePos == FORCE_REDISTRIBUTION_CYCLE) {
                forceRedistribution(cycle);
            }
            // ==========================================================

            // Vérifications naturelles continuent normalement
            for (Station s : stations) {
                s.verifyStolen();
            }

            for (Station s : stations) {
                s.incrementEmptyFullCounters();
            }

            controlCenter.checkAndRedistribute();

            boolean repairTriggered = false;

            for (Station s : stations) {
                for (Slot slot : s.getSlotList()) {
                    if (slot.getIsOccupied()) {
                        Vehicule v = slot.getActualVehicule();
                        if (v.getLocationNb() >= 6 && v.getVehiculeState() instanceof ParkedState) {
                            System.out.println(
                                    "Velo dont l'ID est " + v.getId() + " se trouvant dans la station " + s.getId()
                                            + " : vient d'avoir " + v.getLocationNb() + " locations, il va donc en réparation");
                            v.setState(new UnderRepairState(v));
                            v.setRepairIntervalsRemaining(2);
                            repairTriggered = true;
                        } 
                        else if (v.getVehiculeState() instanceof UnderRepairState) {
                            v.accept(technician);
                            
                            if (v.getRepairIntervalsRemaining() == 0) {
                                System.out.println("Velo dont l'ID est " + v.getId() + 
                                                " se trouvant dans la station " + s.getId() + 
                                                " est Réparé !");
                            }
                            repairTriggered = true;
                        }
                    }
                }
            }

            if (!repairTriggered) {
                System.out.println("Aucune réparation en cours ou déclenchée.");
            }

            System.out.println(colors.getBlue() + "----------------------------------------" + colors.getReset());
            System.out.println("Fin du cycle " + cycle + "\n\n\n\n");

            cycle++;

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Force un vol au cycle spécifié
     * Trouve une station avec plusieurs vélos et en isole un seul
     */
    private void forceTheft(int cycle) {
        System.out.println(MAGENTA + "\n🎬 [FORÇAGE CYCLE " + cycle + "] Déclenchement d'un scénario de vol..." + colors.getReset());
        
        // Trouver une station avec au moins 2 vélos
        Station targetStation = null;
        for (Station s : stations) {
            if (s.getNbOccupiedSlot() >= 2) {
                targetStation = s;
                break;
            }
        }
        
        if (targetStation == null) {
            System.out.println(colors.getRed() + "⚠️ Impossible de forcer un vol : aucune station avec 2+ vélos" + colors.getReset());
            return;
        }
        
        // Retirer tous les vélos sauf un
        List<Vehicule> removed = new ArrayList<>();
        while (targetStation.getNbOccupiedSlot() > 1) {
            Vehicule v = targetStation.removeVehiculeForRedistribution();
            if (v != null) {
                removed.add(v);
            }
        }
        
        // Redistribuer les vélos retirés dans d'autres stations
        for (Vehicule v : removed) {
            for (Station s : stations) {
                if (s.getId() != targetStation.getId() && !s.isFull()) {
                    s.parkVehicule(v);
                    break;
                }
            }
        }
        
        System.out.println(MAGENTA + "🎬 Station " + targetStation.getId() + " : 1 seul vélo isolé (forçage)" + colors.getReset());
        System.out.println(MAGENTA + "🎬 Le vol devrait se produire dans les prochains cycles..." + colors.getReset());
    }

    /**
     * Force une réparation au cycle spécifié
     * Trouve un vélo garé et met son nombre de locations à 6
     */
    private void forceRepair(int cycle) {
        System.out.println(CYAN + "\n🎬 [FORÇAGE CYCLE " + cycle + "] Déclenchement d'un scénario de réparation..." + colors.getReset());
        
        // Trouver un vélo garé
        Vehicule targetVehicule = null;
        Station targetStation = null;
        
        for (Station s : stations) {
            for (Slot slot : s.getSlotList()) {
                if (slot.getIsOccupied() && slot.getActualVehicule().getVehiculeState() instanceof ParkedState) {
                    targetVehicule = slot.getActualVehicule();
                    targetStation = s;
                    break;
                }
            }
            if (targetVehicule != null) break;
        }
        
        if (targetVehicule == null) {
            System.out.println(colors.getRed() + "⚠️ Impossible de forcer une réparation : aucun vélo garé disponible" + colors.getReset());
            return;
        }
        
        // Forcer le nombre de locations à 6
        int oldLocationNb = targetVehicule.getLocationNb();
        targetVehicule.locationNb = 6;
        
        System.out.println(CYAN + "🎬 Vélo ID " + targetVehicule.getId() + " dans la station " + targetStation.getId() + 
                          " : locations forcées de " + oldLocationNb + " → 6 (forçage)" + colors.getReset());
        System.out.println(CYAN + "🎬 La réparation sera déclenchée lors de la prochaine vérification..." + colors.getReset());
    }

    /**
     * Force une redistribution au cycle spécifié
     * Vide complètement une station ou la remplit totalement
     */
    private void forceRedistribution(int cycle) {
        System.out.println(colors.getOrange() + "\n🎬 [FORÇAGE CYCLE " + cycle + "] Déclenchement d'un scénario de redistribution..." + colors.getReset());
        
        // Alterner entre vider et remplir
        boolean shouldEmpty = (cycle / CYCLE_PERIOD) % 2 == 0;
        
        if (shouldEmpty) {
            // Trouver une station avec des vélos et la vider
            Station targetStation = null;
            for (Station s : stations) {
                if (s.getNbOccupiedSlot() > 0) {
                    targetStation = s;
                    break;
                }
            }
            
            if (targetStation == null) {
                System.out.println(colors.getRed() + "⚠️ Impossible de forcer une redistribution : toutes les stations sont vides" + colors.getReset());
                return;
            }
            
            // Vider la station
            List<Vehicule> removed = new ArrayList<>();
            while (targetStation.getNbOccupiedSlot() > 0) {
                Vehicule v = targetStation.removeVehiculeForRedistribution();
                if (v != null) {
                    removed.add(v);
                }
            }
            
            // Redistribuer ailleurs
            for (Vehicule v : removed) {
                for (Station s : stations) {
                    if (s.getId() != targetStation.getId() && !s.isFull()) {
                        s.parkVehicule(v);
                        break;
                    }
                }
            }
            
            System.out.println(colors.getOrange() + "🎬 Station " + targetStation.getId() + 
                              " : complètement vidée (forçage, " + removed.size() + " vélos déplacés)" + colors.getReset());
            
        } else {
            // Trouver une station non pleine et la remplir
            Station targetStation = null;
            for (Station s : stations) {
                if (!s.isFull() && s.getNbOccupiedSlot() < s.getCapacity() - 3) {
                    targetStation = s;
                    break;
                }
            }
            
            if (targetStation == null) {
                System.out.println(colors.getRed() + "⚠️ Impossible de forcer une redistribution : pas de station à remplir" + colors.getReset());
                return;
            }
            
            // Collecter des vélos d'autres stations
            List<Vehicule> toMove = new ArrayList<>();
            int needed = targetStation.getCapacity() - targetStation.getNbOccupiedSlot();
            
            for (Station s : stations) {
                if (s.getId() != targetStation.getId() && toMove.size() < needed) {
                    while (s.getNbOccupiedSlot() > 2 && toMove.size() < needed) {
                        Vehicule v = s.removeVehiculeForRedistribution();
                        if (v != null) {
                            toMove.add(v);
                        }
                    }
                }
            }
            
            // Remplir la station cible
            for (Vehicule v : toMove) {
                targetStation.parkVehicule(v);
            }
            
            System.out.println(colors.getOrange() + "🎬 Station " + targetStation.getId() + 
                              " : remplie au maximum (forçage, " + toMove.size() + " vélos ajoutés)" + colors.getReset());
        }
        
        System.out.println(colors.getOrange() + "🎬 La redistribution sera déclenchée lors de la prochaine vérification..." + colors.getReset());
    }
}