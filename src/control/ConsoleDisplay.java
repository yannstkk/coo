package control;

import java.util.List;

/**
 * Responsabilité : Gérer tout l'affichage console
 * (headers, status, barres de progression, messages formatés)
 */
public class ConsoleDisplay {
    
    private final Colors colors = new Colors();
    
    public void printHeader(String title) {
        System.out.println("\n" + colors.getYellow() + "╔════════════════════════════════════════════════════════════╗" + colors.getReset());
        System.out.println(colors.getYellow() + "║  " + String.format("%-56s", title) + "  ║" + colors.getReset());
        System.out.println(colors.getYellow() + "╚════════════════════════════════════════════════════════════╝" + colors.getReset());
    }
    
    public void printCycleHeader(int cycle) {
        System.out.println("\n" + colors.getYellow() + "┌────────────────────────────────────────────────────────────┐" + colors.getReset());
        System.out.println(colors.getYellow() + "│  CYCLE " + String.format("%-49s", cycle) + "  │" + colors.getReset());
        System.out.println(colors.getYellow() + "└────────────────────────────────────────────────────────────┘" + colors.getReset());
        System.out.println();
    }
    
    public void printCycleFooter(int cycle) {
        System.out.println(colors.getBlue() + "  ────────────────────────────────────────────────────────────" + colors.getReset());
    }
    
    public void printInitializationInfo(List<Station> stations, int userCount) {
        printHeader("INITIALISATION DU SYSTEME");
        System.out.println();
        for (Station s : stations) {
            System.out.println(colors.getBlue() + "  Station " + s.getId() + " : " + 
                s.getNbOccupiedSlot() + "/" + s.getCapacity() + " vélos" + colors.getReset());
        }
        System.out.println("\n  " + colors.getGreen() + userCount + " utilisateurs enregistrés" + colors.getReset());
        System.out.println();
    }
    
    public void printActionsInfo(List<String> actions) {
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
    }
    
    public void printStationsStatus(List<Station> stations) {
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
    
    public void printStationsStatusCompact(List<Station> stations) {
        for (Station s : stations) {
            int occupied = s.getNbOccupiedSlot();
            int capacity = s.getCapacity();
            String bar = generateBar(occupied, capacity);
            System.out.println("    Station " + s.getId() + " : " + bar + " " + occupied + "/" + capacity);
        }
        System.out.println();
    }
    
    public void printRedistributionHeader(List<Integer> stationIds) {
        System.out.println("  " + colors.getOrange() + "Redistribution automatique : Stations " + 
            stationIds + colors.getReset());
    }
    
    public void printRedistributionStatus() {
        System.out.println("  " + colors.getBlue() + "État après redistribution :" + colors.getReset());
    }
    
    public void printMessages(List<String> messages) {
        for (String msg : messages) {
            System.out.println("  " + msg);
        }
        if (!messages.isEmpty()) {
            System.out.println();
        }
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