package control;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import control.strategy.Distribution;
import control.strategy.RoundRobin;
import vehicle.*;
import vehicle.accessory.Basket;

/**
 * Responsabilité unique : Orchestrer la simulation du système Vélib

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

    public Simulation() {
        this(new RoundRobin());
    }

    public Simulation(Distribution distributionStrategy) {
        initializeStations();
        initializeUsers();
        
        controlCenter = new ControlCenter(stations, distributionStrategy);
        redistributionManager = new RedistributionManager(distributionStrategy);
        
        display.printInitializationInfo(stations, users.size());
    }


    public void runSimulation() {
        int cycle = 1;
        
        while (true) {
            processCycle(cycle);
            cycle++;
            pause();
        }
    }
    

    private void processCycle(int cycle) {
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
            System.err.println(" Erreur lors d'un scénario forcé : " + e.getMessage());
            e.printStackTrace();
        }
    }
    

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
    
 
    private void incrementStationCounters() {
        for (Station st : stations) {
            st.incrementEmptyFullCounters();
        }
    }
    

    private void handleAutomaticRedistribution() {
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
    
   
    private void pause() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    
    private void initializeStations() {
        for (int i = 1; i <= 3; i++) {
            Station station = new Station(i, random.nextInt(11) + 10);
            addVehiculesToStation(station);
            stations.add(station);
        }
    }
    
    private void addVehiculesToStation(Station station) {
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
    
    private void initializeUsers() {
        for (int i = 0; i < 10; i++) {
            User u = new User("Nom num " + i, "prenom num " + i, 100.0);
            users.add(u);
        }
    }
}