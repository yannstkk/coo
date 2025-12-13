package control;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import exceptions.CannotParkException;

/**
 * Responsabilité : Générer les actions aléatoires des utilisateurs
 * (locations et retours de vélos)
 */
public class ActionGenerator {
    
    private final Random random = new Random();
    

    public List<String> generateActions(List<User> users, List<Station> stations) {
        int numActions = random.nextInt(5) + 1;
        
        Set<User> alreadyActed = new HashSet<>();
        Set<Integer> alreadyUsedVehicleIds = new HashSet<>();
        List<String> actions = new ArrayList<>();

        for (int i = 0; i < numActions; i++) {
            List<User> availableUsers = users.stream()
                    .filter(u -> !alreadyActed.contains(u))
                    .collect(Collectors.toList());

            if (availableUsers.isEmpty()) {
                break;
            }

            User u = availableUsers.get(random.nextInt(availableUsers.size()));
            alreadyActed.add(u);

            Station s = stations.get(random.nextInt(stations.size()));

            if (u.getRentedVehicule() == null) {
                try {
                    String action = u.rent(s, alreadyUsedVehicleIds);
                    if (action != null) {
                        actions.add(action);
                        if (u.getRentedVehicule() != null) {
                            alreadyUsedVehicleIds.add(u.getRentedVehicule().getId());
                        }
                    }
                } catch (IllegalStateException | CannotParkException e) {
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
                }
            }
        }
        
        return actions;
    }
}