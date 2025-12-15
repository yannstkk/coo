import control.Simulation;
import control.strategy.Distribution;
import control.strategy.RandomDistribution;
import control.strategy.RoundRobin;
import exceptions.CannotParkException;
import exceptions.IllegalStateException;

public class App {
    public static void main(String[] args) throws CannotParkException {

        if (args.length == 0) {
            System.err.println("Veuillez indiquer la stratégie en premier paramètre : round robin ou random");
            return;
        }

        Distribution strategy;
        String strategyArg = args[0].toLowerCase();

        switch (strategyArg) {
            case "roundrobin" -> strategy = new RoundRobin();
            case "random" -> strategy = new RandomDistribution();
            default -> {
                System.err.println("erreur de veuillez reessayer, Choisissez roundrobin ou random !");
                return;
            }
        }

        Simulation simulation = new Simulation(strategy);
        try {
            simulation.runSimulation();
        } catch (CannotParkException | IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
