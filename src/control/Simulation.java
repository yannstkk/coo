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
    
    // Constantes pour le forçage d'événements - Cycle de 15
    private static final int CYCLE_PERIOD = 15;

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

                s.parkVehicule(v); // Version sans message pour l'initialisation
            }
        }

        for (int i = 0; i < 10; i++) {
            User u = new User("Nom num " + i, "prenom num " + i, 100.0);
            users.add(u);
        }

        controlCenter = new ControlCenter(stations, distributionStrategy);
        
        // Affichage initial élégant
        printHeader("INITIALISATION DU SYSTEME");
        System.out.println();
        for (Station s : stations) {
            System.out.println(colors.getBlue() + "  Station " + s.getId() + " : " + 
                s.getNbOccupiedSlot() + "/" + s.getCapacity() + " vélos" + colors.getReset());
        }
        System.out.println("\n  " + colors.getGreen() + users.size() + " utilisateurs enregistrés" + colors.getReset());
        System.out.println();
    }

    public void runSimulation() {

        int cycle = 1;
        while (true) {

            printCycleHeader(cycle);

            int numActions = random.nextInt(5) + 1;
            System.out.println("  " + colors.getGreen() + numActions + " action(s) générée(s)" + colors.getReset());
            System.out.println();

            Set<User> alreadyActed = new HashSet<>();
            List<String> actions = new ArrayList<>();

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
                        String action = u.rent(s);
                        if (action != null) actions.add(action);
                    } catch (IllegalStateException | CannotParkException e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        String action = u.park(s);
                        if (action != null) actions.add(action);
                    } catch (CannotParkException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Affichage des actions
            for (String action : actions) {
                System.out.println("  " + action);
            }
            
            if (!actions.isEmpty()) {
                System.out.println();
            }

            // Vérifications AVANT les forçages pour que les compteurs naturels fonctionnent
            List<String> theftMessages = new ArrayList<>();
            for (Station s : stations) {
                String msg = s.verifyStolen();
                if (msg != null) theftMessages.add(msg);
            }

            // Forçages d'événements
            // Pattern : Cycle 6, 21, 36... (vol) | Cycle 13, 28, 43... (réparation) | Cycle 20, 35, 50... (redistribution)
            if ((cycle - 6) % 15 == 0 && cycle >= 6) {
                forceTheft(cycle);
            }
            
            if ((cycle - 13) % 15 == 0 && cycle >= 13) {
                forceRepair(cycle);
            }
            
            if ((cycle - 20) % 15 == 0 && cycle >= 20) {
                forceRedistribution(cycle);
            }

            // Affichage des vols (naturels ou forcés)
            for (String msg : theftMessages) {
                System.out.println("  " + msg);
            }
            if (!theftMessages.isEmpty()) System.out.println();

            for (Station s : stations) {
                s.incrementEmptyFullCounters();
            }

            controlCenter.checkAndRedistribute();

            // Réparations
            List<String> repairMessages = new ArrayList<>();

            for (Station s : stations) {
                for (Slot slot : s.getSlotList()) {
                    if (slot.getIsOccupied()) {
                        Vehicule v = slot.getActualVehicule();
                        if (v.getLocationNb() >= 6 && v.getVehiculeState() instanceof ParkedState) {
                            repairMessages.add(colors.getOrange() + "Vélo #" + v.getId() + 
                                " (Station " + s.getId() + ") nécessite une réparation" + colors.getReset());
                            v.setState(new UnderRepairState(v));
                            v.setRepairIntervalsRemaining(2);
                        } 
                        else if (v.getVehiculeState() instanceof UnderRepairState) {
                            v.accept(technician);
                            
                            if (v.getRepairIntervalsRemaining() == 0) {
                                repairMessages.add(colors.getGreen() + "Vélo #" + v.getId() + 
                                    " (Station " + s.getId() + ") réparé avec succès" + colors.getReset());
                            }
                        }
                    }
                }
            }

            for (String msg : repairMessages) {
                System.out.println("  " + msg);
            }
            if (!repairMessages.isEmpty()) System.out.println();

            // État des stations
            printStationsStatus();
            
            printCycleFooter(cycle);

            cycle++;

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void forceTheft(int cycle) {
        System.out.println("  " + colors.getPurple() + "[SCENARIO FORCE] Simulation d'un vol" + colors.getReset());
        
        Station targetStation = null;
        for (Station s : stations) {
            if (s.getNbOccupiedSlot() >= 2) {
                targetStation = s;
                break;
            }
        }
        
        if (targetStation == null) {
            System.out.println("  " + colors.getRed() + "Impossible d'isoler un vélo" + colors.getReset());
            System.out.println();
            return;
        }
        
        List<Vehicule> removed = new ArrayList<>();
        while (targetStation.getNbOccupiedSlot() > 1) {
            Vehicule v = targetStation.removeVehiculeForRedistribution();
            if (v != null) {
                removed.add(v);
            }
        }
        
        for (Vehicule v : removed) {
            for (Station s : stations) {
                if (s.getId() != targetStation.getId() && !s.isFull()) {
                    s.parkVehicule(v);
                    break;
                }
            }
        }
        
        System.out.println("  " + colors.getPurple() + "Station " + targetStation.getId() + 
            " : isolation d'un vélo (" + removed.size() + " vélos déplacés)" + colors.getReset());
        System.out.println();
    }

    private void forceRepair(int cycle) {
        System.out.println("  " + colors.getCyan() + "[SCENARIO FORCE] Simulation d'une réparation" + colors.getReset());
        
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
            System.out.println("  " + colors.getRed() + "Aucun vélo disponible" + colors.getReset());
            System.out.println();
            return;
        }
        
        int oldLocationNb = targetVehicule.getLocationNb();
        targetVehicule.locationNb = 6;
        
        System.out.println("  " + colors.getCyan() + "Vélo #" + targetVehicule.getId() + 
            " (Station " + targetStation.getId() + ") : usure forcée (" + 
            oldLocationNb + " -> 6 locations)" + colors.getReset());
        System.out.println();
    }

    private void forceRedistribution(int cycle) {
        System.out.println("  " + colors.getOrange() + "[SCENARIO FORCE] Simulation d'une redistribution" + colors.getReset());
        
        int redistributionNumber = (cycle - 1) / CYCLE_PERIOD;
        boolean shouldEmpty = redistributionNumber % 2 == 0;
        
        if (shouldEmpty) {
            Station targetStation = null;
            for (Station s : stations) {
                if (s.getNbOccupiedSlot() > 0) {
                    targetStation = s;
                    break;
                }
            }
            
            if (targetStation == null) {
                System.out.println("  " + colors.getRed() + "Toutes les stations sont vides" + colors.getReset());
                System.out.println();
                return;
            }
            
            List<Vehicule> removed = new ArrayList<>();
            while (targetStation.getNbOccupiedSlot() > 0) {
                Vehicule v = targetStation.removeVehiculeForRedistribution();
                if (v != null) {
                    removed.add(v);
                }
            }
            
            for (Vehicule v : removed) {
                for (Station s : stations) {
                    if (s.getId() != targetStation.getId() && !s.isFull()) {
                        s.parkVehicule(v);
                        break;
                    }
                }
            }
            
            System.out.println("  " + colors.getOrange() + "Station " + targetStation.getId() + 
                " vidée complètement (" + removed.size() + " vélos redistribués)" + colors.getReset());
            
        } else {
            Station targetStation = null;
            for (Station s : stations) {
                if (!s.isFull() && s.getNbOccupiedSlot() < s.getCapacity() - 3) {
                    targetStation = s;
                    break;
                }
            }
            
            if (targetStation == null) {
                System.out.println("  " + colors.getRed() + "Impossible de remplir une station" + colors.getReset());
                System.out.println();
                return;
            }
            
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
            
            for (Vehicule v : toMove) {
                targetStation.parkVehicule(v);
            }
            
            System.out.println("  " + colors.getOrange() + "Station " + targetStation.getId() + 
                " remplie au maximum (" + toMove.size() + " vélos ajoutés)" + colors.getReset());
        }
        
        System.out.println();
    }

    private void printHeader(String title) {
        System.out.println("\n" + colors.getYellow() + "╔════════════════════════════════════════════════════════════╗" + colors.getReset());
        System.out.println(colors.getYellow() + "║  " + String.format("%-56s", title) + "  ║" + colors.getReset());
        System.out.println(colors.getYellow() + "╚════════════════════════════════════════════════════════════╝" + colors.getReset());
    }

    private void printCycleHeader(int cycle) {
        System.out.println("\n" + colors.getYellow() + "┌────────────────────────────────────────────────────────────┐" + colors.getReset());
        System.out.println(colors.getYellow() + "│  CYCLE " + String.format("%-49s", cycle) + "  │" + colors.getReset());
        System.out.println(colors.getYellow() + "└────────────────────────────────────────────────────────────┘" + colors.getReset());
        System.out.println();
    }

    private void printCycleFooter(int cycle) {
        System.out.println(colors.getBlue() + "  ────────────────────────────────────────────────────────────" + colors.getReset());
    }

    private void printStationsStatus() {
        System.out.println("  " + colors.getBlue() + "État du réseau :" + colors.getReset());
        for (Station s : stations) {
            int occupied = s.getNbOccupiedSlot();
            int capacity = s.getCapacity();
            double percentage = (double) occupied / capacity * 100;
            
            String statusColor;
            String status;
            if (occupied == 0) {
                statusColor = colors.getRed();
                status = "VIDE";
            } else if (occupied == capacity) {
                statusColor = colors.getRed();
                status = "PLEINE";
            } else if (percentage < 30) {
                statusColor = colors.getOrange();
                status = "FAIBLE";
            } else if (percentage > 70) {
                statusColor = colors.getOrange();
                status = "ÉLEVÉ";
            } else {
                statusColor = colors.getGreen();
                status = "NORMAL";
            }
            
            String bar = generateBar(occupied, capacity);
            
            System.out.println("    Station " + s.getId() + " : " + bar + " " + 
                occupied + "/" + capacity + " " + statusColor + "[" + status + "]" + colors.getReset());
        }
        System.out.println();
    }

    private String generateBar(int current, int max) {
        int barLength = 20;
        int filled = (int) ((double) current / max * barLength);
        
        StringBuilder bar = new StringBuilder(colors.getBlue() + "[" + colors.getReset());
        for (int i = 0; i < barLength; i++) {
            if (i < filled) {
                bar.append(colors.getGreen()).append("=").append(colors.getReset());
            } else {
                bar.append(" ");
            }
        }
        bar.append(colors.getBlue() + "]" + colors.getReset());
        
        return bar.toString();
    }
}