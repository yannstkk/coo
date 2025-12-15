package control;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import control.strategy.Distribution;
import control.strategy.RoundRobin;
import exceptions.CannotParkException;
import exceptions.IllegalStateException;
import vehicle.*;
import vehicle.accessory.Basket;

/**
 * Responsabilité unique : Orchestrer la simulation du système Vélib
 * 
 */
public class Simulation {

    private final List<Station> stations = new ArrayList<>();
    private final List<User> users = new ArrayList<>();
    private final ControlCenter controlCenter;
    private final Random random = new Random();

    private final ActionGenerator actionGenerator = new ActionGenerator();
    private final ConsoleDisplay display = new ConsoleDisplay();
    private final ScenarioManager scenarioManager = new ScenarioManager();
    private final RepairManager repairManager = new RepairManager();
    private final RedistributionManager redistributionManager;

    /**
     * Creates a simulation with default RoundRobin distribution strategy
     * 
     * @throws CannotParkException if initial vehicles cannot be parked
     */
    public Simulation() throws CannotParkException {
        this(new RoundRobin());
    }

    /**
     * Creates a simulation with the specified distribution strategy
     * 
     * @param distributionStrategy the distribution strategy to use
     * @throws CannotParkException if initial vehicles cannot be parked
     */
    public Simulation(Distribution distributionStrategy) throws CannotParkException {
        initializeStations();
        initializeUsers();

        controlCenter = new ControlCenter(stations, distributionStrategy);
        redistributionManager = new RedistributionManager(distributionStrategy);

        display.printInitializationInfo(stations, users.size());
    }

    /**
     * Runs the simulation in an infinite loop, processing cycles continuously
     * 
     * @throws IllegalStateException if an illegal state occurs during simulation
     * @throws CannotParkException   if a vehicle cannot be parked
     */
    public void runSimulation() throws IllegalStateException, CannotParkException {
        int cycle = 1;

        while (true) {
            processCycle(cycle);
            cycle++;
            pause();
        }
    }

    /**
     * Processes a single simulation cycle including actions, scenarios, and
     * redistribution
     * 
     * @param cycle the current cycle number
     * @throws IllegalStateException if an illegal state occurs
     * @throws CannotParkException   if a vehicle cannot be parked
     */
    private void processCycle(int cycle) throws IllegalStateException, CannotParkException {
        display.printCycleHeader(cycle);

        List<String> actions = actionGenerator.generateActions(users, stations);
        display.printActionsInfo(actions);

        executeScheduledScenarios(cycle);

        display.printStationsStatus(stations);

        boolean theftOccurred = checkAndDisplayThefts();

        incrementStationCounters();

        handleAutomaticRedistribution();

        List<String> repairMessages = repairManager.processRepairs(stations);
        display.printMessages(repairMessages);

        display.printCycleFooter(cycle);
    }

    /**
     * Executes scheduled scenarios (theft, repair, redistribution) based on cycle
     * number
     * 
     * @param cycle the current cycle number
     */
    private void executeScheduledScenarios(int cycle) {
        try {
            if (cycle >= 6 && (cycle - 6) % 21 == 0) {
                scenarioManager.forceTheft(stations, cycle);
            }

            if (cycle >= 13 && (cycle - 13) % 21 == 0) {
                scenarioManager.forceRepair(stations, cycle);
            }

            if (cycle >= 20 && (cycle - 20) % 21 == 0) {
                scenarioManager.forceRedistribution(stations, cycle);
            }
        } catch (Exception e) {
            System.err.println(" erreur lors d'un scenario forcé : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Checks for thefts at all stations and displays theft messages if any occurred
     * 
     * @return true if a theft occurred, false otherwise
     */
    private boolean checkAndDisplayThefts() {
        List<String> theftMessages = new ArrayList<>();

        for (Station st : stations) {
            String msg = st.verifyStolen();
            if (msg != null) {
                theftMessages.add(msg);
            }
        }

        if (!theftMessages.isEmpty()) {
            display.printMessages(theftMessages);

            display.printStationsStatus(stations);

            return true;
        }

        return false;
    }

    /**
     * Increments the empty and full interval counters for all stations
     */
    private void incrementStationCounters() {
        for (Station st : stations) {
            st.incrementEmptyFullCounters();
        }
    }

    /**
     * Handles automatic redistribution if any station needs it
     * 
     * @throws CannotParkException if a vehicle cannot be parked during
     *                             redistribution
     */
    private void handleAutomaticRedistribution() throws CannotParkException {
        List<Integer> stationIds = redistributionManager.getRedistributionStationIds(stations);

        if (!stationIds.isEmpty()) {
            display.printRedistributionHeader(stationIds);
            boolean redistributed = redistributionManager.checkAndRedistribute(stations);
            System.out.println();

            if (redistributed) {
                display.printRedistributionStatus();
                display.printStationsStatusCompact(stations);
            }
        }
    }

    /**
     * Pauses the simulation for 1000 milliseconds between cycles
     */
    private void pause() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes 3 stations with random capacities and vehicles
     * 
     * @throws CannotParkException if vehicles cannot be parked in stations
     */
    private void initializeStations() throws CannotParkException {
        for (int i = 1; i <= 3; i++) {
            Station station = new Station(i, random.nextInt(11) + 10);
            addVehiculesToStation(station);
            stations.add(station);
        }
    }

    /**
     * Adds a random number of vehicles to a station with optional basket decoration
     * 
     * @param station the station to add vehicles to
     * @throws CannotParkException if vehicles cannot be parked
     */
    private void addVehiculesToStation(Station station) throws CannotParkException {
        int nbVehicules = random.nextInt(6) + 5;

        for (int i = 0; i < nbVehicules; i++) {
            double basePrice = 10.0;
            Vehicule v = new ClassicBicycle(basePrice);

            if (random.nextBoolean()) {
                v = new Basket(v);
            }

            station.parkVehicule(v);
        }
    }

    /**
     * Initializes 10 users with default names and balance
     */
    private void initializeUsers() {
        for (int i = 0; i < 10; i++) {
            User u = new User("Nom num " + i, "prenom num " + i, 100.0);
            users.add(u);
        }
    }
}