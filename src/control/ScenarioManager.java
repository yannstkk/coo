package control;

import java.util.ArrayList;
import java.util.List;

import control.strategy.Slot;
import vehicle.Vehicule;
import vehicle.state.ParkedState;

/**
 * Responsabilité : Gérer les scénarios forcés
 * (vols, réparations forcées, redistributions forcées)
 */
public class ScenarioManager {
    
    private final Colors colors = new Colors();
    

    public void forceTheft(List<Station> stations, int cycle) {
        System.out.println("  " + colors.getPurple() + "[SCENARIO FORCE] Simulation d'un vol" + colors.getReset());
        
        Station targetStation = findStationForTheft(stations);
        
        if (targetStation == null) {
            System.out.println("  " + colors.getRed() + "Impossible d'isoler un vélo (pas assez de vélos)" + colors.getReset());
            System.out.println();
            return;
        }
        
        List<VehiculeTransfer> transfers = isolateSingleBike(targetStation, stations);
        
        if (transfers.isEmpty()) {
            System.out.println("  " + colors.getRed() + "Impossible d'isoler un vélo (pas assez d'espace)" + colors.getReset());
        } else {
            System.out.println("  " + colors.getPurple() + "Station " + targetStation.getId() + 
                " : isolation d'un vélo (" + transfers.size() + " vélos déplacés)" + colors.getReset());
            
            for (VehiculeTransfer t : transfers) {
                System.out.println("   --> Vélo #" + t.vehiculeId + " : Station " + 
                    t.fromStation + " --> Station " + t.toStation);
            }
        }
        
        System.out.println();
    }
    

    public void forceRepair(List<Station> stations, int cycle) {
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
    

    public void forceRedistribution(List<Station> stations, int cycle) {
        System.out.println("  " + colors.getOrange() + "[SCENARIO FORCE] Simulation d'une redistribution" + colors.getReset());
        
        int redistributionNumber = (cycle - 20) / 21;
        boolean shouldEmpty = redistributionNumber % 2 == 0;
        
        if (shouldEmpty) {
            emptyStation(stations);
        } else {
            fillStation(stations);
        }
        
        System.out.println();
    }
    
    
    private Station findStationForTheft(List<Station> stations) {
        for (Station s : stations) {
            if (s.getNbOccupiedSlot() >= 2) {
                return s;
            }
        }
        return null;
    }
    
    private List<VehiculeTransfer> isolateSingleBike(Station targetStation, List<Station> allStations) {
        List<VehiculeTransfer> transfers = new ArrayList<>();
        List<Vehicule> toMove = new ArrayList<>();
        
        while (targetStation.getNbOccupiedSlot() > 1) {
            Vehicule v = targetStation.removeVehiculeForRedistribution();
            if (v != null) {
                toMove.add(v);
            } else {
                break;
            }
        }
        
        for (Vehicule v : toMove) {
            boolean placed = false;
            for (Station s : allStations) {
                if (s.getId() != targetStation.getId() && !s.isFull()) {
                    s.parkVehicule(v);
                    transfers.add(new VehiculeTransfer(v.getId(), targetStation.getId(), s.getId()));
                    placed = true;
                    break;
                }
            }
            if (!placed) {
                targetStation.parkVehicule(v);
                System.err.println("  " + colors.getRed() + "Attention : Vélo #" + v.getId() + 
                    " n'a pas pu être déplacé" + colors.getReset());
            }
        }
        
        return transfers;
    }
    
    private void emptyStation(List<Station> stations) {
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
    }
    
    private void fillStation(List<Station> stations) {
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
        
        int needed = targetStation.getCapacity() - targetStation.getNbOccupiedSlot();
        List<Vehicule> toMove = new ArrayList<>();
        
        for (Station s : stations) {
            if (s.getId() != targetStation.getId() && toMove.size() < needed) {
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
        
        if (toMove.size() < needed) {
            for (Station s : stations) {
                if (s.getId() != targetStation.getId() && toMove.size() < needed) {
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
        
        for (Vehicule v : toMove) {
            if (!targetStation.isFull()) {
                targetStation.parkVehicule(v);
            }
        }
        
        if (targetStation.isFull()) {
            System.out.println("  " + colors.getOrange() + "Station " + targetStation.getId() + 
                " remplie complètement (" + toMove.size() + " vélos ajoutés)" + colors.getReset());
        } else {
            System.out.println("  " + colors.getOrange() + "Station " + targetStation.getId() + 
                " remplie partiellement (" + toMove.size() + " vélos ajoutés, capacité insuffisante)" + colors.getReset());
        }
    }
    
    public static class VehiculeTransfer {
        public final int vehiculeId;
        public final int fromStation;
        public final int toStation;
        
        public VehiculeTransfer(int vehiculeId, int fromStation, int toStation) {
            this.vehiculeId = vehiculeId;
            this.fromStation = fromStation;
            this.toStation = toStation;
        }
    }
}