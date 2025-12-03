import yani.*;


public class App {
    public static void main(String[] args) {

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
        simulation.runSimulation();
    }
}
