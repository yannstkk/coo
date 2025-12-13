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
    private Colors colors = new Colors();
    
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

                s.parkVehicule(v);
            }
        }

        for (int i = 0; i < 10; i++) {
            User u = new User("Nom num " + i, "prenom num " + i, 100.0);
            users.add(u);
        }

        controlCenter = new ControlCenter(stations, distributionStrategy);
        
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
            // System.out.println("  " + colors.getGreen() + numActions + " action(s) générée(s)" + colors.getReset());
            // System.out.println();

            Set<User> alreadyActed = new HashSet<>();
            Set<Integer> alreadyUsedVehicleIds = new HashSet<>();  // CHANGEMENT: Set d'IDs
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
                        String action = u.rent(s, alreadyUsedVehicleIds);  // Passer Set<Integer>
                        if (action != null) {
                            actions.add(action);
                            if (u.getRentedVehicule() != null) {
                                alreadyUsedVehicleIds.add(u.getRentedVehicule().getId());
                            }
                        }
                    } catch (IllegalStateException | CannotParkException e) {
                        // Action échouée, continuer
                    }

                } else {
                    try {
                        int vehiculeIdBeingParked = u.getRentedVehicule().getId();
                        String action = u.park(s);
                        if (action != null) {
                            actions.add(action);
                            alreadyUsedVehicleIds.add(vehiculeIdBeingParked);
                        }
                    } catch (CannotParkException e) {
                        // Action échouée, continuer
                    }
                }
            }

            if (!actions.isEmpty()) {
                System.out.println("  " + colors.getGreen() + actions.size() + " action(s) effectuée(s)" + colors.getReset());
                System.out.println();
                for (String action : actions) {
                    System.out.println("  " + action);
                }
                System.out.println();
            } else {
                System.out.println("  " + colors.getYellow() + "Aucune action n'a pu être effectuée" + colors.getReset());
                System.out.println();
            }

            // Forçages d'événements
            try {
                // Vol : cycle 6, puis tous les 21 cycles
                if (cycle >= 6 && (cycle - 6) % 21 == 0) {
                    forceTheft(cycle);
                }
                
                // Réparation : cycle 13, puis tous les 21 cycles
                if (cycle >= 13 && (cycle - 13) % 21 == 0) {
                    forceRepair(cycle);
                }
                
                // Redistribution : cycle 20, puis tous les 21 cycles
                if (cycle >= 20 && (cycle - 20) % 21 == 0) {
                    forceRedistribution(cycle);
                }
            } catch (Exception e) {
                System.err.println("  " + colors.getRed() + "Erreur lors d'un scénario forcé : " + 
                    e.getMessage() + colors.getReset());
                e.printStackTrace();
            }

            // Vérifications naturelles de vol
            List<String> theftMessages = new ArrayList<>();
            for (Station st : stations) {
                String msg = st.verifyStolen();
                if (msg != null) theftMessages.add(msg);
            }

            for (String msg : theftMessages) {
                System.out.println("  " + msg);
            }
            if (!theftMessages.isEmpty()) System.out.println();

            printStationsStatus();

            for (Station st : stations) {
                st.incrementEmptyFullCounters();
            }

            boolean redistributionOccurred = checkAndRedistribute();
            
            if (redistributionOccurred) {
                System.out.println("  " + colors.getBlue() + "État après redistribution :" + colors.getReset());
                printStationsStatusCompact();
            }

            // Réparations
            List<String> repairMessages = new ArrayList<>();

            for (Station st : stations) {
                for (Slot slot : st.getSlotList()) {
                    if (slot.getIsOccupied()) {
                        Vehicule v = slot.getActualVehicule();
                        if (v.getLocationNb() >= 6 && v.getVehiculeState() instanceof ParkedState) {
                            repairMessages.add(colors.getOrange() + "Vélo #" + v.getId() + 
                                " (Station " + st.getId() + ") nécessite une réparation" + colors.getReset());
                            v.setState(new UnderRepairState(v));
                            v.setRepairIntervalsRemaining(2);
                        } 
                        else if (v.getVehiculeState() instanceof UnderRepairState) {
                            v.accept(technician);
                            
                            if (v.getRepairIntervalsRemaining() == 0) {
                                repairMessages.add(colors.getGreen() + "Vélo #" + v.getId() + 
                                    " (Station " + st.getId() + ") réparé avec succès" + colors.getReset());
                            }
                        }
                    }
                }
            }

            for (String msg : repairMessages) {
                System.out.println("  " + msg);
            }
            if (!repairMessages.isEmpty()) System.out.println();

            printCycleFooter(cycle);

            cycle++;

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkAndRedistribute() {
        List<Station> stationsToRedistribute = new java.util.ArrayList<>();

        for (Station s : stations) {
            if (s.needsRedistribution()) {
                stationsToRedistribute.add(s);
            }
        }

        if (!stationsToRedistribute.isEmpty()) {
            List<Integer> stationIds = stationsToRedistribute.stream()
                .map(s -> s.getId())
                .toList();
            
            System.out.println("  " + colors.getOrange() + "Redistribution automatique : Stations " + 
                stationIds + colors.getReset());
            
            controlCenter.getDistributionStrategy().distribute(stations);
            System.out.println();
            return true;
        }
        return false;
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
            System.out.println("  " + colors.getRed() + "Impossible d'isoler un vélo (pas assez de vélos)" + colors.getReset());
            System.out.println();
            return;
        }
        
        List<VehiculeTransfer> transfers = new ArrayList<>();
        List<Vehicule> toMove = new ArrayList<>();
        
        // Collecter tous les vélos sauf un
        while (targetStation.getNbOccupiedSlot() > 1) {
            Vehicule v = targetStation.removeVehiculeForRedistribution();
            if (v != null) {
                toMove.add(v);
            } else {
                break;
            }
        }
        
        // Déplacer les vélos collectés
        for (Vehicule v : toMove) {
            boolean placed = false;
            for (Station s : stations) {
                if (s.getId() != targetStation.getId() && !s.isFull()) {
                    s.parkVehicule(v);
                    transfers.add(new VehiculeTransfer(v.getId(), targetStation.getId(), s.getId()));
                    placed = true;
                    break;
                }
            }
            if (!placed) {
                // Remettre le vélo si impossible de le placer
                targetStation.parkVehicule(v);
                System.err.println("  " + colors.getRed() + "Attention : Vélo #" + v.getId() + 
                    " n'a pas pu être déplacé" + colors.getReset());
            }
        }
        
        if (transfers.isEmpty()) {
            System.out.println("  " + colors.getRed() + "Impossible d'isoler un vélo (pas assez d'espace)" + colors.getReset());
        } else {
            System.out.println("  " + colors.getPurple() + "Station " + targetStation.getId() + 
                " : isolation d'un vélo (" + transfers.size() + " vélos déplacés)" + colors.getReset());
            
            for (VehiculeTransfer t : transfers) {
                System.out.println("    → Vélo #" + t.vehiculeId + " : Station " + 
                    t.fromStation + " → Station " + t.toStation);
            }
        }
        
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
            System.out.println("  " + colors.getRed() + "Aucun vélo disponible pour réparation" + colors.getReset());
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
    
    int redistributionNumber = (cycle - 20) / 21;
    boolean shouldEmpty = redistributionNumber % 2 == 0;
    
    if (shouldEmpty) {
        // Vider complètement une station
        Station targetStation = null;
        for (Station s : stations) {
            if (s.getNbOccupiedSlot() > 0) {
                targetStation = s;
                break;
            }
        }
        
        if (targetStation == null) {
            System.out.println("  " + colors.getRed() + "Toutes les stations sont déjà vides" + colors.getReset());
            System.out.println();
            return;
        }
        
        List<Vehicule> removed = new ArrayList<>();
        while (targetStation.getNbOccupiedSlot() > 0) {
            Vehicule v = targetStation.removeVehiculeForRedistribution();
            if (v != null) {
                removed.add(v);
            } else {
                break;
            }
        }
        
        for (Vehicule v : removed) {
            boolean placed = false;
            for (Station s : stations) {
                if (s.getId() != targetStation.getId() && !s.isFull()) {
                    s.parkVehicule(v);
                    placed = true;
                    break;
                }
            }
            if (!placed) {
                targetStation.parkVehicule(v);
            }
        }
        
        System.out.println("  " + colors.getOrange() + "Station " + targetStation.getId() + 
            " vidée (" + removed.size() + " vélos redistribués)" + colors.getReset());
        
    } else {
        // Remplir COMPLÈTEMENT une station (à 100% de sa capacité)
        Station targetStation = null;
        for (Station s : stations) {
            if (!s.isFull() && s.getNbOccupiedSlot() < s.getCapacity() - 3) {
                targetStation = s;
                break;
            }
        }
        
        if (targetStation == null) {
            System.out.println("  " + colors.getRed() + "Aucune station ne peut être remplie" + colors.getReset());
            System.out.println();
            return;
        }
        
        // Calculer combien de vélos sont nécessaires pour remplir COMPLÈTEMENT
        int needed = targetStation.getCapacity() - targetStation.getNbOccupiedSlot();
        List<Vehicule> toMove = new ArrayList<>();
        
        // PHASE 1 : Collecter depuis les autres stations (en gardant au moins 2 vélos par station)
        for (Station s : stations) {
            if (s.getId() != targetStation.getId() && toMove.size() < needed) {
                // Prendre des vélos tout en gardant au moins 2 vélos dans la station source
                while (s.getNbOccupiedSlot() > 2 && toMove.size() < needed) {
                    Vehicule v = s.removeVehiculeForRedistribution();
                    if (v != null) {
                        toMove.add(v);
                    } else {
                        break;
                    }
                }
            }
        }
        
        // PHASE 2 : Si pas assez de vélos, prendre TOUS les vélos des autres stations
        if (toMove.size() < needed) {
            for (Station s : stations) {
                if (s.getId() != targetStation.getId() && toMove.size() < needed) {
                    // Cette fois, prendre TOUS les vélos restants
                    while (s.getNbOccupiedSlot() > 0 && toMove.size() < needed) {
                        Vehicule v = s.removeVehiculeForRedistribution();
                        if (v != null) {
                            toMove.add(v);
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        
        // Ajouter tous les vélos collectés à la station cible
        for (Vehicule v : toMove) {
            if (!targetStation.isFull()) {
                targetStation.parkVehicule(v);
            }
        }
        
        // Message selon si on a réussi à remplir complètement ou non
        if (targetStation.isFull()) {
            System.out.println("  " + colors.getOrange() + "Station " + targetStation.getId() + 
                " remplie complètement (" + toMove.size() + " vélos ajoutés)" + colors.getReset());
        } else {
            System.out.println("  " + colors.getOrange() + "Station " + targetStation.getId() + 
                " remplie partiellement (" + toMove.size() + " vélos ajoutés, capacité insuffisante)" + colors.getReset());
        }
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

    private void printStationsStatusCompact() {
        for (Station s : stations) {
            int occupied = s.getNbOccupiedSlot();
            int capacity = s.getCapacity();
            String bar = generateBar(occupied, capacity);
            System.out.println("    Station " + s.getId() + " : " + bar + " " + occupied + "/" + capacity);
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

    private static class VehiculeTransfer {
        int vehiculeId;
        int fromStation;
        int toStation;
        
        VehiculeTransfer(int vehiculeId, int fromStation, int toStation) {
            this.vehiculeId = vehiculeId;
            this.fromStation = fromStation;
            this.toStation = toStation;
        }
    }
}