package control;

import java.util.ArrayList;
import java.util.List;

import control.strategy.Slot;
import exceptions.CannotParkException;
import vehicle.Vehicule;
import vehicle.state.ParkedState;

/**
 * Responsabilité : Gérer les scénarios forcés
 * (vols, réparations forcées, redistributions forcées)
 */
public class ScenarioManager {

    private final Colors colors = new Colors();

    /**
     * Forces a theft scenario by isolating a single bike at a station
     * 
     * @param stations the list of stations
     * @param cycle    the current cycle number
     * @throws CannotParkException if vehicles cannot be parked during theft
     *                             scenario
     */
    public void forceTheft(List<Station> stations, int cycle) throws CannotParkException {
        System.out.println("  " + colors.getPurple() + "[SCENARIO FORCE] Simulation d'un vol" + colors.getReset());

        Station targetStation = findStationForTheft(stations);

        if (targetStation == null) {
            System.out.println(
                    "  " + colors.getRed() + "Impossible d'isoler un vélo (pas assez de vélos)" + colors.getReset());
            System.out.println();
            return;
        }

        List<VehiculeTransfer> transfers = isolateSingleBike(targetStation, stations);

        if (transfers.isEmpty()) {
            System.out.println(
                    "  " + colors.getRed() + "Impossible d'isoler un vélo (pas assez d'espace)" + colors.getReset());
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

    /**
     * Forces a repair scenario by setting a vehicle's usage count to 6
     * 
     * @param stations the list of stations
     * @param cycle    the current cycle number
     */
    public void forceRepair(List<Station> stations, int cycle) {
        System.out
                .println("  " + colors.getCyan() + "[SCENARIO FORCE] Simulation d'une réparation" + colors.getReset());

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
            if (targetVehicule != null)
                break;
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

    /**
     * Forces a redistribution scenario by alternating between emptying and filling
     * stations
     * 
     * @param stations the list of stations
     * @param cycle    the current cycle number
     * @throws CannotParkException if vehicles cannot be parked during
     *                             redistribution
     */
    public void forceRedistribution(List<Station> stations, int cycle) throws CannotParkException {
        System.out.println(
                "  " + colors.getOrange() + "[SCENARIO FORCE] Simulation d'une redistribution" + colors.getReset());

        int redistributionNumber = (cycle - 20) / 21;
        boolean shouldEmpty = redistributionNumber % 2 == 0;

        if (shouldEmpty) {
            emptyStation(stations);
        } else {
            fillStation(stations);
        }

        System.out.println();
    }

    /**
     * Finds a station suitable for theft scenario (at least 2 bikes)
     * 
     * @param stations the list of stations
     * @return a station suitable for theft, or null if none found
     */
    private Station findStationForTheft(List<Station> stations) {
        for (Station s : stations) {
            if (s.getNbOccupiedSlot() >= 2) {
                return s;
            }
        }
        return null;
    }

    /**
     * Isolates a single bike at a station by moving all other bikes to other
     * stations
     * 
     * @param targetStation the station to isolate a bike from
     * @param allStations   the list of all stations
     * @return list of vehicle transfers performed
     * @throws CannotParkException if vehicles cannot be parked
     */
    private List<VehiculeTransfer> isolateSingleBike(Station targetStation, List<Station> allStations)
            throws CannotParkException {
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

    /**
     * Empties a station by moving all its vehicles to other stations
     * 
     * @param stations the list of stations
     * @throws CannotParkException if vehicles cannot be parked
     */
    private void emptyStation(List<Station> stations) throws CannotParkException {
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

    /**
     * Fills a station by moving vehicles from other stations to it
     * 
     * @param stations the list of stations
     * @throws CannotParkException if vehicles cannot be parked
     */
    private void fillStation(List<Station> stations) throws CannotParkException {
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
                    " remplie partiellement (" + toMove.size() + " vélos ajoutés, capacité insuffisante)"
                    + colors.getReset());
        }
    }

    public static class VehiculeTransfer {
        public final int vehiculeId;
        public final int fromStation;
        public final int toStation;

        /**
         * Creates a record of a vehicle transfer between stations
         * 
         * @param vehiculeId  the ID of the transferred vehicle
         * @param fromStation the source station ID
         * @param toStation   the destination station ID
         */
        public VehiculeTransfer(int vehiculeId, int fromStation, int toStation) {
            this.vehiculeId = vehiculeId;
            this.fromStation = fromStation;
            this.toStation = toStation;
        }
    }
}